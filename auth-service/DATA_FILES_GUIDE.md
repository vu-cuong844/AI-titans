# 📁 HƯỚNG DẪN SỬ DỤNG CÁC FILE DỮ LIỆU TEST

## 📋 Tổng quan
Hệ thống auth-service đã được tách thành các file dữ liệu riêng biệt để dễ dàng test từng trường hợp cụ thể.

## 📂 Các file dữ liệu

### 1. **`data.sql`** - Dữ liệu cơ bản (Mặc định)
- **Mục đích:** Start ứng dụng với dữ liệu cơ bản
- **Nội dung:** Chỉ các user hợp lệ, không có trường hợp lỗi
- **Sử dụng:** Khi muốn start ứng dụng bình thường

**Accounts:**
- `admin` / `password` - ADMIN role
- `manager1` / `password` - MANAGER role  
- `employee1` / `password` - EMPLOYEE role
- `employee2` / `password` - EMPLOYEE role
- `employee3` / `password` - EMPLOYEE role

### 2. **`data_happy_path.sql`** - Test Case 1
- **Mục đích:** Test đăng nhập với thông tin hợp lệ
- **Nội dung:** Các user hợp lệ để test happy path
- **Sử dụng:** Khi muốn test Test Case 1

**Accounts:** (Giống data.sql)
- Tất cả user đều active và hợp lệ
- Test đăng nhập thành công
- Test JWT token generation
- Test session management

### 3. **`data_unhappy_path.sql`** - Test Case 2
- **Mục đích:** Test đăng nhập với thông tin không hợp lệ
- **Nội dung:** Các user có vấn đề để test error handling
- **Sử dụng:** Khi muốn test Test Case 2

**Accounts:**
- `admin` / `password` - HỢP LỆ (để so sánh)
- `disabled_user` / `password` - INACTIVE (không hợp lệ)
- `invalid_email` / `password` - EMAIL KHÔNG HỢP LỆ

**Test scenarios:**
- Đăng nhập với user bị vô hiệu hóa → 401
- Đăng nhập với email không hợp lệ → 401
- Đăng nhập với user không tồn tại → 401
- Đăng nhập với password sai → 401

### 4. **`data_special_cases.sql`** - Edge Cases
- **Mục đích:** Test các trường hợp đặc biệt và boundary conditions
- **Nội dung:** Các user với dữ liệu đặc biệt
- **Sử dụng:** Khi muốn test edge cases

**Accounts:**
- `admin` / `password` - BASIC
- `employee1` / `password` - BASIC
- `longname_user` / `password` - TÊN DÀI
- `special_dept` / `password` - DEPARTMENT ĐẶC BIỆT
- `supervisor` / `password` - ROLE ĐẶC BIỆT
- `special_email` / `password` - EMAIL PHỨC TẠP
- `user123` / `password` - USERNAME CÓ SỐ
- `user-name` / `password` - USERNAME CÓ DẤU GẠCH
- `user.name` / `password` - USERNAME CÓ DẤU CHẤM

---

## 🔄 Cách chuyển đổi giữa các file

### Phương pháp 1: Copy & Paste (Đơn giản)
```bash
# 1. Backup file hiện tại
cp src/main/resources/data.sql src/main/resources/data.sql.backup

# 2. Copy nội dung file muốn test
cp src/main/resources/data_happy_path.sql src/main/resources/data.sql

# 3. Restart ứng dụng
./mvnw spring-boot:run
```

### Phương pháp 2: Script tự động (Nâng cao)
```bash
# Tạo script switch_data.sh
#!/bin/bash
case $1 in
  "basic")
    cp src/main/resources/data.sql src/main/resources/data.sql.backup
    cp src/main/resources/data.sql src/main/resources/data.sql
    echo "Switched to basic data"
    ;;
  "happy")
    cp src/main/resources/data.sql src/main/resources/data.sql.backup
    cp src/main/resources/data_happy_path.sql src/main/resources/data.sql
    echo "Switched to happy path data"
    ;;
  "unhappy")
    cp src/main/resources/data.sql src/main/resources/data.sql.backup
    cp src/main/resources/data_unhappy_path.sql src/main/resources/data.sql
    echo "Switched to unhappy path data"
    ;;
  "special")
    cp src/main/resources/data.sql src/main/resources/data.sql.backup
    cp src/main/resources/data_special_cases.sql src/main/resources/data.sql
    echo "Switched to special cases data"
    ;;
  *)
    echo "Usage: $0 {basic|happy|unhappy|special}"
    ;;
esac
```

