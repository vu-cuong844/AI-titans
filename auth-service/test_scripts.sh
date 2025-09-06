#!/bin/bash

# ============================================
# BỘ SCRIPT TEST CHO AUTH SERVICE
# ============================================
# Chạy script này để test tất cả các test cases
# Usage: ./test_scripts.sh

BASE_URL="http://localhost:8081"
TOKEN=""
OLD_TOKEN=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to check if server is running
check_server() {
    print_test "Checking if server is running..."
    if curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_success "Server is running at $BASE_URL"
    else
        print_error "Server is not running at $BASE_URL"
        print_warning "Please start the server first: ./mvnw spring-boot:run"
        exit 1
    fi
}

# Function to extract token from response
extract_token() {
    echo "$1" | grep -o '"token":"[^"]*"' | cut -d'"' -f4
}

# ============================================
# TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ
# ============================================

test_case_1() {
    print_test "=== TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ ==="
    
    # Test 1.1: Login Admin
    print_test "1.1 - Testing Admin login..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password"}')
    
    if echo "$response" | grep -q '"token"'; then
        print_success "Admin login successful"
        TOKEN=$(extract_token "$response")
        echo "Token: ${TOKEN:0:50}..."
    else
        print_error "Admin login failed"
        echo "Response: $response"
    fi
    
    # Test 1.2: Login Manager
    print_test "1.2 - Testing Manager login..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"manager1","password":"password"}')
    
    if echo "$response" | grep -q '"token"'; then
        print_success "Manager login successful"
    else
        print_error "Manager login failed"
        echo "Response: $response"
    fi
    
    # Test 1.3: Login Employee
    print_test "1.3 - Testing Employee login..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"employee1","password":"password"}')
    
    if echo "$response" | grep -q '"token"'; then
        print_success "Employee login successful"
    else
        print_error "Employee login failed"
        echo "Response: $response"
    fi
}

# ============================================
# TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ
# ============================================

test_case_2() {
    print_test "=== TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ ==="
    
    # Test 2.1: Wrong Username
    print_test "2.1 - Testing wrong username..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"wronguser","password":"password"}')
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Wrong username correctly rejected (401)"
    else
        print_error "Wrong username test failed. Expected 401, got $http_code"
    fi
    
    # Test 2.2: Wrong Password
    print_test "2.2 - Testing wrong password..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"wrongpassword"}')
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Wrong password correctly rejected (401)"
    else
        print_error "Wrong password test failed. Expected 401, got $http_code"
    fi
    
    # Test 2.3: Disabled User
    print_test "2.3 - Testing disabled user..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"disabled_user","password":"password"}')
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Disabled user correctly rejected (401)"
    else
        print_error "Disabled user test failed. Expected 401, got $http_code"
    fi
}

# ============================================
# TEST CASE 3: TẠO VÀ QUẢN LÝ PHIÊN ĐĂNG NHẬP
# ============================================

test_case_3() {
    print_test "=== TEST CASE 3: TẠO VÀ QUẢN LÝ PHIÊN ĐĂNG NHẬP ==="
    
    # Get a fresh token
    print_test "3.1 - Getting fresh token..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password"}')
    
    TOKEN=$(extract_token "$response")
    if [ -n "$TOKEN" ]; then
        print_success "Token obtained successfully"
        echo "Token: ${TOKEN:0:50}..."
    else
        print_error "Failed to get token"
        return 1
    fi
    
    # Test 3.2: Use token to access protected endpoint
    print_test "3.2 - Testing token access to protected endpoint..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "Token access successful (200)"
    else
        print_error "Token access failed. Expected 200, got $http_code"
    fi
}

# ============================================
# TEST CASE 4: TỰ ĐỘNG ĐĂNG XUẤT KHI TOKEN HẾT HẠN
# ============================================

test_case_4() {
    print_test "=== TEST CASE 4: TỰ ĐỘNG ĐĂNG XUẤT KHI TOKEN HẾT HẠN ==="
    
    # Test 4.1: Invalid Token
    print_test "4.1 - Testing invalid token..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer invalid_token")
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Invalid token correctly rejected (401)"
    else
        print_error "Invalid token test failed. Expected 401, got $http_code"
    fi
    
    # Test 4.2: No Token
    print_test "4.2 - Testing no token..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile")
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "No token correctly rejected (401)"
    else
        print_error "No token test failed. Expected 401, got $http_code"
    fi
}

# ============================================
# TEST CASE 5: ĐĂNG XUẤT AN TOÀN
# ============================================

