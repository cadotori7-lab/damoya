@echo off
setlocal
cd /d "%~dp0"
set CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0
set JAVA_HOME=C:\Program Files\Java\jdk-17
set TOMCAT_SERVICE=Tomcat9
set WAR_SOURCE=%CD%\target\damoya.war
set WAR_TARGET=%CATALINA_HOME%\webapps\damoya.war
set WAR_TEMP=%CATALINA_HOME%\webapps\damoya.war.deploying
set EXPLODED_TARGET=%CATALINA_HOME%\webapps\damoya

net session >nul 2>&1
if errorlevel 1 (
    echo ADMINISTRATOR REQUIRED - Run deploy.bat as Administrator.
    exit /b 1
)

sc query "%TOMCAT_SERVICE%" >nul 2>&1
if errorlevel 1 (
    echo TOMCAT SERVICE NOT FOUND - %TOMCAT_SERVICE%
    exit /b 1
)

echo Building WAR...
call mvn clean package -q
if errorlevel 1 (
    echo BUILD FAILED - WAR was not created.
    exit /b 1
)

if not exist "%WAR_SOURCE%" (
    echo WAR NOT FOUND - %WAR_SOURCE%
    exit /b 1
)

echo Stopping Tomcat service...
sc query "%TOMCAT_SERVICE%" | find "STOPPED" >nul
if not errorlevel 1 goto tomcat_stopped

sc stop "%TOMCAT_SERVICE%" >nul
if errorlevel 1 (
    echo TOMCAT STOP FAILED - Deployment cancelled.
    exit /b 1
)

for /l %%i in (1,1,30) do (
    sc query "%TOMCAT_SERVICE%" | find "STOPPED" >nul
    if not errorlevel 1 goto tomcat_stopped
    timeout /t 1 /nobreak >nul
)

echo TOMCAT STOP TIMEOUT - Deployment cancelled.
exit /b 1

:tomcat_stopped
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if not errorlevel 1 (
    echo PORT 8080 IS STILL IN USE - Deployment cancelled.
    exit /b 1
)

echo Deploying...
if exist "%WAR_TARGET%" copy /y "%WAR_TARGET%" "%CD%\target\damoya.previous.war" >nul
if exist "%WAR_TEMP%" del /f /q "%WAR_TEMP%"
if exist "%EXPLODED_TARGET%" rd /s /q "%EXPLODED_TARGET%"
if exist "%EXPLODED_TARGET%" (
    echo OLD DEPLOYMENT DIRECTORY COULD NOT BE REMOVED.
    exit /b 1
)

copy /y "%WAR_SOURCE%" "%WAR_TEMP%" >nul
if errorlevel 1 (
    echo WAR COPY FAILED - Tomcat remains stopped.
    exit /b 1
)

move /y "%WAR_TEMP%" "%WAR_TARGET%" >nul
if errorlevel 1 (
    echo WAR MOVE FAILED - Tomcat remains stopped.
    exit /b 1
)

echo Starting Tomcat service...
sc start "%TOMCAT_SERVICE%" >nul
if errorlevel 1 (
    echo TOMCAT START FAILED.
    exit /b 1
)

for /l %%i in (1,1,30) do (
    sc query "%TOMCAT_SERVICE%" | find "RUNNING" >nul
    if not errorlevel 1 goto tomcat_running
    timeout /t 1 /nobreak >nul
)

echo TOMCAT START TIMEOUT.
exit /b 1

:tomcat_running
echo Done! http://localhost:8080/damoya/
endlocal
