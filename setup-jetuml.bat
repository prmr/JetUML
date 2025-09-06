@echo off
echo Starting VcXsrv...
start "" "C:\Program Files\VcXsrv\vcxsrv.exe" :0 -multiwindow -ac -clipboard -wgl

timeout /t 2 /nobreak > nul

echo Starting JetUML Docker container...
docker compose up

echo Shutting down JetUML container...
docker compose down

echo Shutting down VcXsrv...
taskkill /f /im vcxsrv.exe > nul

pause