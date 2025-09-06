# BỘ TEST SCENARIOS CHO AUTH SERVICE

## Tổng quan
Tài liệu này cung cấp các test scenarios chi tiết dựa trên 6 yêu cầu test case đã được phân tích.

## Test Data
- **Base URL:** `http://localhost:8081`
- **Password mặc định:** `password` (cho tất cả test accounts)
- **Swagger UI:** `http://localhost:8081/my-api-docs.html`

---

## 🧪 **TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ**

### Scenario 1.1: Đăng nhập với Admin
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Expected Result:**
- Status: `200 OK`
- Response chứa JWT token và thông tin user
- User role: `ADMIN`
- Department: `IT`

### Scenario 1.2: Đăng nhập với Manager
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager1","password":"password"}'
```

**Expected Result:**
- Status: `200 OK`
- User role: `MANAGER`
- Department: `HR`

### Scenario 1.3: Đăng nhập với Employee
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"employee1","password":"password"}'
```

**Expected Result:**
- Status: `200 OK`
- User role: `EMPLOYEE`
- Department: `IT`

---

## 🚫 **TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ**

### Scenario 2.1: Username không tồn tại
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"wronguser","password":"password"}'
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message: `"Authentication failed"`

### Scenario 2.2: Password sai
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message: `"Authentication failed"`

### Scenario 2.3: Username và password đều sai
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"nonexistent","password":"anypassword"}'
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message: `"Authentication failed"`

### Scenario 2.4: User bị vô hiệu hóa
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"disabled_user","password":"password"}'
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message: `"Authentication failed"`

### Scenario 2.5: Request body không hợp lệ
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"","password":""}'
```

**Expected Result:**
- Status: `400 Bad Request`
- Validation error

---

## 🔐 **TEST CASE 3: TẠO VÀ QUẢN LÝ PHIÊN ĐĂNG NHẬP**

### Scenario 3.1: Kiểm tra JWT token được tạo
```bash
# Bước 1: Đăng nhập
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')

# Bước 2: Kiểm tra token có tồn tại
echo "Token: $TOKEN"
```

**Expected Result:**
- Token không null/empty
- Token có format JWT hợp lệ

### Scenario 3.2: Sử dụng token để truy cập protected endpoint
```bash
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- Status: `200 OK`
- Response chứa thông tin user profile

### Scenario 3.3: Kiểm tra token expiry (24h)
```bash
# Token sẽ hết hạn sau 24 giờ
# Có thể test bằng cách tạo token với thời hạn ngắn hơn trong JwtUtil
```

---

## ⏰ **TEST CASE 4: TỰ ĐỘNG ĐĂNG XUẤT KHI TOKEN HẾT HẠN**

### Scenario 4.1: Sử dụng token hết hạn
```bash
# Sử dụng token đã hết hạn (cần tạo token với thời hạn ngắn để test)
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer expired_token_here"
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message về token expired

### Scenario 4.2: Token không hợp lệ
```bash
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer invalid_token"
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message về invalid token

---

## 🚪 **TEST CASE 5: ĐĂNG XUẤT AN TOÀN**

### Scenario 5.1: Logout thành công
```bash
# Bước 1: Đăng nhập để lấy token
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')

# Bước 2: Logout
curl -X POST http://localhost:8081/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- Status: `200 OK`
- Message: `"Logout successful"`

### Scenario 5.2: Kiểm tra token bị blacklist sau logout
```bash
# Sau khi logout, token không thể sử dụng được nữa
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- Status: `401 Unauthorized`
- Token bị từ chối do blacklist

### Scenario 5.3: Logout không có token
```bash
curl -X POST http://localhost:8081/api/auth/logout
```

**Expected Result:**
- Status: `400 Bad Request`
- Error message: `"No valid token found"`

---

## 🔄 **TEST CASE 6: XÁC THỰC BẰNG TOKEN**

