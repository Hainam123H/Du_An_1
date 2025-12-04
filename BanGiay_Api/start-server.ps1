# Script don gian de khoi dong server
# Su dung: .\start-server.ps1

Write-Host "Dang dung server cu (neu co)..."

Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 1

Write-Host ""
Write-Host "Dang khoi dong server moi..."
Write-Host ""

node server.js

