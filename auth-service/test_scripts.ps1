# ============================================
# BỘ SCRIPT TEST CHO AUTH SERVICE (PowerShell)
# ============================================
# Chạy script này để test tất cả các test cases
# Usage: .\test_scripts.ps1

$BASE_URL = "http://localhost:8081"
$TOKEN = ""
$OLD_TOKEN = ""

# Function to print colored output
function Write-Test {
    param([string]$Message)
    Write-Host "[TEST] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

# Function to check if server is running
function Test-Server {
    Write-Test "Checking if server is running..."
    try {
        $response = Invoke-WebRequest -Uri "$BASE_URL/actuator/health" -Method GET -TimeoutSec 5 -ErrorAction Stop
        Write-Success "Server is running at $BASE_URL"
        return $true
    }
    catch {
        Write-Error "Server is not running at $BASE_URL"
        Write-Warning "Please start the server first: .\mvnw.cmd spring-boot:run"
        return $false
    }
}

# Function to extract token from JSON response
function Get-TokenFromResponse {
    param([string]$Response)
    try {
        $json = $Response | ConvertFrom-Json
        return $json.token
    }
    catch {
        return ""
    }
}

# ============================================
# TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ
# ============================================

function Test-Case1 {
    Write-Test "=== TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ ==="
    
    # Test 1.1: Login Admin
    Write-Test "1.1 - Testing Admin login..."
    try {
        $body = @{
            username = "admin"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            Write-Success "Admin login successful"
            $script:TOKEN = $response.token
            Write-Host "Token: $($TOKEN.Substring(0, [Math]::Min(50, $TOKEN.Length)))..."
        }
    }
    catch {
        Write-Error "Admin login failed: $($_.Exception.Message)"
    }
    
    # Test 1.2: Login Manager
    Write-Test "1.2 - Testing Manager login..."
    try {
        $body = @{
            username = "manager1"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            Write-Success "Manager login successful"
        }
    }
    catch {
        Write-Error "Manager login failed: $($_.Exception.Message)"
    }
    
    # Test 1.3: Login Employee
    Write-Test "1.3 - Testing Employee login..."
    try {
        $body = @{
            username = "employee1"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            Write-Success "Employee login successful"
        }
    }
    catch {
        Write-Error "Employee login failed: $($_.Exception.Message)"
    }
}

# ============================================
# TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ
# ============================================

function Test-Case2 {
    Write-Test "=== TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ ==="
    
    # Test 2.1: Wrong Username
    Write-Test "2.1 - Testing wrong username..."
    try {
        $body = @{
            username = "wronguser"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Error "Wrong username test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Wrong username correctly rejected (401)"
        } else {
            Write-Error "Wrong username test failed. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
    
    # Test 2.2: Wrong Password
    Write-Test "2.2 - Testing wrong password..."
    try {
        $body = @{
            username = "admin"
            password = "wrongpassword"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Error "Wrong password test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Wrong password correctly rejected (401)"
        } else {
            Write-Error "Wrong password test failed. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
    
    # Test 2.3: Disabled User
    Write-Test "2.3 - Testing disabled user..."
    try {
        $body = @{
            username = "disabled_user"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Error "Disabled user test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Disabled user correctly rejected (401)"
        } else {
            Write-Error "Disabled user test failed. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
}

# ============================================
# TEST CASE 3: TẠO VÀ QUẢN LÝ PHIÊN ĐĂNG NHẬP
# ============================================

function Test-Case3 {
    Write-Test "=== TEST CASE 3: TẠO VÀ QUẢN LÝ PHIÊN ĐĂNG NHẬP ==="
    
    # Get a fresh token
    Write-Test "3.1 - Getting fresh token..."
    try {
        $body = @{
            username = "admin"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            Write-Success "Token obtained successfully"
            $script:TOKEN = $response.token
            Write-Host "Token: $($TOKEN.Substring(0, [Math]::Min(50, $TOKEN.Length)))..."
        }
    }
    catch {
        Write-Error "Failed to get token: $($_.Exception.Message)"
        return
    }
    
    # Test 3.2: Use token to access protected endpoint
    Write-Test "3.2 - Testing token access to protected endpoint..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -Headers $headers
        Write-Success "Token access successful (200)"
    }
    catch {
        Write-Error "Token access failed: $($_.Exception.Message)"
    }
}

# ============================================
# TEST CASE 4: TỰ ĐỘNG ĐĂNG XUẤT KHI TOKEN HẾT HẠN
# ============================================

function Test-Case4 {
    Write-Test "=== TEST CASE 4: TỰ ĐỘNG ĐĂNG XUẤT KHI TOKEN HẾT HẠN ==="
    
    # Test 4.1: Invalid Token
    Write-Test "4.1 - Testing invalid token..."
    try {
        $headers = @{
            "Authorization" = "Bearer invalid_token"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -Headers $headers -ErrorAction Stop
        Write-Error "Invalid token test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Invalid token correctly rejected (401)"
        } else {
            Write-Error "Invalid token test failed. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
    
    # Test 4.2: No Token
    Write-Test "4.2 - Testing no token..."
    try {
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -ErrorAction Stop
        Write-Error "No token test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "No token correctly rejected (401)"
        } else {
            Write-Error "No token test failed. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
}

# ============================================
# TEST CASE 5: ĐĂNG XUẤT AN TOÀN
# ============================================

function Test-Case5 {
    Write-Test "=== TEST CASE 5: ĐĂNG XUẤT AN TOÀN ==="
    
    # Get a fresh token for logout test
    Write-Test "5.1 - Getting token for logout test..."
    try {
        $body = @{
            username = "admin"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            $script:TOKEN = $response.token
        } else {
            Write-Error "Failed to get token for logout test"
            return
        }
    }
    catch {
        Write-Error "Failed to get token for logout test: $($_.Exception.Message)"
        return
    }
    
    # Test 5.2: Logout Success
    Write-Test "5.2 - Testing logout..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/logout" -Method POST -Headers $headers
        Write-Success "Logout successful (200)"
    }
    catch {
        Write-Error "Logout failed: $($_.Exception.Message)"
    }
    
    # Test 5.3: Use token after logout
    Write-Test "5.3 - Testing token after logout..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -Headers $headers -ErrorAction Stop
        Write-Error "Token after logout test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Token correctly blacklisted after logout (401)"
        } else {
            Write-Error "Token not blacklisted after logout. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
    
    # Test 5.4: Logout without token
    Write-Test "5.4 - Testing logout without token..."
    try {
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/logout" -Method POST -ErrorAction Stop
        Write-Error "Logout without token test failed - should have returned 400"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 400) {
            Write-Success "Logout without token correctly rejected (400)"
        } else {
            Write-Error "Logout without token test failed. Expected 400, got $($_.Exception.Response.StatusCode)"
        }
    }
}

# ============================================
# TEST CASE 6: XÁC THỰC BẰNG TOKEN
# ============================================

function Test-Case6 {
    Write-Test "=== TEST CASE 6: XÁC THỰC BẰNG TOKEN ==="
    
    # Test 6.1: Login for refresh test
    Write-Test "6.1 - Getting token for refresh test..."
    try {
        $body = @{
            username = "admin"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token) {
            $script:OLD_TOKEN = $response.token
        } else {
            Write-Error "Failed to get token for refresh test"
            return
        }
    }
    catch {
        Write-Error "Failed to get token for refresh test: $($_.Exception.Message)"
        return
    }
    
    # Test 6.2: Refresh Token
    Write-Test "6.2 - Testing token refresh..."
    try {
        $headers = @{
            "Authorization" = "Bearer $OLD_TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/refresh" -Method POST -Headers $headers
        
        if ($response.token) {
            Write-Success "Token refresh successful (200)"
            $script:TOKEN = $response.token
            if ($TOKEN -ne $OLD_TOKEN) {
                Write-Success "New token is different from old token"
            } else {
                Write-Error "New token is same as old token"
            }
        }
    }
    catch {
        Write-Error "Token refresh failed: $($_.Exception.Message)"
    }
    
    # Test 6.3: Use old token after refresh
    Write-Test "6.3 - Testing old token after refresh..."
    try {
        $headers = @{
            "Authorization" = "Bearer $OLD_TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -Headers $headers -ErrorAction Stop
        Write-Error "Old token after refresh test failed - should have returned 401"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            Write-Success "Old token correctly blacklisted after refresh (401)"
        } else {
            Write-Error "Old token not blacklisted after refresh. Expected 401, got $($_.Exception.Response.StatusCode)"
        }
    }
    
    # Test 6.4: Use new token after refresh
    Write-Test "6.4 - Testing new token after refresh..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/profile" -Method GET -Headers $headers
        Write-Success "New token works correctly (200)"
    }
    catch {
        Write-Error "New token not working: $($_.Exception.Message)"
    }
}

# ============================================
# ADDITIONAL TESTS
# ============================================

function Test-Additional {
    Write-Test "=== ADDITIONAL TESTS ==="
    
    # Get a fresh token
    try {
        $body = @{
            username = "admin"
            password = "password"
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" -Method POST -Body $body -ContentType "application/json"
        $script:TOKEN = $response.token
    }
    catch {
        Write-Error "Failed to get token for additional tests"
        return
    }
    
    # Test Get All Users
    Write-Test "Testing get all users..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/users" -Method GET -Headers $headers
        Write-Success "Get all users successful (200)"
    }
    catch {
        Write-Error "Get all users failed: $($_.Exception.Message)"
    }
    
    # Test Get Users by Department
    Write-Test "Testing get users by department..."
    try {
        $headers = @{
            "Authorization" = "Bearer $TOKEN"
        }
        
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/users/department/IT" -Method GET -Headers $headers
        Write-Success "Get users by department successful (200)"
    }
    catch {
        Write-Error "Get users by department failed: $($_.Exception.Message)"
    }
}

# ============================================
# MAIN EXECUTION
# ============================================

function Main {
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "AUTH SERVICE TEST SUITE (PowerShell)" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "Base URL: $BASE_URL" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    
    # Check if server is running
    if (-not (Test-Server)) {
        return
    }
    
    # Run all test cases
    Test-Case1
    Write-Host ""
    Test-Case2
    Write-Host ""
    Test-Case3
    Write-Host ""
    Test-Case4
    Write-Host ""
    Test-Case5
    Write-Host ""
    Test-Case6
    Write-Host ""
    Test-Additional
    
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Success "ALL TESTS COMPLETED!"
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "For detailed testing, use:" -ForegroundColor Yellow
    Write-Host "1. Postman Collection: Auth_Service_Postman_Collection.json" -ForegroundColor Yellow
    Write-Host "2. Swagger UI: $BASE_URL/my-api-docs.html" -ForegroundColor Yellow
    Write-Host "3. Test Scenarios: TEST_SCENARIOS.md" -ForegroundColor Yellow
}

# Run main function
Main
