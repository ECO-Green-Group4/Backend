# 🔔 Hướng Dẫn Cấu Hình Stripe Webhook

## ❓ Tại sao Payment Status không tự động cập nhật?

Khi user thanh toán thành công qua URL từ `/api/payments/package/stripe`, payment status vẫn là `PENDING` vì **webhook chưa được cấu hình đúng**.

### Flow thanh toán Stripe:

```
1. API tạo Payment (status: PENDING) → Trả về Checkout URL
2. User thanh toán trên Stripe → Thành công
3. Stripe gửi webhook event → Backend cập nhật status → SUCCESS ✅
```

**Vấn đề:** Hiện tại `stripe.webhook-secret` đang để trống trong `application.properties` → Webhook không hoạt động → Payment status không được cập nhật.

---

## 🛠️ Giải Pháp

### **Option 1: Sử dụng Stripe CLI (Dễ nhất - Cho Development)**

#### Bước 1: Cài đặt Stripe CLI

**Windows (PowerShell):**
```powershell
# Download Stripe CLI
iwr -useb https://packages.stripe.dev/api/windows/latest/stripe.exe -OutFile stripe.exe

# Hoặc dùng Chocolatey
choco install stripe-cli
```

**Mac:**
```bash
brew install stripe/stripe-cli/stripe
```

#### Bước 2: Login Stripe CLI

```bash
stripe login
```

Browser sẽ mở ra → Login vào Stripe account → Confirm authorization.

#### Bước 3: Forward Webhook Events

```bash
stripe listen --forward-to localhost:8080/api/stripe/webhook
```

**Output mẫu:**
```
> Ready! You are using Stripe API Version [2024-10-28.acacia]. Your webhook signing secret is whsec_xxx (^C to quit)
```

#### Bước 4: Copy Webhook Secret

Từ output trên, copy giá trị `whsec_xxx` và cập nhật vào `application.properties`:

```properties
stripe.webhook-secret=whsec_xxx
```

#### Bước 5: Restart Application

```bash
mvn spring-boot:run
```

#### Bước 6: Test Thanh Toán

1. Gọi API `/api/payments/package/stripe` để tạo checkout session
2. Mở URL trong response và thanh toán
3. Sử dụng test card: `4242 4242 4242 4242`, expiry: bất kỳ ngày tương lai
4. Check log → Webhook event được xử lý
5. Query database → Payment status = `SUCCESS` ✅

---

### **Option 2: Tạo Webhook Endpoint trên Stripe Dashboard (Cho Production)**

#### Bước 1: Expose Backend với Ngrok (nếu đang develop local)

```bash
# Cài đặt ngrok
choco install ngrok  # Windows
brew install ngrok   # Mac

# Expose port 8080
ngrok http 8080
```

**Output mẫu:**
```
Forwarding   https://abc123.ngrok.io -> http://localhost:8080
```

Copy URL `https://abc123.ngrok.io`

#### Bước 2: Tạo Webhook Endpoint trên Stripe Dashboard

