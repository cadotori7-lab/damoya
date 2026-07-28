@echo off
set "CATALINA_HOME=C:\apache-tomcat-9.0.118"
set "CATALINA_BASE=C:\apache-tomcat-9.0.118"
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "WAR_SOURCE=target\damoya.war"

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

echo Stopping Tomcat...
taskkill /f /im tomcat9.exe 2>nul
timeout /t 3 /nobreak >nul

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
del "%CATALINA_HOME%\webapps\ROOT.war" 2>nul
rd /s /q "%CATALINA_HOME%\webapps\ROOT" 2>nul
rd /s /q "%CATALINA_HOME%\work\Catalina\localhost\ROOT" 2>nul

copy /Y "%WAR_SOURCE%" "%CATALINA_HOME%\webapps\ROOT.war"

if errorlevel 1 (
    echo DEPLOY FAILED - WAR copy failed.
    goto fail
)

echo Starting Tomcat...
start "" "%CATALINA_HOME%\bin\startup.bat"

echo Done! http://localhost:8080/
exit /b 0

:fail
exit /b 1
