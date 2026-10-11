@echo off
chcp 65001 >nul
echo ========================================================
echo   CabaiDetect - Otomatisasi Push ke GitHub
echo ========================================================
echo.

set "GH_EXE=C:\Program Files\GitHub CLI\gh.exe"
if not exist "%GH_EXE%" (
    where gh >nul 2>nul
    if %errorlevel% neq 0 (
        echo [ERROR] GitHub CLI tidak ditemukan.
        pause
        exit /b 1
    )
    set "GH_EXE=gh"
)

echo [1/3] Memeriksa status login GitHub...
"%GH_EXE%" auth status >nul 2>nul
if %errorlevel% neq 0 (
    echo [INFO] Anda belum login ke GitHub.
    echo Browser akan dibuka untuk konfirmasi login 1-klik...
    echo.
    "%GH_EXE%" auth login -p https -w
)

echo.
echo [2/3] Membuat repository 'CabaiDetect' di akun GitHub Anda...
"%GH_EXE%" repo create CabaiDetect --public --source=. --remote=origin --push
if %errorlevel% equ 0 (
    echo.
    echo ========================================================
    echo   [SUKSES] Repository berhasil dibuat dan di-push!
    echo ========================================================
) else (
    echo.
    echo [INFO] Jika repository sudah pernah dibuat sebelumnya, mencoba push biasa...
    git push -u origin main
)

echo.
pause
