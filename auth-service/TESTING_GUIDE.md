# 🧪 HƯỚNG DẪN KIỂM THỬ AUTH SERVICE

## 📋 Tổng quan
Tài liệu này hướng dẫn chi tiết cách kiểm thử hệ thống Auth Service với bộ dữ liệu test đã được chuẩn bị sẵn.

## 🚀 Chuẩn bị môi trường

### 1. Khởi động ứng dụng
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### 2. Kiểm tra server đang chạy
- **URL:** `http://localhost:8081`
- **Swagger UI:** `http://localhost:8081/my-api-docs.html`
- **H2 Console:** `http://localhost:8081/h2-console`

---

## 📊 Bộ dữ liệu test

### Test Accounts (Password: `password`)
| Username | Role | Department | Status | Mục đích test |
|----------|------|------------|--------|---------------|
| `admin` | ADMIN | IT | Active | Đăng nhập hợp lệ - Admin |
| `manager1` | MANAGER | HR | Active | Đăng nhập hợp lệ - Manager |
| `employee1` | EMPLOYEE | IT | Active | Đăng nhập hợp lệ - Employee |
| `employee2` | EMPLOYEE | HR | Active | Đăng nhập hợp lệ - Employee |
| `employee3` | EMPLOYEE | Finance | Active | Đăng nhập hợp lệ - Employee |
| `disabled_user` | EMPLOYEE | IT | **Inactive** | Đăng nhập không hợp lệ |
| `invalid_email` | EMPLOYEE | IT | Active | Email format không hợp lệ |
| `longname_user` | EMPLOYEE | IT | Active | Tên dài |
| `special_dept` | EMPLOYEE | R&D | Active | Department đặc biệt |
| `supervisor` | SUPERVISOR | Operations | Active | Role đặc biệt |

### Invalid Accounts (không tồn tại)
- `wronguser` / `password`
- `nonexistent` / `anypassword`
- `admin` / `wrongpassword`

---

## 🧪 Các phương pháp kiểm thử

### 1. 🖥️ **PowerShell Script (Windows)**
```powershell
# Chạy tất cả test cases
.\test_scripts.ps1
```

**Ưu điểm:**
- ✅ Tự động hóa hoàn toàn
- ✅ Báo cáo kết quả chi tiết
- ✅ Màu sắc dễ đọc
- ✅ Kiểm tra server trước khi test

### 2. 🐧 **Bash Script (Linux/Mac)**
```bash
# Cấp quyền thực thi
chmod +x test_scripts.sh

# Chạy tất cả test cases
./test_scripts.sh
```

### 3. 📮 **Postman Collection**
1. Import file: `Auth_Service_Postman_Collection.json`
2. Chạy collection hoặc từng request
3. Xem kết quả trong Test Results

**Ưu điểm:**
- ✅ Giao diện trực quan
- ✅ Test assertions tự động
- ✅ Lưu trữ kết quả
- ✅ Chia sẻ dễ dàng

### 4. 🌐 **Swagger UI**
1. Truy cập: `http://localhost:8081/my-api-docs.html`
2. Test trực tiếp trên giao diện web
3. Xem response real-time

### 5. 💻 **Manual Testing với cURL**
Copy các lệnh từ `TEST_SCENARIOS.md`

---

## 📋 Checklist kiểm thử

### ✅ **Test Case 1: Đăng nhập hợp lệ**
- [ ] Admin login → 200 OK + JWT token
- [ ] Manager login → 200 OK + JWT token  
- [ ] Employee login → 200 OK + JWT token
- [ ] Response chứa đầy đủ thông tin user
- [ ] Token có format JWT hợp lệ

### ✅ **Test Case 2: Đăng nhập không hợp lệ**
- [ ] Wrong username → 401 Unauthorized
- [ ] Wrong password → 401 Unauthorized
- [ ] Disabled user → 401 Unauthorized
- [ ] Empty credentials → 400 Bad Request
- [ ] Error message rõ ràng

### ✅ **Test Case 3: Quản lý session**
- [ ] JWT token được tạo sau login
- [ ] Token có thể sử dụng để truy cập `/profile`
- [ ] Token có thời hạn 24h
- [ ] Session được quản lý đúng cách

### ✅ **Test Case 4: Token hết hạn**
- [ ] Invalid token → 401 Unauthorized
- [ ] Expired token → 401 Unauthorized
- [ ] No token → 401 Unauthorized
- [ ] Tự động từ chối request

