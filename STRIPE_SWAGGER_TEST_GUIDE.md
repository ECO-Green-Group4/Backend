# Hướng Dẫn Test Stripe Payment trên Swagger UI

## 🚀 Bước 1: Khởi Động Application

### Từ IntelliJ IDEA:
1. Mở file `src/main/java/com/evmarket/trade/EvTradeApplication.java`
2. Right-click → **Run 'EvTradeApplication'**
3. Hoặc click nút ▶️ màu xanh ở góc trên

### Từ Eclipse:
1. Right-click vào project
2. **Run As** → **Spring Boot App**

### Từ Command Line (nếu đã fix JAVA_HOME):
```bash
./mvnw.cmd spring-boot:run
```

### Kiểm Tra Application Đã Chạy:
Xem console log, tìm dòng:
```
Tomcat started on port(s): 8080 (http)
Started EvTradeApplication in X.XXX seconds
```

---

## 🌐 Bước 2: Truy Cập Swagger UI

Mở browser và truy cập:
```
http://localhost:8080/swagger-ui.html
```

Hoặc:
```
http://localhost:8080/swagger-ui/index.html
```

**Bạn sẽ thấy giao diện Swagger với nhiều sections:**
- Auth Controller
- Payment Controller
- **Stripe Payment** ← Section này!
- Và nhiều controllers khác...

---

## 💳 Bước 3: Test Stripe Endpoints

### 🔐 Lưu Ý Quan Trọng: Authentication

Hầu hết các endpoints Stripe **YÊU CẦU JWT Token** (trừ webhook).

#### Cách Lấy JWT Token:

1. **Scroll xuống section "Auth Controller"**
2. **Click vào `POST /api/auth/login`**
3. Click nút **"Try it out"**
4. Nhập credentials (thay bằng user có sẵn trong DB):
   ```json
   {
     "username": "admin@evtrade.com",
     "password": "admin123"
   }
   ```
5. Click **"Execute"**
6. Copy **token** từ response
7. **Scroll lên đầu page**
8. Click nút **"Authorize"** 🔓 (góc trên bên phải)
9. Nhập: `Bearer <your_token_here>`
   
   Ví dụ:
   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
10. Click **"Authorize"** → **"Close"**

**Bây giờ tất cả requests sẽ tự động có Authorization header!** ✅

---

## 🧪 Test Case 1: Tạo Payment Intent

**Endpoint:** `POST /api/stripe/create-payment-intent`

**Mục đích:** Tạo Payment Intent để dùng với custom payment form

### Các Bước:

1. **Tìm section "Stripe Payment"** trong Swagger
2. **Click vào `POST /api/stripe/create-payment-intent`**
3. Click **"Try it out"**
4. Nhập Request Body:

```json
{
  "orderId": 1,
  "amount": 100000,
  "description": "Thanh toán đơn hàng #1 - Pin Tesla Model 3",
  "customerEmail": "test@example.com"
}
```

**Giải thích:**
- `orderId`: ID đơn hàng trong hệ thống (số nguyên)
- `amount`: Số tiền VND (tối thiểu 10,000)
- `description`: Mô tả giao dịch
- `customerEmail`: Email nhận receipt từ Stripe

5. Click **"Execute"**

### Response Mong Đợi:

```json
{
  "clientSecret": "pi_xxx_secret_yyy",
  "paymentIntentId": "pi_3Abc123...",
  "publishableKey": "pk_test_51SLP6f3mWDY4eArL...",
  "amount": 100000,
  "currency": "vnd",
  "status": "requires_payment_method",
  "description": "Thanh toán đơn hàng #1 - Pin Tesla Model 3",
  "orderId": 1,
  "message": "Payment Intent created successfully. Use client secret to confirm payment."
}
```

**Giải thích Response:**
- `clientSecret`: Dùng trong frontend để confirm payment
- `paymentIntentId`: ID để tracking payment
- `status`: `requires_payment_method` = chưa có thông tin thẻ

### Copy `clientSecret` để dùng trong frontend!

---

## 🛒 Test Case 2: Tạo Checkout Session

**Endpoint:** `POST /api/stripe/create-checkout-session`

**Mục đích:** Tạo URL redirect đến trang thanh toán Stripe

### Các Bước:

1. **Click vào `POST /api/stripe/create-checkout-session`**
2. Click **"Try it out"**
3. Nhập Request Body:

```json
{
  "orderId": 1,
  "amount": 100000,
  "productName": "Pin xe điện Tesla Model 3",
  "description": "Pin Long Range - 75 kWh - Bảo hành 8 năm",
  "customerEmail": "test@example.com",
  "quantity": 1
}
```

**Giải thích:**
- `productName`: Tên sản phẩm (hiển thị trên Stripe Checkout)
- `quantity`: Số lượng sản phẩm

