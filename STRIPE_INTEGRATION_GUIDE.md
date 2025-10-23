# Hướng Dẫn Tích Hợp Stripe Payment Gateway

## 📋 Tổng Quan

Stripe là một trong những payment gateway phổ biến nhất thế giới, hỗ trợ thanh toán trực tuyến qua thẻ tín dụng/ghi nợ, ví điện tử và nhiều phương thức khác.

### ✅ Đã Hoàn Thành

- ✅ Thêm Stripe Java SDK dependency
- ✅ Cấu hình Stripe với API keys (Sandbox)
- ✅ Tạo StripeConfig để quản lý cấu hình
- ✅ Tạo StripeService và StripeServiceImpl
- ✅ Tạo StripeController với đầy đủ endpoints
- ✅ Tạo Request/Response DTOs
- ✅ Cấu hình Security để cho phép webhook
- ✅ Hỗ trợ 2 phương thức thanh toán:
  - Payment Intent API (custom payment form)
  - Checkout Session (hosted checkout page)

---

## 🔑 Thông Tin Cấu hình

### API Keys (Sandbox)

```properties
# Publishable Key (dùng ở frontend)
stripe.publishable-key=pk_test_51SLP6f3mWDY4eArLAgJ5QocWNywhwx86kbSkJb23ghm8CFNA0DRFMZXadBX6otYAAXoGsyBNX5ol8LdHdvyPEyr500NpS2yzH1

# Secret Key (dùng ở backend)
stripe.secret-key=sk_test_51SLP6f3mWDY4eArLc9yBt9mwxEZbElkb6jy510LuKB6QkjWqu5qallu2PoJe0Zmnkrz8xrWmZAUyaj65AqlYZTSk004Z0LATvJ
```

### URLs

```properties
stripe.success-url=http://localhost:3000/payment/success
stripe.cancel-url=http://localhost:3000/payment/cancel
stripe.currency=VND
```

---

## 🚀 Các API Endpoints

### 1. Tạo Payment Intent (Custom Payment Form)

**Endpoint:** `POST /api/stripe/create-payment-intent`

**Mô tả:** Tạo Payment Intent để sử dụng với Stripe Elements (custom payment form)

**Request Body:**
```json
{
  "orderId": 123,
  "amount": 100000,
  "description": "Thanh toán đơn hàng #123",
  "customerEmail": "customer@example.com"
}
```

**Response:**
```json
{
  "clientSecret": "pi_xxx_secret_yyy",
  "paymentIntentId": "pi_xxx",
  "publishableKey": "pk_test_xxx",
  "amount": 100000,
  "currency": "vnd",
  "status": "requires_payment_method",
  "description": "Thanh toán đơn hàng #123",
  "orderId": 123,
  "message": "Payment Intent created successfully. Use client secret to confirm payment."
}
```

**Flow Frontend:**
```javascript
// 1. Gọi API tạo Payment Intent
const response = await fetch('/api/stripe/create-payment-intent', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    orderId: 123,
    amount: 100000,
    description: 'Thanh toán đơn hàng #123',
    customerEmail: 'customer@example.com'
  })
});

const { clientSecret, publishableKey } = await response.json();

// 2. Khởi tạo Stripe
const stripe = Stripe(publishableKey);
const elements = stripe.elements({ clientSecret });

// 3. Tạo Payment Element
const paymentElement = elements.create('payment');
paymentElement.mount('#payment-element');

// 4. Xử lý submit form
const form = document.getElementById('payment-form');
form.addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const { error } = await stripe.confirmPayment({
    elements,
    confirmParams: {
      return_url: 'http://localhost:3000/payment/success',
    },
  });
  
  if (error) {
    console.error(error.message);
  }
});
```

---

### 2. Tạo Checkout Session (Hosted Checkout Page)

**Endpoint:** `POST /api/stripe/create-checkout-session`

**Mô tả:** Tạo Checkout Session để redirect user đến trang thanh toán của Stripe

**Request Body:**
```json
{
  "orderId": 123,
  "amount": 100000,
  "productName": "Đơn hàng #123",
  "description": "Pin xe điện Tesla Model 3",
  "customerEmail": "customer@example.com",
  "quantity": 1
}
```

**Response:**
```json
{
  "sessionId": "cs_test_xxx",
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_xxx",
  "publishableKey": "pk_test_xxx",
  "orderId": 123,
  "amount": 100000,
  "currency": "VND",
  "message": "Checkout session created. Redirect user to checkout URL."
}
```

