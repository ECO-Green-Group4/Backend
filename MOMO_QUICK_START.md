# MoMo Payment - Quick Start Guide

## 🚀 Tóm Tắt Nhanh

Hệ thống đã được thiết kế lại để sử dụng **MoMo Payment Gateway chính thức**. Khi user thanh toán, API sẽ trả về `paymentUrl` - một đường link đến trang thanh toán MoMo với QR code.

## 📋 Điều Gì Đã Thay Đổi?

### Backend: ✅ ĐÃ HOÀN THÀNH
- Tích hợp MoMo Payment Gateway API
- Xử lý callback và IPN từ MoMo
- Xác thực signature tự động
- Cập nhật trạng thái thanh toán tự động

### Frontend: ⚠️ CẦN CẬP NHẬT

**Trước:**
```javascript
// Hiển thị QR code trực tiếp
<QRCode value={paymentUrl} />
```

**Sau:**
```javascript
// Redirect user đến trang thanh toán MoMo
window.location.href = paymentUrl;
```

## 🔥 Sử Dụng Nhanh

### 1. API Call (không đổi)
```javascript
const response = await fetch(
  'http://localhost:8080/api/payments/package?listingPackageId=123',
  {
    method: 'POST',
    headers: {
      'Authorization': 'Bearer YOUR_TOKEN'
    }
  }
);

const result = await response.json();
```

### 2. Response Mới
```json
{
  "status": "success",
  "message": "Tạo thanh toán MoMo thành công. Truy cập paymentUrl để xem QR code và thanh toán.",
  "data": {
    "paymentId": 123,
    "paymentUrl": "https://test-payment.momo.vn/pay/...",
    "deeplink": "momo://app/...",
    "amount": 50000,
    "status": "PENDING",
    "expiryTime": "2024-01-01T12:15:00"
  }
}
```

### 3. Frontend Action
```javascript
if (result.status === 'success') {
  // Redirect đến trang thanh toán MoMo
  window.location.href = result.data.paymentUrl;
  
  // Hoặc mở tab mới
  // window.open(result.data.paymentUrl, '_blank');
}
```

### 4. Xử Lý Callback
Sau khi user thanh toán, MoMo sẽ redirect về:
```
http://localhost:8080/api/payments/momo-callback?orderId=123&resultCode=0&...
```

Frontend xử lý:
```javascript
// Trang callback
const params = new URLSearchParams(window.location.search);
const resultCode = params.get('resultCode');

if (resultCode === '0') {
  alert('Thanh toán thành công!');
  window.location.href = '/payments/history';
} else {
  alert('Thanh toán thất bại!');
}
```

## 🔧 Config (đã setup sẵn)

File `application.properties` đã được cấu hình với MoMo Sandbox:
```properties
momo.partnerCode=MOMO
momo.accessKey=F8BBA842ECF85
momo.secretKey=K951B6PE1waDMi640xX08PD3vg6EkVlz
momo.url=https://test-payment.momo.vn/v2/gateway/api/create
momo.request-type=payWithMethod
```

## 🧪 Test Ngay

1. Chạy backend:
   ```bash
   cd Backend-Phat
   mvn spring-boot:run
   ```

2. Gọi API thanh toán (dùng Postman):
   ```
   POST http://localhost:8080/api/payments/package?listingPackageId=1
   Authorization: Bearer YOUR_TOKEN
   ```

3. Copy `paymentUrl` từ response

4. Mở `paymentUrl` trong browser → Sẽ thấy trang thanh toán MoMo với QR code

5. Click "Test Payment Success" để test (trên sandbox)

## 📱 Demo Flow

```
User click "Thanh toán"
    ↓
Frontend gọi API
    ↓
Backend → MoMo API
    ↓
Nhận paymentUrl
    ↓
Redirect user đến paymentUrl
    ↓
User quét QR hoặc thanh toán
    ↓
MoMo → IPN → Backend (tự động)
    ↓
MoMo → redirect → Return URL
    ↓
Frontend xử lý callback
    ↓
Hiển thị kết quả
```

## ❓ Result Codes

| Code | Ý Nghĩa |
|------|---------|
| 0 | ✅ Thành công |
| 1000 | ⏳ Đang chờ user xác nhận |
| 1001 | ❌ User từ chối |
| 1004 | ❌ Số dư không đủ |
| 2001 | ❌ Sai thông số |

## 🆘 Troubleshooting

### Vấn đề: Payment luôn PENDING
**Giải pháp:** IPN URL phải accessible từ internet. Dùng ngrok cho local dev:
```bash
ngrok http 8080
# Cập nhật: momo.ipn-url=https://abc123.ngrok.io/api/payments/momo-ipn
```

### Vấn đề: Invalid signature
**Giải pháp:** Kiểm tra `momo.secretKey` trong application.properties

### Vấn đề: MoMo không trả về response
**Giải pháp:** Kiểm tra `momo.url` và network connection

## 📚 Tài Liệu Đầy Đủ

- **Chi tiết đầy đủ:** Xem `MOMO_INTEGRATION_GUIDE.md`
- **Danh sách thay đổi:** Xem `MOMO_CHANGELOG.md`

## 🎯 Checklist Production

Trước khi deploy production:
- [ ] Đăng ký MoMo Business: https://business.momo.vn/
- [ ] Lấy Partner Code, Access Key, Secret Key thật
- [ ] Đổi `momo.url` sang production
- [ ] Cập nhật `momo.return-url` và `momo.ipn-url` với domain thật
- [ ] Test kỹ trên sandbox
- [ ] Setup monitoring cho payment flow

## 💡 Tips

1. **paymentUrl có hiệu lực 15 phút** - sau đó sẽ hết hạn
2. **Luôn xử lý logic trong IPN handler** - không tin tưởng return URL
3. **Dùng ngrok** để test IPN trên local
4. **Result code 0 = success** - các code khác là lỗi

---

**Cần hỗ trợ?** Đọc `MOMO_INTEGRATION_GUIDE.md` hoặc liên hệ MoMo support.

