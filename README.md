# BÀI TẬP 09: SPRING SECURITY 7 + SPRING BOOT 4 + MAPSTRUCT + THYMELEAF

**Môn học:** Lập trình Web (WEBPR330479)  
**Giảng viên:** ThS. Nguyễn Hữu Trung - Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE)  
**Sinh viên thực hiện:** Đinh Thế Khang (khangdinh23062006-droid)  
**GitHub Repository:** [https://github.com/khangdinh23062006-droid/Baitap9](https://github.com/khangdinh23062006-droid/Baitap9)  

---

## 📂 Cấu trúc tổng thể Repository

Repository gồm 3 dự án độc lập tương ứng với 3 ví dụ yêu cầu trong đề bài:

```text
baitap9/
├── vidu1_login_security/          # Ví dụ 1: Login Spring Security + MapStruct + Thymeleaf (không dùng Dialect)
│   ├── src/main/java/vn/iotstar/
│   │   ├── config/                # EncodingConfig, SecurityConfig, DataInitializer
│   │   ├── controller/            # AuthController, HomeController
│   │   ├── dto/                   # UserDTO, LoginDTO
│   │   ├── entity/                # User, Role
│   │   ├── mapper/                # UserMapper (MapStruct)
│   │   ├── repository/            # UserRepository, RoleRepository
│   │   └── security/              # CustomUserDetailsService
│   ├── src/main/resources/
│   │   ├── templates/             # layouts/layout.html, fragments/header.html, auth/login.html, home.html, dashboard.html
│   │   └── static/css/            # app.css
│   ├── pom.xml
│   └── run.ps1 (Cổng 8081)
│
├── vidu2_custom_login/            # Ví dụ 2: Custom Login Username/Email + Layout Dialect
│   ├── src/main/java/vn/iotstar/
│   │   ├── config/                # SecurityConfig, DataInitializer
│   │   ├── controller/            # AuthController, HomeController
│   │   ├── dto/                   # UserDTO, LoginDTO
│   │   ├── entity/                # User (có images, fullName, username), Role
│   │   ├── mapper/                # UserMapper (MapStruct)
│   │   ├── repository/            # UserRepository, RoleRepository
│   │   ├── security/              # CustomUserDetails, CustomUserDetailsService
│   │   └── service/               # UserService, UserServiceImpl
│   ├── src/main/resources/
│   │   ├── templates/             # layout.html (Layout Dialect), header.html (hiển thị Avatar, fullName), login.html, home.html
│   │   └── static/                # css/app.css, images/user.png, images/avatar-default.png
│   ├── pom.xml
│   └── run.ps1 (Cổng 8082)
│
├── vidu3_full_system/             # Ví dụ 3: Hệ thống hoàn chỉnh Users, Roles, OtpToken, Products
│   ├── src/main/java/vn/iotstar/
│   │   ├── config/                # SecurityConfig, CloudinaryConfig, WebMvcConfig, DataInitializer
│   │   ├── controller/            # AuthController, HomeController, DashboardController, UserController, ProductController
│   │   ├── dto/                   # UserDTO, ProductDTO, RegisterDTO, VerifyOtpDTO, ForgotPasswordDTO, ResetPasswordDTO
│   │   ├── entity/                # User, Role, OtpToken, Product (1 User - n Product)
│   │   ├── mapper/                # UserMapper, ProductMapper (MapStruct)
│   │   ├── repository/            # UserRepository, RoleRepository, OtpTokenRepository, ProductRepository
│   │   ├── security/              # CustomUserDetails, CustomUserDetailsService, CustomAuthenticationSuccessHandler
│   │   └── service/               # AuthService, UserService, ProductService, EmailService, CloudinaryService
│   ├── src/main/resources/
│   │   ├── templates/             # auth/ (login, register, verify-otp, forgot, reset), users/, products/, admin/dashboard.html
│   │   └── static/                # css/app.css, images/
│   ├── pom.xml
│   └── run.ps1 (Cổng 8083)
│
├── .gitignore
└── README.md
```

---

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ:** Java 22 / JDK 22
- **Framework:** Spring Boot 4.1.1
- **Bảo mật:** Spring Security 7.0.9 (Cấu hình `SecurityFilterChain`, `DaoAuthenticationProvider`, `PasswordEncoder` BCrypt)
- **Object Mapping:** MapStruct 1.6.3 (`mapstruct-processor`, `lombok-mapstruct-binding`)
- **Template Engine:** Thymeleaf 3 + `thymeleaf-layout-dialect` + `thymeleaf-extras-springsecurity6`
- **Cơ sở dữ liệu:**
  - Hỗ trợ kết nối Microsoft SQL Server (`mssql-jdbc`)
  - Tích hợp sẵn cơ chế H2 In-File Database tự động chạy mượt mà ngay cả khi môi trường chưa bật dịch vụ SQL Server
- **Email & OTP:** Spring Mail (`JavaMailSender`, Gmail SMTP) và bộ sinh mã 6 số OTP tự động có thời hạn 5 phút (ghi log rõ ràng lên console khi test)
- **Lưu trữ hình ảnh:** Cloudinary Java SDK HTTP5 kết hợp Fallback Local Storage (`/uploads/**`)

---

## 📝 Chi tiết 3 Ví dụ

### 1. Ví dụ 1 (`vidu1_login_security`) - Cổng: `8081`
- **Mục tiêu:** Thực hiện đăng nhập bằng Email và Mật khẩu, hiển thị thông tin người dùng lên Header bằng Thymeleaf cơ bản (không dùng Layout Dialect).
- **Tính năng:**
  - `SecurityConfig` phân quyền: `/dashboard` dành cho `ADMIN`, `/categories/**` và `/products/**` yêu cầu xác thực.
  - Form Login xác thực qua email: `usernameParameter("email")`.
  - `EncodingConfig` cấu hình UTF-8 filter.
  - `DataInitializer` tạo sẵn:
    - **Admin:** `trungnh@hcmute.edu.vn` / `123456`
    - **User:** `user@hcmute.edu.vn` / `123456`

### 2. Ví dụ 2 (`vidu2_custom_login`) - Cổng: `8082`
- **Mục tiêu:** Custom Login cho phép người dùng đăng nhập bằng cả **Username hoặc Email**; hiển thị thông tin ảnh đại diện (`images`) và họ tên (`fullName`) trên Header sử dụng Thymeleaf Layout Dialect.
- **Tính năng:**
  - `CustomUserDetails` lưu giữ đầy đủ thông tin: `id`, `username`, `email`, `fullName`, `images`, `role`.
  - `CustomUserDetailsService` xác thực linh hoạt: `findByUsernameOrEmail(login, login)`.
  - Layout sử dụng Thymeleaf Layout Dialect (`layout:decorate="~{layouts/layout}"`, `layout:fragment="content"`).
  - Header kiểm tra và hiển thị ảnh đại diện, họ tên, vai trò và nút đăng xuất với CSRF token.
  - `DataInitializer` tạo sẵn:
    - **User:** `user01` (hoặc `user01@gmail.com`) / `123456`
    - **Admin:** `admin` (hoặc `admin@hcmute.edu.vn`) / `123456`

### 3. Ví dụ 3 (`vidu3_full_system`) - Cổng: `8083`
- **Mục tiêu:** Hệ thống Quản trị & Mua sắm hoàn chỉnh:
  - **Bảng CSDL:** `Users`, `Roles` (`ROLE_ADMIN`, `ROLE_USER`), `OtpToken`, `Products` (quan hệ 1 User - n Product).
  - **Xác thực & Bảo mật:**
    - **Đăng ký tài khoản (Register):** Nhập thông tin, upload ảnh đại diện, tài khoản ở trạng thái chưa kích hoạt (`enabled=false`), hệ thống sinh mã OTP 6 chữ số gửi qua email.
    - **Xác nhận OTP (`/verify-otp`):** Nhập mã OTP nhận qua email để kích hoạt tài khoản. Có nút gửi lại mã OTP mới.
    - **Đăng nhập (Login):** Đăng nhập bằng Username hoặc Email, lưu thông tin vào `HttpSession` (`session.setAttribute("currentUser", ...)`).
    - **Quên mật khẩu (`/forgot-password` & `/reset-password`):** Gửi mã OTP xác thực qua email để cấp quyền đổi mật khẩu mới.
  - **Nghiệp vụ Quản lý (CRUD):**
    - **CRUD User:** Phân quyền Admin quản trị danh sách người dùng, tìm kiếm theo tên/email/username, phân trang, thêm mới, sửa, xóa, khóa/mở khóa tài khoản.
    - **CRUD Product:** Người dùng đăng sản phẩm, xem chi tiết, sửa/xóa sản phẩm của chính mình; Admin có quyền quản lý toàn bộ sản phẩm.
    - **Upload ảnh:** Tích hợp Cloudinary API với cơ chế tự động Fallback sang thư mục cục bộ (`./uploads/products`) nếu chưa có key Cloudinary.
    - **Tìm kiếm & Phân trang:** Tìm kiếm theo tên sản phẩm, hãng sản xuất, nơi xuất xứ; phân trang động mượt mà.
    - **Thống kê:** Đếm tổng số người dùng, tổng số sản phẩm và số lượng sản phẩm của từng người dùng hiển thị trên Dashboard và Header.
  - **Dữ liệu mẫu khởi tạo sẵn:**
    - **Admin:** `admin` (hoặc `admin@hcmute.edu.vn`) / `123456`
    - **User 1:** `user01` (hoặc `user01@gmail.com`) / `123456` (sở hữu các sản phẩm laptop, bàn phím)
    - **User 2:** `user02` (hoặc `user02@gmail.com`) / `123456` (sở hữu các sản phẩm chuột, màn hình)

---

## 🚀 Hướng dẫn chạy dự án

### Cách 1: Chạy bằng Maven command line

1. **Chạy Ví dụ 1:**
   ```bash
   cd vidu1_login_security
   mvn spring-boot:run
   ```
   Truy cập: [http://localhost:8081](http://localhost:8081)

2. **Chạy Ví dụ 2:**
   ```bash
   cd vidu2_custom_login
   mvn spring-boot:run
   ```
   Truy cập: [http://localhost:8082](http://localhost:8082)

3. **Chạy Ví dụ 3:**
   ```bash
   cd vidu3_full_system
   mvn spring-boot:run
   ```
   Truy cập: [http://localhost:8083](http://localhost:8083)

### Cách 2: Chạy bằng PowerShell Script có sẵn

Tại mỗi thư mục dự án, chỉ cần chạy file script:
```powershell
.\run.ps1
```

> **Lưu ý kiểm thử OTP:**  
> Khi thực hiện chức năng Đăng ký tài khoản hoặc Quên mật khẩu, mã OTP được sinh ra và ghi rõ trên cửa sổ Console/Terminal của ứng dụng:  
> `[OTP SERVICE] >>> MÃ OTP LÀ: 123456 <<<`  
> Người dùng có thể copy mã này để kích hoạt ngay mà không cần chờ email thật.