**Flow Frontend:**
```javascript
// 1. Gọi API tạo Checkout Session
const response = await fetch('/api/stripe/create-checkout-session', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    orderId: 123,
    amount: 100000,
    productName: 'Đơn hàng #123',
    description: 'Pin xe điện Tesla Model 3',
    customerEmail: 'customer@example.com'
  })
});

const { checkoutUrl } = await response.json();

// 2. Redirect user đến trang thanh toán Stripe
window.location.href = checkoutUrl;
```

---

### 3. Webhook từ Stripe

**Endpoint:** `POST /api/stripe/webhook`

**Mô tả:** Nhận webhook events từ Stripe (payment succeeded, failed, refunded, etc.)

**Headers:**
- `Stripe-Signature`: Signature để verify webhook

**Events được xử lý:**
- `payment_intent.succeeded` - Thanh toán thành công
- `payment_intent.payment_failed` - Thanh toán thất bại
- `payment_intent.canceled` - Thanh toán bị hủy
- `charge.refunded` - Đã hoàn tiền
- `checkout.session.completed` - Checkout session hoàn thành

**Cấu hình Webhook trên Stripe Dashboard:**

1. Truy cập: https://dashboard.stripe.com/test/webhooks
2. Click "Add endpoint"
3. Nhập URL: `https://your-domain.com/api/stripe/webhook`
4. Chọn events: 
   - `payment_intent.succeeded`
   - `payment_intent.payment_failed`
   - `payment_intent.canceled`
   - `charge.refunded`
   - `checkout.session.completed`
5. Copy Webhook Signing Secret và thêm vào `application.properties`:
   ```properties
   stripe.webhook-secret=whsec_xxx
   ```

---

### 4. Lấy Thông Tin Payment Intent

**Endpoint:** `GET /api/stripe/payment-intent/{id}`

**Example:**
```bash
GET /api/stripe/payment-intent/pi_xxx
```

---

### 5. Lấy Thông Tin Checkout Session

**Endpoint:** `GET /api/stripe/checkout-session/{id}`

**Example:**
```bash
GET /api/stripe/checkout-session/cs_test_xxx
```

---

### 6. Hủy Payment Intent

**Endpoint:** `POST /api/stripe/payment-intent/{id}/cancel`

**Example:**
```bash
POST /api/stripe/payment-intent/pi_xxx/cancel
```

**Response:**
```json
{
  "success": true,
  "message": "Payment Intent canceled successfully",
  "paymentIntent": { ... }
}
```

---

### 7. Hoàn Tiền (Refund)

**Endpoint:** `POST /api/stripe/refund`

**Query Parameters:**
- `paymentIntentId` (required): ID của Payment Intent
- `amount` (optional): Số tiền hoàn (null = hoàn toàn bộ)
- `reason` (optional): Lý do hoàn tiền (duplicate, fraudulent, requested_by_customer)

**Example:**
```bash
POST /api/stripe/refund?paymentIntentId=pi_xxx&amount=50000&reason=requested_by_customer
```

**Response:**
```json
{
  "success": true,
  "message": "Refund created successfully",
  "refund": {
    "id": "re_xxx",
    "amount": 50000,
    "status": "succeeded"
  }
}
```

---

## 🧪 Test với Stripe Sandbox

### Test Cards

Stripe cung cấp các test cards để test thanh toán:

| Card Number | Brand | Scenario |
|------------|-------|----------|
| 4242 4242 4242 4242 | Visa | Thành công |
| 4000 0025 0000 3155 | Visa | Yêu cầu 3D Secure |
| 4000 0000 0000 9995 | Visa | Thất bại (declined) |
| 5555 5555 5555 4444 | Mastercard | Thành công |
| 3782 822463 10005 | Amex | Thành công |

**Các thông tin khác:**
- **Expiry Date:** Bất kỳ ngày nào trong tương lai (VD: 12/25)
- **CVC:** Bất kỳ 3 số nào (VD: 123)
- **ZIP Code:** Bất kỳ 5 số nào (VD: 12345)

### Test Webhook Locally

Sử dụng Stripe CLI để test webhook trên localhost:

```bash
# 1. Cài đặt Stripe CLI
# Download tại: https://stripe.com/docs/stripe-cli

# 2. Login
stripe login

# 3. Forward webhook đến localhost
stripe listen --forward-to http://localhost:8080/api/stripe/webhook

# 4. Trigger test event
stripe trigger payment_intent.succeeded
```

---

## 📊 So Sánh 2 Phương Thức

### Payment Intent API

**Ưu điểm:**
- ✅ Kiểm soát hoàn toàn UI/UX
- ✅ Tích hợp seamless vào website
- ✅ Tùy chỉnh payment form theo brand

