$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    Write-Host "Starting Ví dụ 3: Full System (Port 8083)..." -ForegroundColor Cyan
    & mvn spring-boot:run
} finally {
    Pop-Location
}
