# Script de khoi dong lai server API
# Su dung: .\restart-server.ps1

Write-Host ""
Write-Host "========================================"
Write-Host "  Khoi dong lai Server BanGiay API"
Write-Host "========================================"
Write-Host ""

# Dung server cu
Write-Host "[1/3] Dang dung server cu..."

$port = 3000
$connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue

if ($connections) {
    $connections | ForEach-Object {
        $processId = $_.OwningProcess
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 2
    Write-Host "  OK - Da dung server cu"
} else {
    Write-Host "  Info - Khong co server nao dang chay"
}

Write-Host ""

# Kiem tra files
Write-Host "[2/3] Dang kiem tra files..."

$serverFile = Join-Path $PSScriptRoot "server.js"
if (Test-Path $serverFile) {
    Write-Host "  OK - Tim thay server.js"
} else {
    Write-Host "  ERROR - Khong tim thay server.js"
    exit 1
}

$orderRoutes = Join-Path $PSScriptRoot "routes\order.routes.js"
if (Test-Path $orderRoutes) {
    Write-Host "  OK - Tim thay order.routes.js"
} else {
    Write-Host "  WARNING - Khong tim thay routes/order.routes.js"
}

Write-Host ""

# Khoi dong server moi
Write-Host "[3/3] Dang khoi dong server moi..."
Write-Host ""

Set-Location $PSScriptRoot

Write-Host "Server dang khoi dong..."
Write-Host "De dung server, nhan Ctrl+C"
Write-Host "========================================"
Write-Host ""

node server.js
