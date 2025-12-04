# Script khởi động lại Server API
# Chạy script này: .\CHẠY_NGAY.ps1

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  KHỞI ĐỘNG LẠI SERVER API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Dừng server cũ
Write-Host "[1/2] Đang dừng server cũ..." -ForegroundColor Yellow
$processes = Get-Process node -ErrorAction SilentlyContinue
if ($processes) {
    $processes | ForEach-Object {
        Write-Host "  - Dừng process PID: $($_.Id)" -ForegroundColor Gray
        Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 1
    Write-Host "  ✓ Đã dừng server cũ" -ForegroundColor Green
} else {
    Write-Host "  ℹ Không có server nào đang chạy" -ForegroundColor Gray
}

Write-Host ""

# Khởi động server mới
Write-Host "[2/2] Đang khởi động server mới..." -ForegroundColor Yellow
Write-Host ""

$apiPath = "E:\du_an_1\BanGiay_Api"
if (-not (Test-Path $apiPath)) {
    Write-Host "✗ Không tìm thấy thư mục: $apiPath" -ForegroundColor Red
    exit 1
}

Set-Location $apiPath

Write-Host "Server đang khởi động..." -ForegroundColor Cyan
Write-Host "Để dừng server, nhấn Ctrl+C" -ForegroundColor Gray
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Chạy server
node server.js