4. Click **"Execute"**

### Response Mong Đợi:

```json
{
  "sessionId": "cs_test_a1b2c3...",
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_a1b2c3...",
  "publishableKey": "pk_test_51SLP6f3mWDY4eArL...",
  "orderId": 1,
  "amount": 100000,
  "currency": "VND",
  "message": "Checkout session created. Redirect user to checkout URL."
}
```

### Copy `checkoutUrl` và Paste vào Browser:

**Bạn sẽ được redirect đến trang thanh toán Stripe!** 🎉

**Test bằng Test Card:**
```
Card Number: 4242 4242 4242 4242
Expiry: 12/25
CVC: 123
ZIP: 12345
```

---

## 🔍 Test Case 3: Lấy Thông Tin Payment Intent

**Endpoint:** `GET /api/stripe/payment-intent/{id}`

**Mục đích:** Kiểm tra trạng thái payment

### Các Bước:

1. **Click vào `GET /api/stripe/payment-intent/{id}`**
2. Click **"Try it out"**
3. Nhập `id` (Payment Intent ID từ Test Case 1):
   ```
   pi_3Abc123...
   ```
4. Click **"Execute"**

### Response Mong Đợi:

Bạn sẽ thấy toàn bộ thông tin Payment Intent từ Stripe, bao gồm:
- ID, amount, currency
- Status (requires_payment_method, succeeded, etc.)
- Metadata (order_id)
- Customer info

---

## 🔍 Test Case 4: Lấy Thông Tin Checkout Session

**Endpoint:** `GET /api/stripe/checkout-session/{id}`

**Mục đích:** Kiểm tra Checkout Session

### Các Bước:

1. **Click vào `GET /api/stripe/checkout-session/{id}`**
2. Click **"Try it out"**
3. Nhập `id` (Session ID từ Test Case 2):
   ```
   cs_test_a1b2c3...
   ```
4. Click **"Execute"**

---

## ❌ Test Case 5: Hủy Payment Intent

**Endpoint:** `POST /api/stripe/payment-intent/{id}/cancel`

**Mục đích:** Hủy payment chưa hoàn thành

### Các Bước:

1. **Click vào `POST /api/stripe/payment-intent/{id}/cancel`**
2. Click **"Try it out"**
3. Nhập `id` (Payment Intent ID):
   ```
   pi_3Abc123...
   ```
4. Click **"Execute"**

### Response Mong Đợi:

```json
{
  "success": true,
  "message": "Payment Intent canceled successfully",
  "paymentIntent": {
    "id": "pi_3Abc123...",
    "status": "canceled",
    ...
  }
}
```

**Lưu Ý:** Chỉ cancel được payment đang ở trạng thái:
- `requires_payment_method`
- `requires_confirmation`
- `requires_action`

---

## 💰 Test Case 6: Hoàn Tiền (Refund)

**Endpoint:** `POST /api/stripe/refund`

**Mục đích:** Hoàn tiền cho payment đã thành công

### Điều Kiện Tiên Quyết:
- Payment Intent phải ở trạng thái `succeeded`
- Cần có payment thật đã thanh toán thành công

### Các Bước:

1. **Click vào `POST /api/stripe/refund`**
2. Click **"Try it out"**
3. Nhập Query Parameters:

**Hoàn tiền toàn bộ:**
```
paymentIntentId: pi_3Abc123...
amount: (leave empty)
reason: requested_by_customer
```

**Hoàn tiền một phần:**
```
paymentIntentId: pi_3Abc123...
amount: 50000
reason: requested_by_customer
```

**Reasons hợp lệ:**
- `duplicate` - Trùng lặp
- `fraudulent` - Gian lận
- `requested_by_customer` - Khách yêu cầu

4. Click **"Execute"**

### Response Mong Đợi:

```json
{
  "success": true,
  "message": "Refund created successfully",
  "refund": {
    "id": "re_1Abc123...",
    "amount": 50000,
    "status": "succeeded",
    ...
  }
}
```

---

## 🔔 Test Case 7: Webhook (Không Test Trực Tiếp Trên Swagger)

**Endpoint:** `POST /api/stripe/webhook`

**Lưu Ý:** Endpoint này **KHÔNG CẦN Authentication** và được gọi bởi Stripe, không phải user.

**Để test webhook:**

### Option 1: Dùng Stripe CLI (Recommended)

```bash
# 1. Install Stripe CLI
# Download: https://stripe.com/docs/stripe-cli

# 2. Login
stripe login

# 3. Forward webhook
stripe listen --forward-to http://localhost:8080/api/stripe/webhook

# Output sẽ show webhook secret:
# > Ready! Your webhook signing secret is whsec_xxx

# 4. Copy webhook secret vào application.properties:
# stripe.webhook-secret=whsec_xxx

# 5. Restart application

# 6. Trigger test event
stripe trigger payment_intent.succeeded

# 7. Check console logs - sẽ thấy:
# "Processing Stripe event: payment_intent.succeeded"
```

