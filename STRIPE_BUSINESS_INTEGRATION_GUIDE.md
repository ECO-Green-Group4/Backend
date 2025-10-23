# Hướng Dẫn Thanh Toán Package/Membership/Contract Bằng Stripe

## 🎯 Tổng Quan

Stripe đã được tích hợp đầy đủ vào business logic của hệ thống EV Trade. Bây giờ bạn có thể thanh toán các loại sau bằng Stripe:

1. **Listing Package (VIP Package)** - Gói tin VIP cho listing
2. **Membership** - Gói thành viên
3. **Contract** - Hợp đồng mua bán
4. **Contract Add-On** - Dịch vụ bổ sung cho hợp đồng

---

## 🚀 API Endpoints Mới

### 1. Thanh Toán Listing Package (VIP) Bằng Stripe

**Endpoint:** `POST /api/payments/package/stripe`

**Mục đích:** Seller chọn gói VIP cho listing và thanh toán bằng Stripe

**Query Parameters:**
- `listingPackageId` (required): ID của ListingPackage cần thanh toán

**Headers:**
- `Authorization: Bearer {jwt_token}`

**Flow:**
```
1. Seller chọn package cho listing (qua API khác)
   → Tạo ListingPackage với status "PENDING_PAYMENT"

2. Seller gọi API này để thanh toán bằng Stripe
   → Backend tạo Payment record (status: PENDING)
   → Backend tạo Stripe Checkout Session
   → Trả về checkout URL

3. Frontend redirect user đến checkout URL

4. User thanh toán trên trang Stripe

5. Stripe gọi webhook → Backend update Payment (status: SUCCESS)
   → ListingPackage chuyển sang "ACTIVE"
```

**Response:**
```json
{
  "status": 200,
  "message": "Stripe checkout session created. Redirect user to checkout URL",
  "data": {
    "sessionId": "cs_test_xxx",
    "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_xxx",
    "publishableKey": "pk_test_xxx",
    "orderId": 123,
    "amount": 500000,
    "currency": "VND",
    "message": "Checkout session created..."
  }
}
```

**Test trên Swagger:**
1. Mở Swagger: `http://localhost:8080/swagger-ui.html`
2. Authorize với JWT token
3. Tìm `POST /api/payments/package/stripe`
4. Nhập `listingPackageId` (ví dụ: 1)
5. Execute
6. Copy `checkoutUrl` và mở trong browser
7. Thanh toán với test card: **4242 4242 4242 4242**

---

### 2. Thanh Toán Membership Bằng Stripe

**Endpoint:** `POST /api/payments/membership/stripe`

**Mục đích:** User mua gói membership

**Query Parameters:**
- `servicePackageId` (required): ID của ServicePackage loại MEMBERSHIP

**Flow tương tự Listing Package**

**Test:**
```bash
# 1. Get danh sách membership packages
GET /api/payments/membership/packages

# 2. Chọn package và thanh toán
POST /api/payments/membership/stripe?servicePackageId=1
```

---

### 3. Thanh Toán Contract Bằng Stripe

**Endpoint:** `POST /api/payments/contract/stripe`

**Mục đích:** Buyer thanh toán hợp đồng mua pin

**Query Parameters:**
- `contractId` (required): ID của Contract cần thanh toán

**Flow:**
```
1. Buyer tạo order → Contract được tạo (status: PENDING_PAYMENT)
2. Buyer thanh toán contract bằng Stripe
3. Payment success → Contract status = "PAID"
```

---

### 4. Thanh Toán Contract Add-On Bằng Stripe

**Endpoint:** `POST /api/payments/addon/stripe`

**Mục đích:** Buyer thanh toán dịch vụ bổ sung (bảo hiểm, bảo hành...)

**Query Parameters:**
- `contractAddOnId` (required): ID của ContractAddOn

---

## 📊 So Sánh Payment Gateways

| Feature | Stripe | VNPay | MoMo | SePay |
|---------|--------|-------|------|-------|
| Listing Package | ✅ | ✅ | ✅ | ✅ |
| Membership | ✅ | ❌ | ✅ | ✅ |
| Contract | ✅ | ✅ | ❌ | ✅ |
| Add-On | ✅ | ✅ | ✅ | ✅ |
| Thẻ quốc tế | ✅ | ⚠️ | ❌ | ❌ |
| QR Code | ❌ | ❌ | ✅ | ✅ |
| Hosted Checkout | ✅ | ❌ | ❌ | ❌ |
| Webhook | ✅ | ✅ | ✅ | ✅ |

---

## 🔄 Payment Flow Hoàn Chỉnh

### Flow 1: Listing Package (VIP)