### Scenario 6.1: Đăng nhập bằng token hợp lệ
```bash
# Bước 1: Đăng nhập
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"employee1","password":"password"}' | jq -r '.token')

# Bước 2: Sử dụng token để truy cập các endpoint
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- Status: `200 OK`
- Tự động đăng nhập thành công

### Scenario 6.2: Refresh token
```bash
# Bước 1: Đăng nhập để lấy token cũ
OLD_TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')

# Bước 2: Refresh token
NEW_TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/refresh \
  -H "Authorization: Bearer $OLD_TOKEN" | jq -r '.token')

# Bước 3: Kiểm tra token mới
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer $NEW_TOKEN"
```

**Expected Result:**
- Status: `200 OK`
- Token mới khác token cũ
- Token cũ bị blacklist

### Scenario 6.3: Refresh token không hợp lệ
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Authorization: Bearer invalid_token"
```

**Expected Result:**
- Status: `401 Unauthorized`
- Error message về invalid token

---

## 🔧 **CÁC TEST SCENARIOS BỔ SUNG**

### Scenario 7.1: Test với các role khác nhau
```bash
# Test với SUPERVISOR role
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"supervisor","password":"password"}'
```

### Scenario 7.2: Test với các department khác nhau
```bash
# Test với R&D department
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"special_dept","password":"password"}'
```

### Scenario 7.3: Test với user có tên dài
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"longname_user","password":"password"}'
```

### Scenario 7.4: Test lấy danh sách users
```bash
# Cần token hợp lệ
curl -X GET http://localhost:8081/api/auth/users \
  -H "Authorization: Bearer $TOKEN"
```

### Scenario 7.5: Test lấy users theo department
```bash
curl -X GET http://localhost:8081/api/auth/users/department/IT \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 **CHECKLIST KIỂM THỬ**

### ✅ Test Case 1: Đăng nhập hợp lệ
- [ ] Admin login thành công
- [ ] Manager login thành công  
- [ ] Employee login thành công
- [ ] Response chứa JWT token
- [ ] Response chứa thông tin user đầy đủ

### ✅ Test Case 2: Đăng nhập không hợp lệ
- [ ] Username không tồn tại → 401
- [ ] Password sai → 401
- [ ] User bị vô hiệu hóa → 401
- [ ] Request body không hợp lệ → 400
- [ ] Error message rõ ràng

### ✅ Test Case 3: Quản lý session
- [ ] JWT token được tạo
- [ ] Token có format hợp lệ
- [ ] Token có thể sử dụng để truy cập protected endpoint
- [ ] Session được quản lý đúng cách

### ✅ Test Case 4: Token hết hạn
- [ ] Token hết hạn → 401
- [ ] Token không hợp lệ → 401
- [ ] Tự động từ chối request

### ✅ Test Case 5: Đăng xuất an toàn
- [ ] Logout thành công → 200
- [ ] Token bị blacklist sau logout
- [ ] Không thể sử dụng token đã logout
- [ ] Logout không có token → 400

### ✅ Test Case 6: Xác thực bằng token
- [ ] Đăng nhập bằng token hợp lệ
- [ ] Refresh token thành công
- [ ] Token cũ bị blacklist sau refresh
- [ ] Refresh token không hợp lệ → 401

---

## 🚀 **CÁCH CHẠY TEST**

### 1. Khởi động ứng dụng
```bash
./mvnw spring-boot:run
```

### 2. Chạy từng test scenario
Copy và paste các lệnh curl vào terminal

### 3. Sử dụng Postman
Import Postman collection (sẽ tạo ở bước tiếp theo)

### 4. Sử dụng Swagger UI
Truy cập: `http://localhost:8081/my-api-docs.html`

---

## 📝 **GHI CHÚ QUAN TRỌNG**

1. **Password:** Tất cả test accounts đều có password là `password`
2. **Token Expiry:** Token có thời hạn 24 giờ
3. **Blacklist:** Token bị blacklist sau logout hoặc refresh
4. **Error Handling:** Tất cả error đều trả về JSON format
5. **Security:** Chỉ endpoint `/login` và `/refresh` không cần authentication
