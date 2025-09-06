# Auth Service API Endpoints

## Tổng quan
Hệ thống Auth Service đã được hoàn thiện với đầy đủ các chức năng đăng nhập, đăng xuất, và quản lý token theo yêu cầu.

## Các Endpoint

### 1. Đăng nhập
**POST** `/api/auth/login`

**Request Body:**
```json
{
  "username": "testuser",
  "password": "password"
}
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "testuser",
  "fullName": "Test User",
  "email": "test@company.com",
  "department": "IT",
  "role": "EMPLOYEE"
}
```

**Response Error (401):**
```json
{
  "error": "Authentication failed",
  "message": "Invalid username or password"
}
```

### 2. Đăng xuất
**POST** `/api/auth/logout`

**Headers:**
```
Authorization: Bearer <token>
```

**Response Success (200):**
```json
{
  "message": "Logout successful"
}
```

**Response Error (400):**
```json
{
  "error": "Bad request",
  "message": "No valid token found in Authorization header"
}
```

### 3. Refresh Token
**POST** `/api/auth/refresh`

**Headers:**
```
Authorization: Bearer <old-token>
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "testuser",
  "fullName": "Test User",
  "email": "test@company.com",
  "department": "IT",
  "role": "EMPLOYEE"
}
```

**Response Error (401):**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 4. Lấy thông tin profile
**GET** `/api/auth/profile`

**Headers:**
```
Authorization: Bearer <token>
```

**Response Success (200):**
```json
{
  "id": 1,
  "username": "testuser",
  "fullName": "Test User",
  "email": "test@company.com",
  "department": "IT",
  "role": "EMPLOYEE",
  "isActive": true
}
```

## Các tính năng đã hoàn thiện

### ✅ Đáp ứng đầy đủ yêu cầu test case:

1. **Đăng nhập với thông tin hợp lệ** ✅
   - Endpoint: `POST /api/auth/login`
   - Trả về JWT token và thông tin user
   - Tự động tạo session với JWT

2. **Xử lý thông tin đăng nhập không hợp lệ** ✅
   - Trả về HTTP 401 với message lỗi chi tiết
   - Error handling được cải thiện

3. **Tạo và quản lý phiên đăng nhập** ✅
   - JWT token với thời hạn 24h
   - Session management với STATELESS policy
   - Token validation tự động

4. **Tự động đăng xuất khi token hết hạn** ✅
   - JwtAuthenticationFilter kiểm tra token expiry
   - Tự động từ chối request với token hết hạn
   - Client có thể sử dụng refresh token

5. **Đăng xuất an toàn** ✅
   - Endpoint: `POST /api/auth/logout`
   - Token blacklist mechanism
   - Invalidate token ngay lập tức

6. **Xác thực bằng token** ✅
   - JWT Authentication Filter
   - Bearer token validation
   - Auto-login với token hợp lệ

## Cơ chế bảo mật

### Token Blacklist
- Khi user logout, token được thêm vào blacklist
- JwtAuthenticationFilter kiểm tra blacklist trước khi xác thực
- Token bị blacklist sẽ bị từ chối ngay lập tức

### Refresh Token
- Cho phép gia hạn token mà không cần đăng nhập lại
- Token cũ được blacklist sau khi refresh
- Tăng cường bảo mật với token rotation

### Error Handling
- HTTP status codes phù hợp (401, 400, 500)
- JSON error responses với message chi tiết
- Logging đầy đủ cho debugging

## Cách test

### 1. Đăng nhập
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}'
```

### 2. Sử dụng token để truy cập protected endpoint
```bash
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer <token>"
```

### 3. Refresh token
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Authorization: Bearer <old-token>"
```

### 4. Logout
```bash
curl -X POST http://localhost:8081/api/auth/logout \
  -H "Authorization: Bearer <token>"
```

## Swagger UI
Truy cập Swagger UI tại: `http://localhost:8081/my-api-docs.html`

## Kết luận
Hệ thống Auth Service đã hoàn thiện 100% các yêu cầu test case với:
- ✅ Đăng nhập/đăng xuất an toàn
- ✅ Quản lý session với JWT
- ✅ Token blacklist mechanism
- ✅ Refresh token functionality
- ✅ Error handling chi tiết
- ✅ Bảo mật cao với token rotation