### Option 2: Test Thật

1. Tạo Payment Intent từ Swagger
2. Dùng frontend test page để thanh toán
3. Stripe tự động gọi webhook
4. Check backend logs

---

## 🎨 Test Với Frontend HTML Page

**File:** `stripe_test_frontend.html`

1. **Đảm bảo backend đang chạy** (port 8080)
2. **Mở file trong browser:**
   - Right-click file `stripe_test_frontend.html`
   - **Open with** → Chrome/Firefox/Edge

3. **Chọn phương thức thanh toán:**
   - **Stripe Checkout** (hosted page) - Dễ nhất!
   - **Payment Intent** (custom form)

4. **Nhập thông tin:**
   ```
   Order ID: 1
   Số Tiền: 100000
   Email: test@example.com
   Mô Tả: Test payment
   ```

5. **Click "Thanh Toán"**

6. **Test bằng test card:**
   ```
   Card: 4242 4242 4242 4242
   Date: 12/25
   CVC: 123
   ZIP: 12345
   ```

---

## 📊 Kiểm Tra Kết Quả Trên Stripe Dashboard

1. Truy cập: **https://dashboard.stripe.com/test/payments**
2. Đăng nhập bằng Stripe account
3. Xem tất cả payments trong sandbox

**Bạn sẽ thấy:**
- Payments đã tạo
- Trạng thái (succeeded, canceled, etc.)
- Amount, customer info
- Metadata (order_id)

---

## 💡 Tips & Tricks

### 1. Test Cards Đặc Biệt

```
✅ Thành công: 4242 4242 4242 4242
🔐 3D Secure: 4000 0025 0000 3155 (popup xác thực)
❌ Declined: 4000 0000 0000 9995
💳 Visa Debit: 4000 0566 5566 5556
💳 Mastercard: 5555 5555 5555 4444
💳 Amex: 3782 822463 10005
```

### 2. Check Response Status Codes

- **200 OK** - Thành công
- **400 Bad Request** - Sai format request
- **401 Unauthorized** - Thiếu/sai token
- **404 Not Found** - Không tìm thấy payment

### 3. Common Errors

**Error: "No API key provided"**
- Check `application.properties` có `stripe.secret-key`

**Error: "Invalid token"**
- Token JWT hết hạn, login lại

**Error: "Payment Intent not found"**
- Sai ID hoặc dùng live key với test ID

---

## 📋 Checklist Test Hoàn Chỉnh

- [ ] Application chạy thành công
- [ ] Swagger UI accessible
- [ ] Login và lấy JWT token
- [ ] Authorize token trong Swagger
- [ ] Test CREATE Payment Intent ✅
- [ ] Test CREATE Checkout Session ✅
- [ ] Copy checkout URL và test thanh toán
- [ ] Test GET Payment Intent ✅
- [ ] Test GET Checkout Session ✅
- [ ] Test CANCEL Payment Intent ✅
- [ ] Test REFUND (nếu có payment succeeded)
- [ ] Check Stripe Dashboard
- [ ] Test webhook với Stripe CLI
- [ ] Test frontend HTML page

---

## 🎯 Flow Hoàn Chỉnh

```
1. User → Swagger → Create Payment Intent
2. Backend → Stripe API → Return client_secret
3. Frontend → Dùng client_secret → Stripe.js
4. User → Nhập thông tin thẻ → Submit
5. Stripe → Xử lý payment → Gọi webhook
6. Backend → Nhận webhook → Update order
7. Frontend → Hiển thị success/fail
```

---

## 📞 Troubleshooting

### Swagger không load?
- Check application có chạy không
- Check port 8080 có bị chiếm không
- Try: http://localhost:8080/swagger-ui/index.html

### 401 Unauthorized mãi?
- Click nút "Authorize" 🔓 ở góc trên
- Nhập: `Bearer <token>`
- Đảm bảo có space sau "Bearer"

### Payment Intent created nhưng không test được?
- Cần frontend để confirm payment
- Hoặc dùng Checkout Session (easier)

---

## 🎉 Kết Luận

**Bạn đã sẵn sàng test Stripe Payment!**

**Quick Start:**
1. Run application
2. Open Swagger: http://localhost:8080/swagger-ui.html
3. Login → Get token → Authorize
4. Test "Create Checkout Session"
5. Copy URL → Paste in browser
6. Pay with test card: 4242 4242 4242 4242
7. Done! 🚀

**Happy Testing!** 💳✨

