$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    Write-Host "Starting Ví dụ 1: Login Spring Security (Port 8081)..." -ForegroundColor Cyan
    & mvn spring-boot:run
} finally {
    Pop-Location
}
