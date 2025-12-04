# Script để dừng tất cả processes liên quan đến Android Studio và Emulator

Write-Host "Đang dừng các processes..." -ForegroundColor Yellow

# Dừng Java processes (Gradle, Android Studio)
$javaProcesses = Get-Process | Where-Object {$_.ProcessName -like "*java*"}
if ($javaProcesses) {
    Write-Host "Tìm thấy $($javaProcesses.Count) Java processes" -ForegroundColor Cyan
    foreach ($proc in $javaProcesses) {
        try {
            Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
            Write-Host "Đã dừng: $($proc.ProcessName) (PID: $($proc.Id))" -ForegroundColor Green
        } catch {
            Write-Host "Không thể dừng: $($proc.ProcessName)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "Không tìm thấy Java processes" -ForegroundColor Green
}

# Dừng Emulator processes
$emulatorProcesses = Get-Process | Where-Object {$_.ProcessName -like "*emulator*" -or $_.ProcessName -like "*qemu*"}
if ($emulatorProcesses) {
    Write-Host "Tìm thấy $($emulatorProcesses.Count) Emulator processes" -ForegroundColor Cyan
    foreach ($proc in $emulatorProcesses) {
        try {
            Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
            Write-Host "Đã dừng: $($proc.ProcessName) (PID: $($proc.Id))" -ForegroundColor Green
        } catch {
            Write-Host "Không thể dừng: $($proc.ProcessName)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "Không tìm thấy Emulator processes" -ForegroundColor Green
}

# Dừng ADB processes
$adbProcesses = Get-Process | Where-Object {$_.ProcessName -like "*adb*"}
if ($adbProcesses) {
    Write-Host "Tìm thấy $($adbProcesses.Count) ADB processes" -ForegroundColor Cyan
    foreach ($proc in $adbProcesses) {
        try {
            Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
            Write-Host "Đã dừng: $($proc.ProcessName) (PID: $($proc.Id))" -ForegroundColor Green
        } catch {
            Write-Host "Không thể dừng: $($proc.ProcessName)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "Không tìm thấy ADB processes" -ForegroundColor Green
}

Write-Host "`nHoàn tất! Bây giờ bạn có thể:" -ForegroundColor Yellow
Write-Host "1. Đóng Android Studio" -ForegroundColor Cyan
Write-Host "2. Mở lại Android Studio" -ForegroundColor Cyan
Write-Host "3. File -> Invalidate Caches -> Invalidate and Restart" -ForegroundColor Cyan
Write-Host "4. Build -> Clean Project" -ForegroundColor Cyan
Write-Host "5. Build -> Rebuild Project" -ForegroundColor Cyan

