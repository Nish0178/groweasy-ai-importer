# PowerShell runner for GrowEasy Java Backend
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Starting GrowEasy Java Spring Boot Backend" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan

# Check Java
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java is not installed or not in PATH. Please install JDK 21."
    exit 1
}

$javaVersion = java -version 2>&1 | Out-String
Write-Host "Java Detected:" -ForegroundColor Gray
Write-Host $javaVersion -ForegroundColor DarkGray

# Check Maven in PATH or known location
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    $knownMvn = "C:\Users\dell\Downloads\apache-maven-3.9.16-bin\bin"
    if (Test-Path $knownMvn) {
        $env:PATH = "$knownMvn;$env:PATH"
        Write-Host "Added Maven from $knownMvn to PATH." -ForegroundColor Gray
    }
}

# Ensure GEMINI_API_KEY is in environment
if ([string]::IsNullOrWhiteSpace($env:GEMINI_API_KEY)) {
    $userKey = [System.Environment]::GetEnvironmentVariable("GEMINI_API_KEY", "User")
    if (-not [string]::IsNullOrWhiteSpace($userKey)) {
        $env:GEMINI_API_KEY = $userKey.Trim()
        Write-Host "Loaded GEMINI_API_KEY from User environment." -ForegroundColor Gray
    }
}

if ([string]::IsNullOrWhiteSpace($env:GEMINI_API_KEY)) {
    $candidates = @(
        (Join-Path $PSScriptRoot ".env"),
        (Join-Path $PSScriptRoot "..\.env"),
        (Join-Path $PSScriptRoot "..\backend\.env")
    )
    foreach ($envFile in $candidates) {
        if (Test-Path $envFile) {
            $keyLine = Get-Content $envFile | Where-Object { $_ -match "^GEMINI_API_KEY=" } | Select-Object -First 1
            if ($keyLine) {
                $val = ($keyLine -replace "^GEMINI_API_KEY=", "").Trim()
                if (-not [string]::IsNullOrWhiteSpace($val)) {
                    $env:GEMINI_API_KEY = $val
                    Write-Host "Loaded GEMINI_API_KEY from environment file: $envFile" -ForegroundColor Gray
                    break
                }
            }
        }
    }
}

if ([string]::IsNullOrWhiteSpace($env:GEMINI_API_KEY)) {
    Write-Host "Warning: GEMINI_API_KEY is not set in environment." -ForegroundColor Yellow
} else {
    Write-Host "GEMINI_API_KEY is configured in runtime environment." -ForegroundColor Green
}

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Running with Maven..." -ForegroundColor Green
    mvn clean spring-boot:run
} else {
    Write-Host "Maven command ('mvn') was not found in PATH." -ForegroundColor Yellow
    Write-Host "You can open this project ('backend-java') in IntelliJ IDEA, Eclipse, or VS Code to run it with one click." -ForegroundColor Cyan
    Write-Host "Or install Maven using: winget install Apache.Maven" -ForegroundColor White
}