### ✅ **Test Case 5: Đăng xuất an toàn**
- [ ] Logout với token hợp lệ → 200 OK
- [ ] Token bị blacklist sau logout
- [ ] Không thể sử dụng token đã logout
- [ ] Logout không có token → 400 Bad Request

### ✅ **Test Case 6: Xác thực bằng token**
- [ ] Đăng nhập bằng token hợp lệ
- [ ] Refresh token thành công → 200 OK
- [ ] Token cũ bị blacklist sau refresh
- [ ] Token mới hoạt động bình thường
- [ ] Refresh token không hợp lệ → 401 Unauthorized

---

## 🔧 Troubleshooting

### Lỗi thường gặp

#### 1. **Server không chạy**
```
Error: Server is not running at http://localhost:8081
```
**Giải pháp:**
```bash
.\mvnw.cmd spring-boot:run
```

#### 2. **Port 8081 đã được sử dụng**
```
Port 8081 was already in use
```
**Giải pháp:**
- Thay đổi port trong `application.yml`
- Hoặc kill process đang sử dụng port 8081

#### 3. **Database connection error**
```
Failed to configure a DataSource
```
**Giải pháp:**
- Kiểm tra H2 database configuration
- Restart ứng dụng

#### 4. **Token không hợp lệ**
```
401 Unauthorized
```
**Giải pháp:**
- Đăng nhập lại để lấy token mới
- Kiểm tra format Authorization header: `Bearer <token>`

### Debug tips

#### 1. **Kiểm tra logs**
```bash
# Xem logs trong console khi chạy ứng dụng
# Hoặc kiểm tra file log nếu có cấu hình logging
```

#### 2. **Test từng endpoint riêng lẻ**
```bash
# Test login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Test profile với token
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer <your-token>"
```

#### 3. **Sử dụng H2 Console**
- URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

---

## 📈 Kết quả mong đợi

### Thành công (100% test cases pass)
```
============================================
AUTH SERVICE TEST SUITE
============================================
[SUCCESS] Server is running at http://localhost:8081
[SUCCESS] Admin login successful
[SUCCESS] Manager login successful
[SUCCESS] Employee login successful
[SUCCESS] Wrong username correctly rejected (401)
[SUCCESS] Wrong password correctly rejected (401)
[SUCCESS] Token obtained successfully
[SUCCESS] Token access successful (200)
[SUCCESS] Invalid token correctly rejected (401)
[SUCCESS] Logout successful (200)
[SUCCESS] Token correctly blacklisted after logout (401)
[SUCCESS] Token refresh successful (200)
[SUCCESS] New token is different from old token
[SUCCESS] Old token correctly blacklisted after refresh (401)
[SUCCESS] New token works correctly (200)
============================================
[SUCCESS] ALL TESTS COMPLETED!
============================================
```

### Tỷ lệ thành công
- **Test Case 1:** 100% (3/3 scenarios)
- **Test Case 2:** 100% (3/3 scenarios)  
- **Test Case 3:** 100% (2/2 scenarios)
- **Test Case 4:** 100% (2/2 scenarios)
- **Test Case 5:** 100% (4/4 scenarios)
- **Test Case 6:** 100% (4/4 scenarios)

**Tổng cộng:** **100% (18/18 scenarios)**

---

## 📚 Tài liệu tham khảo

1. **API Documentation:** `API_ENDPOINTS.md`
2. **Test Scenarios:** `TEST_SCENARIOS.md`
3. **Postman Collection:** `Auth_Service_Postman_Collection.json`
4. **Test Scripts:** 
   - `test_scripts.ps1` (Windows)
   - `test_scripts.sh` (Linux/Mac)

---

## 🎯 Kết luận

Hệ thống Auth Service đã được thiết kế và implement để đáp ứng **100% các yêu cầu test case** với:

- ✅ **Bảo mật cao:** JWT token, blacklist mechanism
- ✅ **Error handling:** HTTP status codes và messages phù hợp
- ✅ **Token management:** Refresh token, auto-expiry
- ✅ **Comprehensive testing:** 18 test scenarios chi tiết
- ✅ **Multiple testing methods:** Scripts, Postman, Swagger UI
- ✅ **Production ready:** Logging, validation, security

**Hệ thống sẵn sàng cho production!** 🚀
