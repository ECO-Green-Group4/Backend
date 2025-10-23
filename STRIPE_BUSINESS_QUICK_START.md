# Stripe Business Integration - Quick Start

## 🎯 Bạn Cần Gì?

Stripe đã được tích hợp vào **business logic thực tế** của dự án. Giờ bạn có thể thanh toán:

✅ **Listing Package (VIP)** - Gói tin VIP cho listing  
✅ **Membership** - Gói thành viên  
✅ **Contract** - Hợp đồng mua bán  
✅ **Add-On** - Dịch vụ bổ sung  

---

## 🚀 Test Ngay Trên Swagger

### Bước 1: Run Application
```bash
mvn spring-boot:run
```

### Bước 2: Mở Swagger
```
http://localhost:8080/swagger-ui.html
```

### Bước 3: Login & Authorize
1. Section **"Auth Controller"** → `POST /api/auth/login`
2. Login với user seller có sẵn
3. Copy token
4. Click **"Authorize"** 🔓 → `Bearer {token}`

### Bước 4: Test Thanh Toán Package

1. **Tìm section "Payment Controller"**
2. **Click `POST /api/payments/package/stripe`**
3. Click **"Try it out"**
4. Nhập `listingPackageId`: **1** (hoặc ID có sẵn trong DB)
5. Click **"Execute"**

**Response:**
```json
{
  "status": 200,
  "data": {
    "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_xxx",
    "sessionId": "cs_test_xxx",
    "amount": 500000,
    ...
  }
}
```

6. **Copy `checkoutUrl`**
7. **Paste vào browser**
8. Thanh toán với test card:
   ```
   Card: 4242 4242 4242 4242
   Date: 12/25
   CVC: 123
   ```

9. Click **Pay** → ✅ Thành công!

---

## 📋 4 API Endpoints Mới

| Endpoint | Thanh Toán Gì | Test Với |
|----------|---------------|----------|
| `POST /api/payments/package/stripe` | Listing Package (VIP) | `listingPackageId=1` |
| `POST /api/payments/membership/stripe` | Membership | `servicePackageId=2` |
| `POST /api/payments/contract/stripe` | Contract | `contractId=1` |
| `POST /api/payments/addon/stripe` | Add-On | `contractAddOnId=1` |

---

## 🔄 Flow Hoàn Chỉnh

```
User chọn package 
  ↓
POST /api/payments/package/stripe
  ↓
Backend tạo Payment (PENDING)
Backend tạo Stripe Checkout Session
  ↓
Frontend redirect đến checkoutUrl
  ↓
User thanh toán trên Stripe
  ↓
Stripe gọi webhook
  ↓
Backend update Payment → SUCCESS
Backend activate ListingPackage → ACTIVE
  ↓
✅ Hoàn tất!
```

---

## 💾 Dữ Liệu Trong Database

### Trước Khi Thanh Toán

**ListingPackage:**
```
id: 1
listing_id: 10
service_package_id: 2
status: PENDING_PAYMENT  ← Chờ thanh toán
```

**Payment:** Chưa có

### Sau Khi Thanh Toán

**Payment:**
```
id: 123
payment_type: PACKAGE
listing_package_id: 1
payer_id: 10
payment_gateway: STRIPE
gateway_transaction_id: cs_test_xxx
amount: 500000
currency: VND
payment_status: SUCCESS  ← Updated by webhook
```

**ListingPackage:**
```
status: ACTIVE  ← Đã được activate
```

---

## 🔔 Setup Webhook (Local)

### Option 1: Stripe CLI (Recommended)

```bash
# 1. Install Stripe CLI
# https://stripe.com/docs/stripe-cli

# 2. Forward webhook
stripe listen --forward-to http://localhost:8080/api/stripe/webhook

# 3. Copy webhook secret từ output
# whsec_xxx

# 4. Add vào application.properties
stripe.webhook-secret=whsec_xxx

# 5. Restart app
```

