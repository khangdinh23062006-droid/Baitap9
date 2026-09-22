$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    Write-Host "Starting Ví dụ 2: Custom Login (Port 8082)..." -ForegroundColor Cyan
    & mvn spring-boot:run
} finally {
    Pop-Location
}
