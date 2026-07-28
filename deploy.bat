@echo off
setlocal
cd /d "%~dp0"

:: Re-launch elevated if not already running as Administrator
net session >nul 2>&1
if errorlevel 1 (
    echo Requesting Administrator privileges...
    powershell -NoProfile -Command "Start-Process -FilePath '%~f0' -Verb RunAs -WorkingDirectory '%~dp0'"
    exit /b
)

set CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0
set JAVA_HOME=C:\Program Files\Java\jdk-17
set TOMCAT_SERVICE=Tomcat9
set WAR_SOURCE=%CD%\target\damoya.war
set WAR_TARGET=%CATALINA_HOME%\webapps\damoya.war
set WAR_TEMP=%CATALINA_HOME%\webapps\damoya.war.deploying
set EXPLODED_TARGET=%CATALINA_HOME%\webapps\damoya

sc query "%TOMCAT_SERVICE%" >nul 2>&1
if errorlevel 1 (
    echo TOMCAT SERVICE NOT FOUND - %TOMCAT_SERVICE%
    goto fail
)

echo Building WAR...
call mvn clean package -q
if errorlevel 1 (
    echo BUILD FAILED - WAR was not created.
    goto fail
)

if not exist "%WAR_SOURCE%" (
    echo WAR NOT FOUND - %WAR_SOURCE%
    goto fail
)

echo Stopping Tomcat service...
sc query "%TOMCAT_SERVICE%" | find "STOPPED" >nul
if not errorlevel 1 goto tomcat_stopped

sc stop "%TOMCAT_SERVICE%" >nul
if errorlevel 1 (
    echo TOMCAT STOP FAILED - Deployment cancelled.
    goto fail
)

for /l %%i in (1,1,30) do (
    sc query "%TOMCAT_SERVICE%" | find "STOPPED" >nul
    if not errorlevel 1 goto tomcat_stopped
    timeout /t 1 /nobreak >nul
)

echo TOMCAT STOP TIMEOUT - Deployment cancelled.
goto fail

:tomcat_stopped
echo Waiting for port 8080 to be free...
for /l %%i in (1,1,30) do (
    netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
    if errorlevel 1 goto port_free
    timeout /t 1 /nobreak >nul
)

:: Still occupied - kill leftover listener (common after abrupt Tomcat stop)
echo Port 8080 still in use. Killing leftover process...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%p >nul 2>&1
)
timeout /t 2 /nobreak >nul
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if not errorlevel 1 (
    echo PORT 8080 IS STILL IN USE - Deployment cancelled.
    goto fail
)

:port_free
echo Deploying...
if exist "%WAR_TARGET%" copy /y "%WAR_TARGET%" "%CD%\target\damoya.previous.war" >nul
if exist "%WAR_TEMP%" del /f /q "%WAR_TEMP%"
if exist "%EXPLODED_TARGET%" rd /s /q "%EXPLODED_TARGET%"
if exist "%EXPLODED_TARGET%" (
    echo OLD DEPLOYMENT DIRECTORY COULD NOT BE REMOVED.
    goto fail
)

copy /y "%WAR_SOURCE%" "%WAR_TEMP%" >nul
if errorlevel 1 (
    echo WAR COPY FAILED - Tomcat remains stopped.
    goto fail
)

move /y "%WAR_TEMP%" "%WAR_TARGET%" >nul
if errorlevel 1 (
    echo WAR MOVE FAILED - Tomcat remains stopped.
    goto fail
)

echo Starting Tomcat service...
sc start "%TOMCAT_SERVICE%" >nul
if errorlevel 1 (
    echo TOMCAT START FAILED.
    goto fail
)

for /l %%i in (1,1,30) do (
    sc query "%TOMCAT_SERVICE%" | find "RUNNING" >nul
    if not errorlevel 1 goto tomcat_running
    timeout /t 1 /nobreak >nul
)

echo TOMCAT START TIMEOUT.
goto fail

:tomcat_running
echo Waiting for http://localhost:8080/damoya/ ...
for /l %%i in (1,1,90) do (
    powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/damoya/' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } } catch { }; exit 1" >nul 2>&1
    if not errorlevel 1 goto app_ready
    timeout /t 1 /nobreak >nul
)

echo Tomcat service is RUNNING but the app did not respond in time.
echo Check logs under: "%CATALINA_HOME%\logs"
goto fail

:app_ready
echo Done! http://localhost:8080/damoya/
pause
endlocal
exit /b 0

:fail
pause
endlocal
exit /b 1
