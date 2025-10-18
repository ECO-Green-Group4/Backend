# EV Trade - Hệ thống giao dịch xe điện

## Tổng quan
EV Trade là một nền tảng giao dịch xe điện và pin xe điện, cho phép người dùng đăng bán, mua sắm và quản lý các giao dịch một cách an toàn và minh bạch.

## Luồng đăng xe và bán xe

### 1. Đăng ký tài khoản

#### Đăng ký người dùng thường
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller123",
    "password": "Password123!",
    "confirmPassword": "Password123!",
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-05-15",
    "sex": "Male",
    "identityCard": "123456789012",
    "email": "seller@example.com",
    "address": "123 Đường ABC, Quận 1, TP.HCM",
    "phoneNumber": "0123456789"
  }'
```

#### Đăng ký admin
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123",
    "confirmPassword": "Admin@123",
    "fullName": "Admin User",
    "dateOfBirth": "1985-01-01",
    "sex": "Male",
    "identityCard": "987654321098",
    "email": "admin@evtrade.com",
    "address": "456 Đường XYZ, Quận 2, TP.HCM",
    "phoneNumber": "0987654321"
  }'
```

### 2. Đăng nhập

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller123",
    "password": "Password123!"
  }'
```

**Response:**
```json
{
  "message": "Login successful",
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": 1,
      "username": "seller123",
      "email": "seller@example.com",
      "fullName": "Nguyễn Văn A",
      "role": "member"
    }
  }
}
```

### 3. Đăng bán xe điện

#### Tạo bài đăng xe điện
```bash
curl -X POST "http://localhost:8080/api/listing/vehicle" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "title": "Tesla Model 3 2022",
    "description": "Xe Tesla Model 3 đời 2022, tình trạng tốt, đã đi 15,000km",
    "images": [
      "https://example.com/tesla1.jpg",
      "https://example.com/tesla2.jpg",
      "https://example.com/tesla3.jpg"
    ],
    "location": "Hồ Chí Minh",
    "price": 1500000000,
    "brand": "Tesla",
    "model": "Model 3",
    "year": 2022,
    "bodyType": "Sedan",
    "color": "White",
    "mileage": 15000,
    "inspection": "Yes",
    "origin": "USA",
    "numberOfSeats": 5,
    "licensePlate": "51F-123.45",
    "accessories": "Helmet, charger, floor mats",
    "batteryCapacity": 75,
    "condition": "excellent",
    "postType": "For Sale"
  }'
```

**Response:**
```json
{
  "message": "Vehicle listing created successfully",
  "success": true,
  "data": {
    "listingId": 1,
    "title": "Tesla Model 3 2022",
    "status": "DRAFT",
    "createdAt": "2025-01-16T10:30:00"
  }
}
```

#### Tạo bài đăng pin xe điện
```bash
curl -X POST "http://localhost:8080/api/listing/battery" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "title": "Pin Tesla Model 3 75kWh",
    "description": "Pin Tesla Model 3 75kWh, tình trạng tốt, đã sử dụng 2 năm",
    "images": [
      "https://example.com/battery1.jpg",
      "https://example.com/battery2.jpg"
    ],
    "location": "Hồ Chí Minh",
    "price": 50000000,
    "brand": "Tesla",
    "model": "Model 3",
    "year": 2022,
    "batteryBrand": "Tesla",
    "voltage": "400V",
    "capacity": "75kWh",
    "healthPercent": 85,
    "chargeCycles": 500,
    "type": "Lithium-ion",
    "manufactureYear": 2022,
    "origin": "USA",
    "postType": "For Sale"
  }'
```

### 4. Admin duyệt bài đăng

#### Admin đăng nhập
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

#### Xem tất cả bài đăng chờ duyệt
```bash
curl -X GET "http://localhost:8080/api/admin/listings" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```

#### Duyệt bài đăng (chấp nhận)
```bash
curl -X PUT "http://localhost:8080/api/admin/listings/{listingId}/status" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```


### 5. Khách hàng mua xe

#### Đăng ký tài khoản khách hàng
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer456",
    "password": "Password123!",
    "confirmPassword": "Password123!",
    "fullName": "Trần Thị B",
    "dateOfBirth": "1995-08-20",
    "sex": "Female",
    "identityCard": "987654321098",
    "email": "buyer@example.com",
    "address": "789 Đường DEF, Quận 3, TP.HCM",
    "phoneNumber": "0987654321"
  }'
```

#### Xem danh sách xe đang bán
```bash
curl -X GET "http://localhost:8080/api/listing/vehicles" \
  -H "Authorization: Bearer <BUYER_JWT_TOKEN>"
```