**Nhược điểm:**
- ❌ Phải tự build payment form
- ❌ Phức tạp hơn trong việc implement

**Khi nào dùng:**
- Khi muốn giữ user ở trong website
- Khi cần tùy chỉnh UI theo brand
- Khi có nhiều bước trong checkout flow

---

### Checkout Session

**Ưu điểm:**
- ✅ Dễ implement (chỉ cần redirect)
- ✅ Stripe handle tất cả UI/security
- ✅ Hỗ trợ nhiều payment methods
- ✅ PCI compliance tự động

**Nhược điểm:**
- ❌ Redirect user ra khỏi website
- ❌ Không tùy chỉnh UI được nhiều

**Khi nào dùng:**
- Khi muốn implement nhanh
- Khi chấp nhận redirect user
- Khi muốn hỗ trợ nhiều payment methods

---

## 🔒 Security Best Practices

### 1. Bảo Mật API Keys

❌ **KHÔNG BAO GIỜ:**
- Commit secret key vào Git
- Expose secret key ra frontend
- Chia sẻ API keys qua email/chat

✅ **NÊN:**
- Lưu keys trong environment variables
- Dùng `.env` file (thêm vào `.gitignore`)
- Rotate keys định kỳ

### 2. Verify Webhook Signature

Luôn verify webhook signature để đảm bảo request thực sự từ Stripe:

```java
if (!stripeService.verifyWebhookSignature(payload, sigHeader)) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
}
```

### 3. Idempotency

Stripe có thể gửi cùng một webhook event nhiều lần. Xử lý idempotent:

```java
// Lưu event ID đã xử lý
if (processedEventIds.contains(event.getId())) {
    return; // Skip
}
processedEventIds.add(event.getId());
```

---

## 🐛 Troubleshooting

### Lỗi: "No API key provided"

**Nguyên nhân:** Chưa cấu hình `stripe.secret-key`

**Giải pháp:** Kiểm tra `application.properties`

---

### Lỗi: "Invalid webhook signature"

**Nguyên nhân:** 
- Chưa cấu hình `stripe.webhook-secret`
- Webhook secret không đúng

**Giải pháp:** 
1. Lấy webhook secret từ Stripe Dashboard
2. Thêm vào `application.properties`:
   ```properties
   stripe.webhook-secret=whsec_xxx
   ```

---

### Lỗi: "Amount must be at least..."

**Nguyên nhân:** Stripe có minimum amount cho mỗi currency

**VND minimum:** 10,000 VND (có thể khác nhau tùy region)

**Giải pháp:** Đảm bảo amount >= minimum

---

## 📚 Tài Liệu Tham Khảo

- [Stripe Documentation](https://stripe.com/docs)
- [Stripe API Reference](https://stripe.com/docs/api)
- [Stripe Java SDK](https://stripe.com/docs/api/java)
- [Payment Intents Guide](https://stripe.com/docs/payments/payment-intents)
- [Checkout Sessions Guide](https://stripe.com/docs/payments/checkout)
- [Testing Stripe](https://stripe.com/docs/testing)

---

## 🎯 Next Steps

### 1. Test Locally

```bash
# 1. Build project
mvn clean install

# 2. Run application
mvn spring-boot:run

# 3. Test API với Postman/cURL
curl -X POST http://localhost:8080/api/stripe/create-payment-intent \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 123,
    "amount": 100000,
    "description": "Test payment",
    "customerEmail": "test@example.com"
  }'
```

### 2. Integrate Frontend

Tích hợp Stripe Elements hoặc Checkout vào frontend React/Vue/Angular.

### 3. Setup Webhook

Cấu hình webhook trên Stripe Dashboard và thêm webhook secret.

### 4. Production

Khi deploy production:
1. Đổi sang Production API keys
2. Cập nhật webhook URL
3. Test kỹ trước khi go-live

---

## ✅ Checklist

- [x] Thêm Stripe dependency
- [x] Cấu hình API keys
- [x] Tạo StripeConfig
- [x] Tạo StripeService
- [x] Tạo StripeController
- [x] Cấu hình Security
- [ ] Test Payment Intent flow
- [ ] Test Checkout Session flow
- [ ] Setup webhook trên Stripe Dashboard
- [ ] Test webhook locally
- [ ] Integrate frontend
- [ ] Deploy và test production

---

**🎉 Stripe Integration hoàn tất!**

Bây giờ bạn có thể nhận thanh toán qua Stripe với 2 phương thức:
1. **Payment Intent API** - Custom payment form
2. **Checkout Session** - Hosted checkout page

Chúc bạn thành công! 🚀