```
┌─────────────┐
│   Seller    │
└──────┬──────┘
       │
       │ 1. Chọn package cho listing
       ▼
┌──────────────────────────┐
│  POST /api/seller/...    │
│  selectPackage()         │
└──────┬───────────────────┘
       │ Creates ListingPackage
       │ Status: PENDING_PAYMENT
       ▼
┌──────────────────────────┐
│  Seller decides to pay   │
│  with Stripe             │
└──────┬───────────────────┘
       │
       │ 2. Thanh toán bằng Stripe
       ▼
┌──────────────────────────────────────┐
│  POST /api/payments/package/stripe   │
│  payListingPackageWithStripe()       │
└──────┬───────────────────────────────┘
       │
       │ - Create Payment (PENDING)
       │ - Create Stripe Checkout Session
       │ - Save sessionId to Payment
       ▼
┌──────────────────────────┐
│  Response:               │
│  {                       │
│    checkoutUrl: "..."    │
│  }                       │
└──────┬───────────────────┘
       │
       │ 3. Redirect to Stripe
       ▼
┌──────────────────────────┐
│  Stripe Checkout Page    │
│  User enters card info   │
└──────┬───────────────────┘
       │
       │ 4. User clicks Pay
       ▼
┌──────────────────────────┐
│  Stripe processes        │
│  payment                 │
└──────┬───────────────────┘
       │
       │ 5. Stripe calls webhook
       ▼
┌─────────────────────────────────────┐
│  POST /api/stripe/webhook            │
│  Event: checkout.session.completed   │
└──────┬──────────────────────────────┘
       │
       │ - Find Payment by sessionId
       │ - Update Payment: SUCCESS
       │ - Update ListingPackage: ACTIVE
       ▼
┌──────────────────────────┐
│  ✅ Payment Complete     │
│  ListingPackage ACTIVE   │
└──────────────────────────┘
```

---

## 🧪 Test Scenarios

### Scenario 1: Thanh Toán Listing Package Thành Công

**Bước 1: Login**
```
POST /api/auth/login
{
  "username": "seller@example.com",
  "password": "password"
}
```
→ Copy JWT token

**Bước 2: Authorize trong Swagger**
Click "Authorize" → `Bearer {token}`

**Bước 3: Chọn package cho listing**
```
POST /api/seller/listings/{listingId}/select-package
{
  "packageId": 1  // VIP package ID
}
```
→ Tạo ListingPackage (PENDING_PAYMENT)
→ Copy `listingPackageId` từ response

**Bước 4: Thanh toán bằng Stripe**
```
POST /api/payments/package/stripe?listingPackageId=1
```
→ Response có `checkoutUrl`

**Bước 5: Thanh toán**
- Copy `checkoutUrl`
- Paste vào browser
- Nhập test card: **4242 4242 4242 4242**
- Expiry: **12/25**
- CVC: **123**
- Click **Pay**

**Bước 6: Verify payment**
```
GET /api/payments/history
```
→ Sẽ thấy payment với status "SUCCESS"

**Bước 7: Verify listing package**
Check trong database: ListingPackage status đã chuyển sang "ACTIVE"

---

### Scenario 2: Thanh Toán Membership

**Test:**
```bash
# 1. Get membership packages
GET /api/payments/membership/packages

# 2. Pay with Stripe
POST /api/payments/membership/stripe?servicePackageId=2

# 3. Complete payment on Stripe checkout page

# 4. Check payment history
GET /api/payments/history
```

---

## 🔔 Webhook Setup

### Local Development

**Option 1: Stripe CLI (Recommended)**

```bash
# 1. Install Stripe CLI
# https://stripe.com/docs/stripe-cli

# 2. Login
stripe login

# 3. Forward webhook
stripe listen --forward-to http://localhost:8080/api/stripe/webhook

# Output:
# > Ready! Your webhook signing secret is whsec_xxx

# 4. Copy webhook secret
# Add to application.properties:
stripe.webhook-secret=whsec_xxx

# 5. Restart application

# 6. Test webhook
# Khi thanh toán trên Stripe, webhook sẽ được forward tự động
```

**Option 2: Ngrok**

```bash
# 1. Start ngrok
ngrok http 8080

# 2. Copy HTTPS URL (https://abc123.ngrok.io)

# 3. Go to Stripe Dashboard
# https://dashboard.stripe.com/test/webhooks

# 4. Add endpoint
# URL: https://abc123.ngrok.io/api/stripe/webhook
# Events: checkout.session.completed

# 5. Copy webhook secret
# Add to application.properties

# 6. Restart app
```

### Production

1. Deploy application
2. Stripe Dashboard → Webhooks → Add endpoint
3. URL: `https://yourdomain.com/api/stripe/webhook`
4. Select events:
   - `checkout.session.completed` ✅
   - `payment_intent.succeeded`
   - `payment_intent.payment_failed`
5. Copy webhook secret
6. Update production `application.properties`

---

## 💾 Database Changes

### Payment Table

Khi thanh toán bằng Stripe, Payment record có các field:

```sql
payment_gateway = 'STRIPE'
gateway_transaction_id = 'cs_test_xxx' -- Stripe sessionId
payment_status = 'PENDING' → 'SUCCESS'
payment_type = 'PACKAGE' | 'MEMBERSHIP' | 'CONTRACT' | 'ADDON'
```