#### Xem chi tiết xe
```bash
curl -X GET "http://localhost:8080/api/listing/1" \
  -H "Authorization: Bearer <BUYER_JWT_TOKEN>"
```

#### Tạo đơn hàng
```bash
curl -X POST "http://localhost:8080/api/buyer/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <BUYER_JWT_TOKEN>" \
  -d '{
    "listingId": 1,
    "basePrice": 1500000000
  }'
```

**Response:**
```json
{
  "message": "Order created successfully",
  "success": true,
  "data": {
    "orderId": 1,
    "status": "PENDING",
    "totalAmount": 1500000000,
    "createdAt": "2025-01-16T11:00:00"
  }
}
```

### 6. Admin quản lý đơn hàng

#### Xem tất cả đơn hàng
```bash
curl -X GET "http://localhost:8080/api/admin/orders" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```

#### Gán staff cho đơn hàng
```bash
curl -X PUT "http://localhost:8080/api/admin/orders/1/assign-staff?staffId=2" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```

### 7. Staff xử lý đơn hàng

#### Staff đăng nhập
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "staff789",
    "password": "Password123!"
  }'
```

#### Xem đơn hàng được gán
```bash
curl -X GET "http://localhost:8080/api/staff/orders" \
  -H "Authorization: Bearer <STAFF_JWT_TOKEN>"
```

#### Xem chi tiết đơn hàng
```bash
curl -X GET "http://localhost:8080/api/staff/orders/1" \
  -H "Authorization: Bearer <STAFF_JWT_TOKEN>"
```

### 8. Tạo hợp đồng

#### Staff tạo hợp đồng
```bash
curl -X POST "http://localhost:8080/api/contract" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <STAFF_JWT_TOKEN>" \
  -d '{
    "orderId": 1,
    "contractTerms": "Hợp đồng mua bán xe điện Tesla Model 3",
    "deliveryDate": "2025-02-01",
    "deliveryLocation": "123 Đường ABC, Quận 1, TP.HCM",
    "paymentTerms": "Thanh toán 50% trước, 50% khi nhận xe",
    "warrantyPeriod": "12 tháng",
    "additionalTerms": "Bảo hành pin 8 năm hoặc 160,000km"
  }'
```



### 10. Quản lý dịch vụ add-on

#### Admin tạo dịch vụ add-on
```bash
curl -X POST "http://localhost:8080/api/admin/addon/services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -d '{
    "serviceName": "Kiểm định xe",
    "description": "Dịch vụ kiểm định xe điện",
    "price": 2000000,
    "duration": "2-3 ngày",
    "status": "ACTIVE"
  }'
```

#### Admin tạo gói dịch vụ
```bash
curl -X POST "http://localhost:8080/api/admin/service-packages" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -d '{
    "packageName": "Gói Premium",
    "description": "Gói dịch vụ cao cấp",
    "price": 5000000,
    "duration": "30 ngày",
    "maxListings": 10,
    "status": "ACTIVE"
  }'
```

## Cấu trúc API

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### Listing APIs
- `POST /api/listing/vehicle` - Đăng bán xe điện
- `POST /api/listing/battery` - Đăng bán pin xe điện
- `GET /api/listing/vehicles` - Xem danh sách xe
- `GET /api/listing/batteries` - Xem danh sách pin
- `GET /api/listing/{id}` - Xem chi tiết bài đăng

### Order APIs
- `POST /api/buyer/orders` - Tạo đơn hàng
- `GET /api/buyer/orders` - Xem đơn hàng của buyer
- `GET /api/seller/orders` - Xem đơn hàng của seller

### Admin APIs
- `GET /api/admin/listings` - Xem tất cả bài đăng
- `PUT /api/admin/listings/{id}/approve` - Duyệt bài đăng
- `PUT /api/admin/listings/{id}/reject` - Từ chối bài đăng
- `GET /api/admin/orders` - Xem tất cả đơn hàng
- `PUT /api/admin/orders/{id}/assign-staff` - Gán staff cho đơn hàng
- `GET /api/admin/users` - Xem tất cả người dùng
- `PUT /api/admin/users/{id}/role` - Set role cho user

### Staff APIs
- `GET /api/staff/orders` - Xem đơn hàng được gán
- `GET /api/staff/orders/{id}` - Xem chi tiết đơn hàng

### Contract APIs
- `POST /api/contract` - Tạo hợp đồng
- `GET /api/contract/my-contracts` - Xem hợp đồng của tôi



## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Cài đặt
```bash
# Clone repository
git clone <repository-url>
cd ev-trade

# Cài đặt dependencies
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

### Cấu hình database
Cập nhật file `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ev_trade
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Liên hệ
- Email: support@evtrade.com
- Phone: +84 123 456 789
- Website: https://evtrade.com

