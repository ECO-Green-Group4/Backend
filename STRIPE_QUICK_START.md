# Stripe Payment - Quick Start Guide

## 🚀 Bắt Đầu Nhanh

### Bước 1: Build Project

```bash
mvn clean install
```

### Bước 2: Chạy Application

```bash
mvn spring-boot:run
```

Hoặc nếu đã build:

```bash
java -jar target/ev-trade-0.0.1-SNAPSHOT.jar
```

### Bước 3: Test API

Mở Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

Tìm section **"Stripe Payment"** để xem tất cả endpoints.

---

## 🧪 Test với cURL

### 1. Test Payment Intent

```bash
curl -X POST http://localhost:8080/api/stripe/create-payment-intent \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "orderId": 1,
    "amount": 100000,
    "description": "Test thanh toán đơn hàng #1",
    "customerEmail": "test@example.com"
  }'
```

**Expected Response:**
```json
{
  "clientSecret": "pi_xxx_secret_yyy",
  "paymentIntentId": "pi_xxx",
  "publishableKey": "pk_test_51SLP6f3m...",
  "amount": 100000,
  "currency": "vnd",
  "status": "requires_payment_method",
  "description": "Test thanh toán đơn hàng #1",
  "orderId": 1,
  "message": "Payment Intent created successfully..."
}
```

---

### 2. Test Checkout Session

```bash
curl -X POST http://localhost:8080/api/stripe/create-checkout-session \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "orderId": 1,
    "amount": 100000,
    "productName": "Pin xe điện Tesla",
    "description": "Pin Model 3 Long Range",
    "customerEmail": "test@example.com",
    "quantity": 1
  }'
```

**Expected Response:**
```json
{
  "sessionId": "cs_test_xxx",
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_xxx",
  "publishableKey": "pk_test_51SLP6f3m...",
  "orderId": 1,
  "amount": 100000,
  "currency": "VND",
  "message": "Checkout session created..."
}
```

Copy `checkoutUrl` và mở trong browser để thanh toán.

---

### 3. Test Lấy Payment Intent

```bash
curl -X GET http://localhost:8080/api/stripe/payment-intent/pi_xxx \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 4. Test Hủy Payment Intent

```bash
curl -X POST http://localhost:8080/api/stripe/payment-intent/pi_xxx/cancel \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 5. Test Refund

```bash
curl -X POST "http://localhost:8080/api/stripe/refund?paymentIntentId=pi_xxx&amount=50000&reason=requested_by_customer" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🧪 Test với Postman

### Import Collection

1. Mở Postman
2. Import từ URL: `http://localhost:8080/v3/api-docs`
3. Tìm folder "Stripe Payment"
4. Set environment variable `baseUrl` = `http://localhost:8080`
5. Set environment variable `token` = JWT token của bạn

### Test Flow

1. **Login** → Lấy JWT token
2. **Create Payment Intent** → Copy `clientSecret`
3. Dùng `clientSecret` trong frontend để confirm payment

---

## 🎨 Test Frontend Integration

### Test với HTML + JavaScript đơn giản

Tạo file `test-stripe.html`:

```html
<!DOCTYPE html>
<html>
<head>
  <title>Stripe Payment Test</title>
  <script src="https://js.stripe.com/v3/"></script>
</head>
<body>
  <h1>Test Stripe Payment</h1>
  
  <button id="checkout-button">Thanh toán với Stripe Checkout</button>
  
  <div id="payment-form-container" style="display:none; margin-top: 20px;">
    <h2>Hoặc thanh toán bằng Payment Intent</h2>
    <form id="payment-form">
      <div id="payment-element"></div>
      <button type="submit">Thanh toán</button>
      <div id="error-message"></div>
    </form>
  </div>

  <script>
    // Test 1: Checkout Session
    document.getElementById('checkout-button').addEventListener('click', async () => {
      const response = await fetch('http://localhost:8080/api/stripe/create-checkout-session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer YOUR_JWT_TOKEN' // Thay bằng token thật
        },
        body: JSON.stringify({
          orderId: 1,
          amount: 100000,
          productName: 'Test Product',
          description: 'Test Description',
          customerEmail: 'test@example.com'
        })
      });
      
      const data = await response.json();
      window.location.href = data.checkoutUrl;
    });

    // Test 2: Payment Intent
    async function testPaymentIntent() {
      // 1. Tạo Payment Intent
      const response = await fetch('http://localhost:8080/api/stripe/create-payment-intent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer YOUR_JWT_TOKEN' // Thay bằng token thật
        },
        body: JSON.stringify({
          orderId: 1,
          amount: 100000,
          description: 'Test Payment',
          customerEmail: 'test@example.com'
        })
      });
      
      const { clientSecret, publishableKey } = await response.json();
      
      // 2. Khởi tạo Stripe
      const stripe = Stripe(publishableKey);
      const elements = stripe.elements({ clientSecret });
      
      // 3. Mount Payment Element
      const paymentElement = elements.create('payment');
      paymentElement.mount('#payment-element');
      
      // 4. Hiển thị form
      document.getElementById('payment-form-container').style.display = 'block';
      
      // 5. Handle submit
      document.getElementById('payment-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const { error } = await stripe.confirmPayment({
          elements,
          confirmParams: {
            return_url: 'http://localhost:3000/payment/success',
          },
        });
        
        if (error) {
          document.getElementById('error-message').textContent = error.message;
        }
      });
    }
  </script>
</body>
</html>
```