test_case_5() {
    print_test "=== TEST CASE 5: ĐĂNG XUẤT AN TOÀN ==="
    
    # Get a fresh token for logout test
    print_test "5.1 - Getting token for logout test..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password"}')
    
    TOKEN=$(extract_token "$response")
    if [ -z "$TOKEN" ]; then
        print_error "Failed to get token for logout test"
        return 1
    fi
    
    # Test 5.2: Logout Success
    print_test "5.2 - Testing logout..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/logout" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "Logout successful (200)"
    else
        print_error "Logout failed. Expected 200, got $http_code"
    fi
    
    # Test 5.3: Use token after logout
    print_test "5.3 - Testing token after logout..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Token correctly blacklisted after logout (401)"
    else
        print_error "Token not blacklisted after logout. Expected 401, got $http_code"
    fi
    
    # Test 5.4: Logout without token
    print_test "5.4 - Testing logout without token..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/logout")
    
    http_code="${response: -3}"
    if [ "$http_code" = "400" ]; then
        print_success "Logout without token correctly rejected (400)"
    else
        print_error "Logout without token test failed. Expected 400, got $http_code"
    fi
}

# ============================================
# TEST CASE 6: XÁC THỰC BẰNG TOKEN
# ============================================

test_case_6() {
    print_test "=== TEST CASE 6: XÁC THỰC BẰNG TOKEN ==="
    
    # Test 6.1: Login for refresh test
    print_test "6.1 - Getting token for refresh test..."
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password"}')
    
    OLD_TOKEN=$(extract_token "$response")
    if [ -z "$OLD_TOKEN" ]; then
        print_error "Failed to get token for refresh test"
        return 1
    fi
    
    # Test 6.2: Refresh Token
    print_test "6.2 - Testing token refresh..."
    response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/auth/refresh" \
        -H "Authorization: Bearer $OLD_TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "Token refresh successful (200)"
        TOKEN=$(extract_token "$response")
        if [ "$TOKEN" != "$OLD_TOKEN" ]; then
            print_success "New token is different from old token"
        else
            print_error "New token is same as old token"
        fi
    else
        print_error "Token refresh failed. Expected 200, got $http_code"
    fi
    
    # Test 6.3: Use old token after refresh
    print_test "6.3 - Testing old token after refresh..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer $OLD_TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "401" ]; then
        print_success "Old token correctly blacklisted after refresh (401)"
    else
        print_error "Old token not blacklisted after refresh. Expected 401, got $http_code"
    fi
    
    # Test 6.4: Use new token after refresh
    print_test "6.4 - Testing new token after refresh..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "New token works correctly (200)"
    else
        print_error "New token not working. Expected 200, got $http_code"
    fi
}

# ============================================
# ADDITIONAL TESTS
# ============================================

additional_tests() {
    print_test "=== ADDITIONAL TESTS ==="
    
    # Get a fresh token
    response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"password"}')
    
    TOKEN=$(extract_token "$response")
    
    # Test Get All Users
    print_test "Testing get all users..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/users" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "Get all users successful (200)"
    else
        print_error "Get all users failed. Expected 200, got $http_code"
    fi
    
    # Test Get Users by Department
    print_test "Testing get users by department..."
    response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/api/auth/users/department/IT" \
        -H "Authorization: Bearer $TOKEN")
    
    http_code="${response: -3}"
    if [ "$http_code" = "200" ]; then
        print_success "Get users by department successful (200)"
    else
        print_error "Get users by department failed. Expected 200, got $http_code"
    fi
}

# ============================================
# MAIN EXECUTION
# ============================================

main() {
    echo "============================================"
    echo "AUTH SERVICE TEST SUITE"
    echo "============================================"
    echo "Base URL: $BASE_URL"
    echo "============================================"
    
    # Check if server is running
    check_server
    
    # Run all test cases
    test_case_1
    echo ""
    test_case_2
    echo ""
    test_case_3
    echo ""
    test_case_4
    echo ""
    test_case_5
    echo ""
    test_case_6
    echo ""
    additional_tests
    
    echo ""
    echo "============================================"
    print_success "ALL TESTS COMPLETED!"
    echo "============================================"
    echo ""
    echo "For detailed testing, use:"
    echo "1. Postman Collection: Auth_Service_Postman_Collection.json"
    echo "2. Swagger UI: $BASE_URL/my-api-docs.html"
    echo "3. Test Scenarios: TEST_SCENARIOS.md"
}

# Run main function
main "$@"