### Example Payment Records

**Listing Package:**
```sql
INSERT INTO payments (
  payment_type, 
  listing_package_id, 
  payer_id, 
  payment_gateway,
  gateway_transaction_id,
  amount, 
  currency,
  payment_status
) VALUES (
  'PACKAGE',
  1,
  10,
  'STRIPE',
  'cs_test_a1b2c3d4e5f6',
  500000,
  'VND',
  'SUCCESS'
);
```

---

## 📈 Monitoring & Logging

### Backend Logs

Khi thanh toán thành công, backend sẽ log:

```
INFO - Starting Stripe payment for listing package: 1, user: 10
INFO - Created Stripe checkout session: cs_test_xxx for payment: 123
INFO - Checkout session completed: cs_test_xxx
INFO - Successfully processed payment for session: cs_test_xxx
INFO - Updated payment status to SUCCESS: 123
INFO - Activated listing package: 1
```

### Stripe Dashboard

Check tất cả payments tại:
```
https://dashboard.stripe.com/test/payments
```

Bạn sẽ thấy:
- Payment amount
- Customer email
- Status (Succeeded, Failed, etc.)
- Metadata (order_id)

---

## 🐛 Troubleshooting

### Issue 1: Payment created nhưng không activate package

**Nguyên nhân:** Webhook không được gọi hoặc failed

**Giải pháp:**
1. Check Stripe CLI có đang chạy không
2. Check webhook secret trong `application.properties`
3. Check logs để xem có lỗi webhook không
4. Test webhook manually:
   ```bash
   stripe trigger checkout.session.completed
   ```

---

### Issue 2: "Listing package not found"

**Nguyên nhân:** `listingPackageId` không tồn tại hoặc không thuộc về user

**Giải pháp:**
1. Check `listingPackageId` có đúng không
2. Check user có quyền thanh toán package này không
3. Verify: `listing.user_id == current_user.id`

---

### Issue 3: "Listing package is not in pending payment status"

**Nguyên nhân:** Package đã được thanh toán hoặc expired

**Giải pháp:**
1. Check status của ListingPackage
2. Nếu đã ACTIVE → không cần thanh toán lại
3. Nếu EXPIRED → tạo lại package mới

---

## ✅ Checklist Hoàn Chỉnh

### Development
- [x] Stripe dependency added
- [x] StripeConfig configured
- [x] StripePaymentServiceImpl created
- [x] PaymentController endpoints added
- [x] StripeController webhook updated
- [x] PaymentRepository updated
- [x] SecurityConfig allows webhook
- [ ] Test payment flow end-to-end
- [ ] Setup Stripe CLI for local webhook
- [ ] Test webhook processing

### Testing
- [ ] Test listing package payment
- [ ] Test membership payment
- [ ] Test contract payment
- [ ] Test add-on payment
- [ ] Test payment success flow
- [ ] Test payment failed flow
- [ ] Test webhook idempotency
- [ ] Test concurrent payments

### Production
- [ ] Get production Stripe API keys
- [ ] Update production application.properties
- [ ] Setup production webhook endpoint
- [ ] Configure webhook events
- [ ] Add webhook secret
- [ ] Test production payment
- [ ] Monitor first real payment

---

## 🎯 Next Steps

### 1. Test Local

```bash
# Run application
mvn spring-boot:run

# Open Swagger
http://localhost:8080/swagger-ui.html

# Test payment flow
```

### 2. Frontend Integration

**React Example:**
```jsx
// Step 1: Get checkout URL from backend
const response = await fetch('/api/payments/package/stripe?listingPackageId=1', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const data = await response.json();

// Step 2: Redirect to Stripe Checkout
window.location.href = data.data.checkoutUrl;

// Step 3: User pays on Stripe

// Step 4: Stripe redirects back to success URL
// http://localhost:3000/payment/success?session_id=cs_test_xxx

// Step 5: Show success message
```

### 3. Enhance Features

**Optional enhancements:**
- Email notification khi payment success
- SMS notification
- Payment analytics dashboard
- Refund management UI
- Subscription support (recurring payments)

---

## 📚 API Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/payments/package/stripe` | POST | Pay listing package |
| `/api/payments/membership/stripe` | POST | Pay membership |
| `/api/payments/contract/stripe` | POST | Pay contract |
| `/api/payments/addon/stripe` | POST | Pay add-on |
| `/api/stripe/webhook` | POST | Handle Stripe webhook |
| `/api/payments/history` | GET | Get payment history |
| `/api/payments/{id}` | GET | Get payment detail |

---

## 🎉 Kết Luận

**Stripe đã được tích hợp hoàn chỉnh vào business logic của EV Trade!**

**Bạn có thể:**
✅ Thanh toán Listing Package (VIP)  
✅ Thanh toán Membership  
✅ Thanh toán Contract  
✅ Thanh toán Add-On  
✅ Nhận webhook tự động update database  
✅ Track payment history  
✅ Test với Stripe sandbox  

**Happy Coding! 🚀💳**