---

## 💳 Test Cards

Sử dụng các test cards sau để test:

### Thành Công
```
Card: 4242 4242 4242 4242
Date: 12/25
CVC: 123
ZIP: 12345
```

### Yêu cầu 3D Secure
```
Card: 4000 0025 0000 3155
Date: 12/25
CVC: 123
```

### Thanh toán thất bại
```
Card: 4000 0000 0000 9995
Date: 12/25
CVC: 123
```

---

## 🔔 Test Webhook Locally

### Sử dụng Stripe CLI

```bash
# 1. Cài Stripe CLI
# Download: https://stripe.com/docs/stripe-cli

# 2. Login
stripe login

# 3. Forward webhook đến localhost
stripe listen --forward-to http://localhost:8080/api/stripe/webhook

# Output sẽ cho bạn webhook secret:
# > Ready! Your webhook signing secret is whsec_xxx

# 4. Copy webhook secret và thêm vào application.properties
# stripe.webhook-secret=whsec_xxx

# 5. Restart application

# 6. Trigger test event
stripe trigger payment_intent.succeeded

# 7. Check logs
# Backend sẽ log: "Processing Stripe event: payment_intent.succeeded"
```

### Sử dụng Ngrok (Alternative)

```bash
# 1. Cài ngrok
# Download: https://ngrok.com/download

# 2. Start ngrok
ngrok http 8080

# 3. Copy HTTPS URL (VD: https://abc123.ngrok.io)

# 4. Cấu hình webhook trên Stripe Dashboard
# URL: https://abc123.ngrok.io/api/stripe/webhook

# 5. Test bằng cách tạo payment thật
```

---

## ✅ Checklist Test

- [ ] Backend chạy thành công (`mvn spring-boot:run`)
- [ ] Swagger UI truy cập được (`http://localhost:8080/swagger-ui.html`)
- [ ] API Create Payment Intent hoạt động
- [ ] API Create Checkout Session hoạt động
- [ ] Checkout URL mở được và hiển thị trang thanh toán
- [ ] Test card thanh toán thành công
- [ ] Webhook nhận được event từ Stripe
- [ ] Log backend hiển thị "Payment succeeded"

---

## 🐛 Common Issues

### Issue 1: 401 Unauthorized

**Nguyên nhân:** Thiếu JWT token hoặc token không hợp lệ

**Giải pháp:** 
1. Login qua `/api/auth/login` để lấy token
2. Thêm header: `Authorization: Bearer {token}`

---

### Issue 2: CORS Error

**Nguyên nhân:** Frontend chạy trên domain khác localhost:5173

**Giải pháp:** 
Thêm origin vào `SecurityConfig.java`:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "http://localhost:3000"  // Thêm origin của bạn
));
```

---

### Issue 3: "No API key provided"

**Nguyên nhân:** Chưa cấu hình `stripe.secret-key` trong `application.properties`

**Giải pháp:** 
Kiểm tra file `src/main/resources/application.properties` có dòng:
```properties
stripe.secret-key=sk_test_51SLP6f3mWDY4eArLc9yBt9mwxEZbElkb6jy510LuKB6QkjWqu5qallu2PoJe0Zmnkrz8xrWmZAUyaj65AqlYZTSk004Z0LATvJ
```

---

## 📞 Support

Nếu gặp vấn đề:

1. Check logs: `tail -f logs/spring-boot-application.log`
2. Check Stripe Dashboard: https://dashboard.stripe.com/test/payments
3. Check Swagger API docs: http://localhost:8080/swagger-ui.html

---

**Happy Testing! 🎉**

