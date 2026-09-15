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

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Running with Maven..." -ForegroundColor Green
    mvn clean spring-boot:run
} else {
    Write-Host "Maven command ('mvn') was not found in PATH." -ForegroundColor Yellow
    Write-Host "You can open this project ('backend-java') in IntelliJ IDEA, Eclipse, or VS Code to run it with one click." -ForegroundColor Cyan
    Write-Host "Or install Maven using: winget install Apache.Maven" -ForegroundColor White
}
