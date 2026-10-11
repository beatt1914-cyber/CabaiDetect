@echo off
chcp 65001 >nul
echo ========================================================
echo   CabaiDetect - Menjalankan Aplikasi & Online Tunnel
echo ========================================================
echo.

echo [1/2] Menjalankan Server Spring Boot CabaiDetect...
start "CabaiDetect-Server" java -jar build\libs\cabaidetect-0.0.1-SNAPSHOT.jar

timeout /t 5 >nul

echo [2/2] Membuka Cloudflare Tunnel Online Publik...
start "Cloudflare-Tunnel" "C:\Program Files (x86)\cloudflared\cloudflared.exe" tunnel --url http://localhost:8080

echo.
echo ========================================================
echo   Aplikasi dan Tunnel Cloudflare sedang berjalan!
echo   Buka browser untuk melihat URL trycloudflare.com
echo ========================================================
echo.
pause
