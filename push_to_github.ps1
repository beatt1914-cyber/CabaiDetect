Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  CabaiDetect - Otomatisasi Push ke GitHub" -ForegroundColor Cyan
Write-Host "========================================================`n"

$ghExe = "C:\Program Files\GitHub CLI\gh.exe"
if (-not (Test-Path $ghExe)) {
    $ghCommand = Get-Command gh -ErrorAction SilentlyContinue
    if ($ghCommand) {
        $ghExe = "gh"
    } else {
        Write-Host "[ERROR] GitHub CLI tidak ditemukan." -ForegroundColor Red
        return
    }
}

Write-Host "[1/3] Memeriksa status login GitHub..." -ForegroundColor Yellow
$authStatus = & $ghExe auth status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[INFO] Membuka browser untuk login ke GitHub..." -ForegroundColor Yellow
    & $ghExe auth login -p https -w
}

Write-Host "`n[2/3] Membuat repository 'CabaiDetect' di GitHub dan push otomatis..." -ForegroundColor Yellow
& $ghExe repo create CabaiDetect --public --source=. --remote=origin --push

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n========================================================" -ForegroundColor Green
    Write-Host "  [SUKSES] Repository berhasil dibuat dan di-push!" -ForegroundColor Green
    Write-Host "========================================================" -ForegroundColor Green
} else {
    Write-Host "`n[INFO] Mencoba push manual ke origin main..." -ForegroundColor Yellow
    git push -u origin main
}
