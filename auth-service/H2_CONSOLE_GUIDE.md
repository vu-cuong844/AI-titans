# 🗄️ HƯỚNG DẪN TRUY CẬP H2 CONSOLE

## ✅ **ĐÃ SỬA XONG CẤU HÌNH SECURITY**

Cấu hình security đã được sửa để hỗ trợ H2 console. Không còn deprecated warnings.

---

## 🚀 **CÁCH TRUY CẬP H2 CONSOLE**

### **1. Khởi động ứng dụng:**
```bash
# Sử dụng Maven wrapper
mvn spring-boot:run

# Hoặc nếu có Maven global
mvn spring-boot:run
```

### **2. Truy cập H2 Console:**
- **URL:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** `password`

### **3. Kiểm tra dữ liệu:**
```sql
-- Xem tất cả users
SELECT * FROM mb_users;

-- Xem cấu trúc bảng
DESCRIBE mb_users;

-- Đếm số users
SELECT COUNT(*) FROM mb_users;
```

---

## 📊 **DỮ LIỆU TEST CÓ SẴN**

Sau khi khởi động, bạn sẽ thấy 5 users trong bảng `mb_users`:

| ID | Username | Full Name | Email | Department | Role | Is Active |
|----|----------|-----------|-------|------------|------|-----------|
| 1 | admin | Administrator | admin@fcsm.com | IT | ADMIN | true |
| 2 | manager1 | Manager One | manager1@fcsm.com | HR | MANAGER | true |
| 3 | employee1 | Employee One | employee1@fcsm.com | IT | EMPLOYEE | true |
| 4 | employee2 | Employee Two | employee2@fcsm.com | HR | EMPLOYEE | true |
| 5 | employee3 | Employee Three | employee3@fcsm.com | Finance | EMPLOYEE | true |

---

## 🔧 **CẤU HÌNH SECURITY HIỆN TẠI**

### **Endpoints không cần authentication:**
- `/api/auth/login` - Đăng nhập
- `/api/auth/refresh` - Refresh token
- `/h2-console/**` - H2 Database Console
- `/swagger-ui/**` - Swagger UI
- `/actuator/**` - Health checks

### **Endpoints cần authentication:**
- `/api/auth/profile` - Xem profile
- `/api/auth/logout` - Đăng xuất
- `/api/auth/users` - Danh sách users
- `/api/auth/users/department/{dept}` - Users theo department

---

## 🧪 **TEST QUICK**

### **1. Test H2 Console:**
1. Truy cập: `http://localhost:8081/h2-console`
2. Đăng nhập với thông tin trên
3. Chạy query: `SELECT * FROM mb_users;`

### **2. Test API Login:**
```bash
# PowerShell
$response = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -Body '{"username":"admin","password":"password"}' -ContentType "application/json"
$response | ConvertTo-Json
```

### **3. Test Swagger UI:**
- Truy cập: `http://localhost:8081/swagger-ui.html`
- Test endpoint `/api/auth/login`

---

## ⚠️ **LƯU Ý QUAN TRỌNG**

1. **Database Type:** H2 in-memory database
2. **Data Persistence:** Dữ liệu sẽ mất khi restart ứng dụng
3. **Test Data:** Được load từ `src/main/resources/data.sql`
4. **Security:** H2 console chỉ accessible trong development

---

## 🎯 **KẾT QUẢ MONG ĐỢI**

- ✅ H2 Console accessible tại `http://localhost:8081/h2-console`
- ✅ 5 test users có sẵn trong database
- ✅ API endpoints hoạt động bình thường
- ✅ Swagger UI accessible tại `http://localhost:8081/swagger-ui.html`
- ✅ Không có deprecated warnings

**Bây giờ bạn có thể truy cập H2 console và kiểm tra dữ liệu!** 🎉
