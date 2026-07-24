@echo off
set "CATALINA_HOME=C:\myProgram\apache-tomcat-9.0.119"
set "CATALINA_BASE=C:\myProgram\apache-tomcat-9.0.119"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"

echo Building WAR...
call mvn clean package -q
if errorlevel 1 (
    echo BUILD FAILED - WAR was not created.
    exit /b 1
)

echo Stopping Tomcat...
taskkill /f /im tomcat9.exe 2>nul
timeout /t 3 /nobreak >nul

echo Killing port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo PID %%a 종료 중...
    taskkill /f /pid %%a 2>nul
)
timeout /t 2 /nobreak >nul

echo Deploying...
del "%CATALINA_HOME%\webapps\ROOT.war" 2>nul
rd /s /q "%CATALINA_HOME%\webapps\ROOT" 2>nul
rd /s /q "%CATALINA_HOME%\work\Catalina\localhost\ROOT" 2>nul

copy /Y "target\spring-board.war" "%CATALINA_HOME%\webapps\ROOT.war"

if errorlevel 1 (
    echo DEPLOY FAILED - WAR copy failed.
    exit /b 1
)

echo Starting Tomcat...
start "" "%CATALINA_HOME%\bin\startup.bat"

echo Done! http://localhost:8080/