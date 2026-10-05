@echo off
title StyleMirror - Production Portal & Cloudflare Edge Tunnel
echo =========================================================================
echo       STYLEMIRROR - AI FASHION E-COMMERCE & VIRTUAL TRY-ON PORTAL        
echo =========================================================================
echo.
echo [1/2] Starting Spring Boot Server (port 8080)...
start "StyleMirror Backend" java -jar target\stylemirror-1.0.0.jar

echo Waiting for backend to initialize...
timeout /t 8 /nobreak >nul

echo [2/2] Launching Cloudflare HTTPS Edge Tunnel...
echo.
echo =========================================================================
echo Look below for your public HTTPS link (e.g. https://xxxx.trycloudflare.com)
echo Share that link to open on any mobile phone, iPhone, Android, or laptop!
echo =========================================================================
echo.
tools\cloudflared.exe tunnel --url http://localhost:8080
pause