1. Đăng nhập [Stripe Dashboard](https://dashboard.stripe.com/test/webhooks)
2. Click **"Add endpoint"**
3. Nhập URL:
   ```
   https://abc123.ngrok.io/api/stripe/webhook
   ```
4. Select events to listen for:
   - ✅ `checkout.session.completed`
   - ✅ `payment_intent.succeeded`
   - ✅ `payment_intent.payment_failed`
   - ✅ `payment_intent.canceled`
   - ✅ `charge.refunded`

5. Click **"Add endpoint"**

#### Bước 3: Copy Webhook Secret

Sau khi tạo endpoint, Stripe sẽ hiển thị **Signing secret**:
```
whsec_xxxxxxxxxxxxxxxxx
```

Copy và cập nhật vào `application.properties`:

```properties
stripe.webhook-secret=whsec_xxxxxxxxxxxxxxxxx
```

#### Bước 4: Restart Application

```bash
mvn spring-boot:run
```

---

### **Option 3: Tạm thời Disable Signature Verification (CHỈ CHO TEST)**

**⚠️ KHÔNG KHUYẾN NGHỊ CHO PRODUCTION**

Nếu bạn chỉ muốn test nhanh, có thể tạm thời disable webhook signature verification:

#### Sửa file `StripeServiceImpl.java`:

```java
@Override
public boolean verifyWebhookSignature(String payload, String sigHeader) {
    if (stripeConfig.getWebhookSecret() == null || stripeConfig.getWebhookSecret().isEmpty()) {
        log.warn("Webhook secret is not configured. Skipping signature verification.");
        return true; // 👈 Đổi từ false → true (CHỈ CHO TEST)
    }

    try {
        Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        log.info("Webhook signature verified successfully");
        return true;
    } catch (SignatureVerificationException e) {
        log.error("Invalid webhook signature", e);
        return false;
    }
}
```

**Restart application** và test lại.

**⚠️ LƯU Ý:** Phương pháp này không an toàn vì bất kỳ ai cũng có thể gửi fake webhook đến backend.

---

## ✅ Kiểm Tra Webhook Hoạt Động

### 1. Kiểm tra Log

Khi webhook được gọi, bạn sẽ thấy log:

```
INFO  c.e.t.c.StripeController - Received Stripe webhook
INFO  c.e.t.c.StripeController - Processing Stripe event: checkout.session.completed (evt_xxx)
INFO  c.e.t.c.StripeController - Checkout session completed: cs_test_xxx
INFO  c.e.t.s.StripePaymentServiceImpl - Processing Stripe payment success for session: cs_test_xxx
INFO  c.e.t.s.StripePaymentServiceImpl - Updated payment status to SUCCESS: 123
INFO  c.e.t.s.StripePaymentServiceImpl - Activated listing package: 456
```

### 2. Kiểm tra Database

```sql
SELECT payment_id, payment_status, payment_gateway, gateway_transaction_id, payment_date
FROM payments
WHERE payment_gateway = 'STRIPE'
ORDER BY created_at DESC;
```

Kết quả mong đợi:
```
payment_id | payment_status | payment_gateway | gateway_transaction_id | payment_date
-----------|----------------|-----------------|------------------------|---------------------
123        | SUCCESS        | STRIPE          | cs_test_xxx            | 2025-10-23 10:30:00
```

### 3. Test với Stripe Test Cards

| Card Number         | Description          |
|---------------------|----------------------|
| 4242 4242 4242 4242 | Succeeds            |
| 4000 0000 0000 0002 | Declined            |
| 4000 0000 0000 9995 | Insufficient funds  |

---

## 🐛 Troubleshooting

### Webhook không được gọi

**Kiểm tra:**
1. Backend có đang chạy không?
2. Ngrok/Stripe CLI có đang chạy không?
3. URL webhook đúng chưa? (phải là `/api/stripe/webhook`)
4. SecurityConfig có cho phép public access chưa? → ✅ Đã có

### Webhook trả về 401 Unauthorized

**Nguyên nhân:** Webhook secret không đúng hoặc chưa cấu hình.

**Fix:**
- Kiểm tra lại `stripe.webhook-secret` trong `application.properties`
- Restart application sau khi thay đổi

### Webhook trả về 500 Internal Server Error

**Nguyên nhân:** Lỗi trong business logic (không tìm thấy payment, listing package, etc.)

**Fix:**
- Kiểm tra log để xem exception cụ thể
- Đảm bảo payment đã được tạo trước khi webhook được gọi

---

## 📝 Summary

Để payment status tự động cập nhật thành SUCCESS:

1. **Cấu hình webhook secret** (chọn 1 trong 3 option trên)
2. **Restart application**
3. **Test thanh toán**
4. **Kiểm tra log và database**

✅ Khi webhook hoạt động đúng:
- Payment status sẽ tự động chuyển từ `PENDING` → `SUCCESS`
- Listing package status sẽ chuyển từ `PENDING_PAYMENT` → `ACTIVE`
- Contract status sẽ chuyển từ `PENDING_PAYMENT` → `PAID`

---

## 🎯 Recommended Approach

**Development:** Sử dụng Stripe CLI (`stripe listen`) - Nhanh, dễ debug

**Production:** Tạo webhook endpoint trên Stripe Dashboard với domain thật

---

Nếu cần hỗ trợ thêm, hãy check logs hoặc liên hệ!