### Option 2: Skip Webhook (Test Only)

Bạn vẫn có thể test thanh toán mà không cần webhook. Chỉ cần:
1. Thanh toán trên Stripe
2. Manually update database:
   ```sql
   UPDATE payments SET payment_status = 'SUCCESS' 
   WHERE gateway_transaction_id = 'cs_test_xxx';
   
   UPDATE listingpackage SET status = 'ACTIVE' 
   WHERE listing_package_id = 1;
   ```

---

## 🧪 Full Test Example

### Terminal 1: Run App
```bash
mvn spring-boot:run
```

### Terminal 2: Stripe CLI (Optional)
```bash
stripe listen --forward-to http://localhost:8080/api/stripe/webhook
```

### Browser: Test Payment

1. **Swagger:** `http://localhost:8080/swagger-ui.html`

2. **Login:**
   ```
   POST /api/auth/login
   {
     "username": "seller@example.com",
     "password": "password"
   }
   ```

3. **Authorize:** Click 🔓 → `Bearer {token}`

4. **Get VIP Packages:**
   ```
   GET /api/payments/vip/packages
   ```
   → Note down a `packageId`

5. **Select Package (if needed):**
   ```
   POST /api/seller/listings/{listingId}/select-package
   {
     "packageId": 1
   }
   ```
   → Get `listingPackageId`

6. **Pay with Stripe:**
   ```
   POST /api/payments/package/stripe?listingPackageId=1
   ```
   → Copy `checkoutUrl`

7. **Pay on Stripe:** Open URL → Enter test card → Pay

8. **Verify:** 
   ```
   GET /api/payments/history
   ```
   → Should see payment with status "SUCCESS"

---

## 💡 Quick Tips

### Tip 1: Check Payment Status
```
GET /api/payments/history
```

### Tip 2: Check Database
```sql
SELECT * FROM payments 
WHERE payment_gateway = 'STRIPE' 
ORDER BY created_at DESC;
```

### Tip 3: Re-test Same Package
Nếu muốn test lại:
```sql
-- Reset payment
DELETE FROM payments WHERE listing_package_id = 1;

-- Reset listing package
UPDATE listingpackage SET status = 'PENDING_PAYMENT' 
WHERE listing_package_id = 1;
```

### Tip 4: Test Cards
```
Success:    4242 4242 4242 4242
3D Secure:  4000 0025 0000 3155
Declined:   4000 0000 0000 9995
```

---

## 🐛 Common Issues

**Issue: "Listing package not found"**
→ Check `listingPackageId` có tồn tại trong DB không

**Issue: "Not in pending payment status"**
→ Package đã thanh toán rồi. Reset DB hoặc tạo package mới

**Issue: Payment success nhưng không activate**
→ Webhook chưa được setup. Setup Stripe CLI hoặc update DB manually

**Issue: 401 Unauthorized**
→ Chưa login hoặc token hết hạn. Login lại

---

## 📚 Tài Liệu Đầy Đủ

- **Chi tiết:** `STRIPE_BUSINESS_INTEGRATION_GUIDE.md`
- **Stripe basics:** `STRIPE_INTEGRATION_GUIDE.md`
- **Swagger test:** `STRIPE_SWAGGER_TEST_GUIDE.md`

---

## ✅ Checklist Test

- [ ] Application đang chạy (port 8080)
- [ ] Đã login và có JWT token
- [ ] Đã authorize trong Swagger
- [ ] Database có ListingPackage với status PENDING_PAYMENT
- [ ] Test payment với Stripe checkout
- [ ] Payment thành công
- [ ] Database updated (Payment SUCCESS, ListingPackage ACTIVE)

---

## 🎉 Thành Công!

**Bạn đã có:**
✅ 4 payment endpoints tích hợp với business logic  
✅ Tự động update database khi payment success  
✅ Support thanh toán quốc tế  
✅ Hosted checkout page (không cần code frontend nhiều)  

**Happy Testing! 🚀💳**

