@echo off
setlocal

where mvn >nul 2>nul
if %ERRORLEVEL% == 0 (
    mvn %*
    exit /b %ERRORLEVEL%
)

echo Error: Maven executable 'mvn' was not found on PATH. 1>&2
echo Install Maven or restore the full Maven Wrapper files to use mvnw.cmd. 1>&2
exit /b 1
