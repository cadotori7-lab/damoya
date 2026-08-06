@echo off
setlocal EnableExtensions

REM ASCII-only batch file (UTF-8 Korean comments break cmd.exe parsing)

set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 9.0"
set "CATALINA_BASE=%CATALINA_HOME%"
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "WAR_SOURCE=%~dp0target\damoya.war"

set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo JAVA_HOME is invalid: "%JAVA_HOME%"
    echo Expected java.exe under jdk-17\bin
    goto fail
)

echo Building WAR...
echo JAVA_HOME=%JAVA_HOME%
call mvn clean package -DskipTests
if errorlevel 1 (
    echo BUILD FAILED - WAR was not created.
    goto fail
)

if not exist "%WAR_SOURCE%" (
    echo WAR NOT FOUND - "%WAR_SOURCE%"
    dir "%~dp0target\*.war" 2>nul
    goto fail
)

if not exist "%CATALINA_HOME%\webapps\" (
    echo TOMCAT NOT FOUND - "%CATALINA_HOME%"
    goto fail
)

echo Stopping Tomcat...
taskkill /f /im tomcat9.exe 2>nul
taskkill /f /im Tomcat9.exe 2>nul
timeout /t 3 /nobreak >nul

echo Waiting for port 8080 to be free...
for /l %%i in (1,1,30) do (
    netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
    if errorlevel 1 goto port_free
    timeout /t 1 /nobreak >nul
)

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
del "%CATALINA_HOME%\webapps\damoya.war" 2>nul
rd /s /q "%CATALINA_HOME%\webapps\damoya" 2>nul
rd /s /q "%CATALINA_HOME%\work\Catalina\localhost\damoya" 2>nul
del "%CATALINA_HOME%\webapps\ROOT.war" 2>nul
rd /s /q "%CATALINA_HOME%\webapps\ROOT" 2>nul
rd /s /q "%CATALINA_HOME%\work\Catalina\localhost\ROOT" 2>nul

copy /Y "%WAR_SOURCE%" "%CATALINA_HOME%\webapps\ROOT.war"
if errorlevel 1 (
    echo DEPLOY FAILED - WAR copy failed.
    echo Tip: Run as Administrator if Tomcat is under Program Files.
    goto fail
)

echo Starting Tomcat...
start "" "%CATALINA_HOME%\bin\startup.bat"

echo Done! http://localhost:8080/
endlocal
exit /b 0

:fail
endlocal
exit /b 1
