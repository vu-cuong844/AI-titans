# Knowledge Sharing Platform

Nền tảng MVP chia sẻ kiến thức nội bộ với kiến trúc microservice, sử dụng Spring Boot và Angular.

## Tổng quan

Hệ thống được thiết kế để giúp nhân viên nội bộ chia sẻ, tìm kiếm và quản lý kiến thức một cách hiệu quả với các tính năng:

- **Tập trung hóa kiến thức** từ các phòng ban
- **Gán nhãn và phân loại** tài liệu
- **Tìm kiếm nhanh** theo nhiều tiêu chí
- **Chia sẻ có kiểm soát** với 3 mức độ: Private, Group, Public
- **Tương tác cộng đồng** với đánh dấu sao và bình luận

## Kiến trúc hệ thống

### Backend Services
- **Auth Service** (Port 8081): Xác thực và phân quyền
- **Document Service** (Port 8082): Quản lý tài liệu

### Frontend
- **Angular Application** (Port 4200): Giao diện người dùng

### Database
- **H2 Database**: Cơ sở dữ liệu trong bộ nhớ (có thể chuyển sang PostgreSQL/MySQL)

## Công nghệ sử dụng

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- H2 Database
- JWT Authentication

### Frontend
- Angular 17
- Angular Material
- TypeScript
- SCSS

### DevOps
- Docker & Docker Compose
- Maven

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Node.js 18+
- Docker & Docker Compose (tùy chọn)
- Maven 3.6+

### Chạy với Docker Compose (Khuyến nghị)

1. Clone repository:
```bash
git clone <repository-url>
cd FCSM
```

2. Chạy tất cả services:
```bash
docker-compose up --build
```

3. Truy cập ứng dụng:
- Frontend: http://localhost:4200
- Auth Service: http://localhost:8081
- Document Service: http://localhost:8082
- H2 Console: http://localhost:8081/h2-console (Auth) hoặc http://localhost:8082/h2-console (Document)

### Chạy thủ công

#### Backend Services

1. Chạy Auth Service:
```bash
cd auth-service
mvn spring-boot:run
```

2. Chạy Document Service:
```bash
cd document-service
mvn spring-boot:run
```

#### Frontend

1. Cài đặt dependencies:
```bash
cd frontend
npm install
```

2. Chạy development server:
```bash
npm start
```

## Tài khoản mặc định

Hệ thống có sẵn các tài khoản test:

| Username | Password | Role | Department |
|----------|----------|------|------------|
| admin | password | ADMIN | IT |
| manager1 | password | MANAGER | HR |
| employee1 | password | EMPLOYEE | IT |
| employee2 | password | EMPLOYEE | HR |
| employee3 | password | EMPLOYEE | Finance |

## Tính năng chính

### Auth Service
- ✅ Đăng nhập/đăng xuất
- ✅ JWT Authentication
- ✅ Phân quyền theo role
- ✅ Quản lý người dùng

### Document Service
- ✅ Upload tài liệu (PDF, DOC, DOCX, JPG, PNG)
- ✅ Quản lý tài liệu với 3 mức độ chia sẻ
- ✅ Tìm kiếm theo tên, tag, ngày tạo
- ✅ Gán nhãn/tags cho tài liệu
- ✅ Đánh dấu sao tài liệu
- ✅ Thống kê lượt xem
- ✅ Phân trang kết quả

### Frontend
- ✅ Giao diện đăng nhập
- ✅ Trang chủ với dashboard
- ✅ Danh sách tài liệu với tìm kiếm
- ✅ Chi tiết tài liệu
- ✅ Upload tài liệu mới
- ✅ Responsive design

## API Endpoints

### Auth Service (Port 8081)
- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/profile` - Lấy thông tin profile
- `GET /api/auth/users` - Danh sách người dùng

### Document Service (Port 8082)
- `GET /api/documents` - Danh sách tài liệu
- `GET /api/documents/{id}` - Chi tiết tài liệu
- `POST /api/documents` - Tạo tài liệu mới
- `POST /api/documents/search` - Tìm kiếm tài liệu
- `GET /api/documents/top/viewed` - Tài liệu xem nhiều nhất
- `GET /api/documents/top/starred` - Tài liệu đánh dấu sao nhiều nhất
- `GET /api/documents/recent` - Tài liệu mới nhất
- `POST /api/documents/{id}/star` - Đánh dấu sao tài liệu

## Cấu trúc dự án

```
FCSM/
├── auth-service/                 # Auth Service
│   ├── src/main/java/com/fcsm/auth/
│   │   ├── controller/          # REST Controllers
│   │   ├── model/              # Entity models
│   │   ├── repository/         # Data repositories
│   │   ├── service/            # Business logic
│   │   ├── security/           # Security configuration
│   │   └── util/               # Utility classes
│   ├── src/main/resources/
│   │   ├── application.yml     # Configuration
│   │   └── data.sql           # Sample data
│   └── pom.xml
├── document-service/            # Document Service
│   ├── src/main/java/com/fcsm/document/
│   │   ├── controller/         # REST Controllers
│   │   ├── model/             # Entity models
│   │   ├── repository/        # Data repositories
│   │   └── service/           # Business logic
│   ├── src/main/resources/
│   │   ├── application.yml    # Configuration
│   │   └── data.sql          # Sample data
│   └── pom.xml
├── frontend/                   # Angular Frontend
│   ├── src/app/
│   │   ├── components/        # Angular components
│   │   ├── services/          # Angular services
│   │   ├── models/            # TypeScript models
│   │   └── guards/            # Route guards
│   ├── src/assets/            # Static assets
│   ├── package.json
│   └── angular.json
├── docker-compose.yml          # Docker configuration
└── README.md
```

## Phát triển thêm

### Tính năng có thể mở rộng
- [ ] Tích hợp AI để tự động tạo tóm tắt và tags
- [ ] Hệ thống bình luận và thảo luận
- [ ] Thông báo real-time
- [ ] Export/Import tài liệu
- [ ] Version control cho tài liệu
- [ ] Tích hợp với hệ thống LDAP/AD
- [ ] Mobile app (React Native/Flutter)

### Cải thiện kỹ thuật
- [ ] Chuyển sang PostgreSQL/MySQL
- [ ] Redis cho caching
- [ ] Elasticsearch cho tìm kiếm nâng cao
- [ ] File storage (AWS S3/MinIO)
- [ ] Monitoring và logging
- [ ] CI/CD pipeline

## Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Liên hệ

Project Link: [https://github.com/your-username/knowledge-sharing-platform](https://github.com/your-username/knowledge-sharing-platform)