### Phương pháp 3: PowerShell Script (Windows)
```powershell
# Tạo script switch_data.ps1
param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("basic", "happy", "unhappy", "special")]
    [string]$DataType
)

$dataPath = "src/main/resources"
$backupPath = "$dataPath/data.sql.backup"
$targetPath = "$dataPath/data.sql"

# Backup current file
Copy-Item $targetPath $backupPath -Force

# Switch to target data
switch ($DataType) {
    "basic" { Copy-Item "$dataPath/data.sql" $targetPath -Force }
    "happy" { Copy-Item "$dataPath/data_happy_path.sql" $targetPath -Force }
    "unhappy" { Copy-Item "$dataPath/data_unhappy_path.sql" $targetPath -Force }
    "special" { Copy-Item "$dataPath/data_special_cases.sql" $targetPath -Force }
}

Write-Host "Switched to $DataType data" -ForegroundColor Green
```

---

## 🧪 Workflow kiểm thử

### 1. **Start ứng dụng lần đầu**
```bash
# Sử dụng data.sql (dữ liệu cơ bản)
./mvnw spring-boot:run
```

### 2. **Test Case 1: Happy Path**
```bash
# Chuyển sang happy path data
cp src/main/resources/data_happy_path.sql src/main/resources/data.sql
# Restart ứng dụng
./mvnw spring-boot:run
# Chạy test
./test_scripts.sh
```

### 3. **Test Case 2: Unhappy Path**
```bash
# Chuyển sang unhappy path data
cp src/main/resources/data_unhappy_path.sql src/main/resources/data.sql
# Restart ứng dụng
./mvnw spring-boot:run
# Chạy test
./test_scripts.sh
```

### 4. **Test Special Cases**
```bash
# Chuyển sang special cases data
cp src/main/resources/data_special_cases.sql src/main/resources/data.sql
# Restart ứng dụng
./mvnw spring-boot:run
# Chạy test
./test_scripts.sh
```

### 5. **Quay về dữ liệu cơ bản**
```bash
# Restore từ backup
cp src/main/resources/data.sql.backup src/main/resources/data.sql
# Hoặc copy lại file gốc
cp src/main/resources/data.sql src/main/resources/data.sql
```

---

## 📊 Bảng so sánh các file

| File | Mục đích | User Count | Happy Path | Unhappy Path | Special Cases |
|------|----------|------------|------------|--------------|---------------|
| `data.sql` | Start app | 5 | ✅ | ❌ | ❌ |
| `data_happy_path.sql` | Test Case 1 | 5 | ✅ | ❌ | ❌ |
| `data_unhappy_path.sql` | Test Case 2 | 3 | 1 | 2 | ❌ |
| `data_special_cases.sql` | Edge Cases | 9 | 2 | ❌ | 7 |

---

## 🚀 Quick Start

### Để start ứng dụng ngay:
```bash
# File data.sql đã sẵn sàng với dữ liệu cơ bản
./mvnw spring-boot:run
```

### Để test toàn bộ:
```bash
# 1. Test happy path
cp src/main/resources/data_happy_path.sql src/main/resources/data.sql
./mvnw spring-boot:run &
sleep 10
./test_scripts.sh

# 2. Test unhappy path  
cp src/main/resources/data_unhappy_path.sql src/main/resources/data.sql
./mvnw spring-boot:run &
sleep 10
./test_scripts.sh

# 3. Test special cases
cp src/main/resources/data_special_cases.sql src/main/resources/data.sql
./mvnw spring-boot:run &
sleep 10
./test_scripts.sh

# 4. Restore basic data
cp src/main/resources/data.sql src/main/resources/data.sql
```

---

## ⚠️ Lưu ý quan trọng

1. **Luôn backup:** Trước khi chuyển đổi, backup file `data.sql` hiện tại
2. **Restart required:** Mỗi lần thay đổi file data.sql cần restart ứng dụng
3. **Database reset:** H2 in-memory database sẽ reset mỗi lần restart
4. **Test isolation:** Mỗi file data tạo môi trường test riêng biệt
5. **Password:** Tất cả test accounts đều có password là `password`

---

## 🎯 Kết luận

Với cấu trúc file dữ liệu này, bạn có thể:
- ✅ Start ứng dụng nhanh chóng với dữ liệu cơ bản
- ✅ Test từng test case riêng biệt
- ✅ Kiểm soát dữ liệu test một cách chính xác
- ✅ Dễ dàng debug và troubleshoot
- ✅ Tạo môi trường test ổn định và có thể tái tạo
