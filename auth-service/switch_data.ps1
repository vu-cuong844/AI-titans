# ============================================
# SCRIPT CHUYỂN ĐỔI DỮ LIỆU TEST (PowerShell)
# ============================================
# Usage: .\switch_data.ps1 [basic|happy|unhappy|special]

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("basic", "happy", "unhappy", "special")]
    [string]$DataType
)

$dataPath = "src/main/resources"
$backupPath = "$dataPath/data.sql.backup"
$targetPath = "$dataPath/data.sql"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "SWITCHING TEST DATA" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

# Backup current file
if (Test-Path $targetPath) {
    Copy-Item $targetPath $backupPath -Force
    Write-Host "✅ Backed up current data.sql" -ForegroundColor Green
} else {
    Write-Host "⚠️  No existing data.sql found" -ForegroundColor Yellow
}

# Switch to target data
switch ($DataType) {
    "basic" { 
        Copy-Item "$dataPath/data.sql" $targetPath -Force
        Write-Host "🔄 Switched to BASIC data (5 users, happy path only)" -ForegroundColor Blue
    }
    "happy" { 
        Copy-Item "$dataPath/data_happy_path.sql" $targetPath -Force
        Write-Host "🔄 Switched to HAPPY PATH data (Test Case 1)" -ForegroundColor Blue
    }
    "unhappy" { 
        Copy-Item "$dataPath/data_unhappy_path.sql" $targetPath -Force
        Write-Host "🔄 Switched to UNHAPPY PATH data (Test Case 2)" -ForegroundColor Blue
    }
    "special" { 
        Copy-Item "$dataPath/data_special_cases.sql" $targetPath -Force
        Write-Host "🔄 Switched to SPECIAL CASES data (Edge cases)" -ForegroundColor Blue
    }
}

Write-Host ""
Write-Host "📋 Next steps:" -ForegroundColor Yellow
Write-Host "1. Restart the application: .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host "2. Run tests: .\test_scripts.ps1" -ForegroundColor White
Write-Host "3. Or test manually with Swagger UI: http://localhost:8081/my-api-docs.html" -ForegroundColor White
Write-Host ""
Write-Host "💡 To restore backup: Copy-Item '$backupPath' '$targetPath' -Force" -ForegroundColor Gray
Write-Host "============================================" -ForegroundColor Cyan
