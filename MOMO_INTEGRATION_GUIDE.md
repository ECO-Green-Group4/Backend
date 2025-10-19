# Hướng Dẫn Tích Hợp Thanh Toán MoMo

## Tổng Quan

Hệ thống đã được tích hợp với **MoMo Payment Gateway** để xử lý thanh toán trực tuyến. Khi user thực hiện thanh toán, backend sẽ trả về `paymentUrl` - một đường link đến trang thanh toán của MoMo với QR code để user quét.

## Luồng Thanh Toán

```
1. User gọi API thanh toán (ví dụ: POST /api/payments/package)
   ↓
2. Backend tạo payment record
   ↓
3. Backend gọi MoMo API để tạo thanh toán
   ↓
4. MoMo trả về paymentUrl
   ↓
5. Backend trả về response với paymentUrl cho frontend
   ↓
6. Frontend redirect user đến paymentUrl
   ↓
7. User quét QR code trên trang MoMo hoặc thanh toán bằng app
   ↓
8. MoMo xử lý thanh toán và gọi IPN URL (server-to-server)
   ↓
9. Backend cập nhật trạng thái payment
   ↓
10. MoMo redirect user về returnUrl
```

## API Endpoints

### 1. Thanh Toán Gói Tin VIP
```http
POST /api/payments/package?listingPackageId={id}
Authorization: Bearer {token}
```

**Response:**
```json
{
  "status": "success",
  "message": "Tạo thanh toán MoMo thành công. Truy cập paymentUrl để xem QR code và thanh toán.",
  "data": {
    "paymentId": 123,
    "paymentType": "PACKAGE",
    "amount": 50000,
    "currency": "VND",
    "status": "PENDING",
    "paymentUrl": "https://test-payment.momo.vn/pay/...",
    "deeplink": "momo://app/...",
    "qrCodeUrl": "https://...",
    "paymentDate": null,
    "expiryTime": "2024-01-01T12:15:00",
    "gatewayTransactionId": null,
    "listingPackageId": 456,
    "gatewayResponse": {
      "partnerCode": "MOMO",
      "orderId": "123",
      "requestId": "123_1234567890",
      "amount": 50000,
      "resultCode": 0,
      "message": "Successful",
      "payUrl": "https://test-payment.momo.vn/pay/...",
      "deeplink": "momo://app/...",
      "qrCodeUrl": "https://..."
    }
  }
}
```

### 2. Thanh Toán Hợp Đồng
```http
POST /api/payments/contract?contractId={id}
Authorization: Bearer {token}
```

### 3. Thanh Toán Add-on Service
```http
POST /api/payments/addon?contractAddOnId={id}
Authorization: Bearer {token}
```

### 4. MoMo Callback (GET - cho user redirect)
```http
GET /api/payments/momo-callback?orderId=123&resultCode=0&...
```

### 5. MoMo IPN (POST - server-to-server notification)
```http
POST /api/payments/momo-ipn
Content-Type: application/json

{
  "partnerCode": "MOMO",
  "orderId": "123",
  "resultCode": 0,
  "message": "Successful",
  "transId": "1234567890",
  ...
}
```

## Cấu Hình MoMo

### application.properties

```properties
# MoMo Partner Information (Lấy từ MoMo Business Portal)
momo.partnerCode=YOUR_PARTNER_CODE
momo.accessKey=YOUR_ACCESS_KEY
momo.secretKey=YOUR_SECRET_KEY

# MoMo API URL
# Sandbox: https://test-payment.momo.vn/v2/gateway/api/create
# Production: https://payment.momo.vn/v2/gateway/api/create
momo.url=https://test-payment.momo.vn/v2/gateway/api/create

# Return URL (user redirect sau khi thanh toán)
momo.return-url=http://localhost:8080/api/payments/momo-callback

# IPN URL (MoMo gọi để thông báo kết quả)
momo.ipn-url=http://localhost:8080/api/payments/momo-ipn

# Request Type
# - captureWallet: Mở ví MoMo (nếu user đã cài app)
# - payWithMethod: Hiển thị trang thanh toán với QR code
momo.request-type=payWithMethod

# Language
momo.lang=vi
```

### Lấy Thông Tin MoMo Partner

1. Truy cập: https://business.momo.vn/
2. Đăng ký tài khoản doanh nghiệp
3. Tạo ứng dụng mới
4. Lấy thông tin:
   - Partner Code
   - Access Key
   - Secret Key

### MoMo Sandbox (Test)

Để test, bạn có thể sử dụng thông tin sandbox của MoMo:
```
Partner Code: MOMO
Access Key: F8BBA842ECF85
Secret Key: K951B6PE1waDMi640xX08PD3vg6EkVlz
```

## Frontend Implementation

### 1. Gọi API Thanh Toán

```javascript
async function payForPackage(listingPackageId) {
  try {
    const response = await fetch(
      `http://localhost:8080/api/payments/package?listingPackageId=${listingPackageId}`,
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    const result = await response.json();
    
    if (result.status === 'success') {
      // Redirect user đến trang thanh toán MoMo
      window.location.href = result.data.paymentUrl;
      
      // Hoặc mở trong tab/window mới
      // window.open(result.data.paymentUrl, '_blank');
    }
  } catch (error) {
    console.error('Payment error:', error);
  }
}
```

### 2. Xử Lý Return URL

Sau khi user thanh toán xong, MoMo sẽ redirect về `returnUrl`. Frontend cần xử lý trang này:

```javascript
// Trên trang callback
const urlParams = new URLSearchParams(window.location.search);
const resultCode = urlParams.get('resultCode');
const orderId = urlParams.get('orderId');

if (resultCode === '0') {
  // Thanh toán thành công
  showSuccessMessage('Thanh toán thành công!');
  // Redirect về trang payment history hoặc listing
  setTimeout(() => {
    window.location.href = '/payments/history';
  }, 2000);
} else {
  // Thanh toán thất bại
  const message = urlParams.get('message');
  showErrorMessage(`Thanh toán thất bại: ${message}`);
}
```

### 3. Hiển thị QR Code (Optional)

Nếu không muốn redirect, bạn có thể hiển thị QR code trực tiếp trên trang:

```javascript
async function showQRCode(listingPackageId) {
  const response = await fetch(
    `http://localhost:8080/api/payments/package?listingPackageId=${listingPackageId}`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  const result = await response.json();
  
  if (result.status === 'success') {
    // Hiển thị iframe với paymentUrl
    const iframe = document.createElement('iframe');
    iframe.src = result.data.paymentUrl;
    iframe.width = '100%';
    iframe.height = '600px';
    document.getElementById('payment-container').appendChild(iframe);
    
    // Hoặc hiển thị QR code image (nếu có)
    if (result.data.qrCodeUrl) {
      const img = document.createElement('img');
      img.src = result.data.qrCodeUrl;
      document.getElementById('qr-container').appendChild(img);
    }
  }
}
```

## Response Fields Giải Thích

| Field | Type | Mô Tả |
|-------|------|-------|
| `paymentId` | Long | ID của payment record trong database |
| `paymentUrl` | String | **QUAN TRỌNG:** URL để user truy cập và quét QR code |
| `deeplink` | String | Link để mở app MoMo trực tiếp (chỉ hoạt động trên mobile) |
| `qrCodeUrl` | String | URL trực tiếp đến hình ảnh QR code (có thể null) |
| `status` | String | Trạng thái: PENDING, SUCCESS, FAILED |
| `amount` | BigDecimal | Số tiền thanh toán |
| `currency` | String | Đơn vị tiền tệ (VND) |
| `expiryTime` | DateTime | Thời gian hết hạn (thường là 15 phút) |
| `gatewayResponse` | Object | Response đầy đủ từ MoMo API |

## MoMo Result Codes

| Result Code | Ý Nghĩa |
|-------------|---------|
| 0 | Thành công |
| 9000 | Giao dịch đã được xác nhận thành công |
| 1000 | Giao dịch đã được khởi tạo, chờ người dùng xác nhận thanh toán |
| 1001 | Giao dịch thất bại do người dùng từ chối xác nhận thanh toán |
| 1002 | Giao dịch thất bại do timeout |
| 1003 | Giao dịch thất bại do người dùng đã hủy |
| 1004 | Giao dịch thất bại do số dư không đủ |
| 1005 | Giao dịch thất bại do URL không hợp lệ |
| 1006 | Giao dịch thất bại do lỗi |
| 2001 | Giao dịch thất bại do sai thông số |
| 4001 | Số tiền không hợp lệ |
| 4100 | Giao dịch thất bại do user không tồn tại |

## Security & Best Practices

### 1. Xác Thực Signature

Backend tự động xác thực signature từ MoMo callback để đảm bảo request hợp lệ:

```java
public boolean verifyCallback(Map<String, String> params) {
    String receivedSignature = params.get("signature");
    String calculatedSignature = signHmacSHA256(rawSignature, secretKey);
    return calculatedSignature.equals(receivedSignature);
}
```

### 2. IPN vs Return URL

- **IPN URL:** Server-to-server, đáng tin cậy, dùng để cập nhật database
- **Return URL:** Browser redirect, có thể bị user can thiệp, chỉ dùng để hiển thị UI

**Luôn ưu tiên xử lý logic nghiệp vụ trong IPN handler!**

### 3. Timeout & Expiry

Payment có thời gian hết hạn (mặc định 15 phút). Sau thời gian này:
- MoMo sẽ không chấp nhận thanh toán
- Backend nên tự động cancel payment records

### 4. Production Checklist

Trước khi deploy production:

- [ ] Thay đổi `momo.url` sang production URL
- [ ] Cập nhật `momo.partnerCode`, `momo.accessKey`, `momo.secretKey` từ MoMo Business Portal
- [ ] Cập nhật `momo.return-url` và `momo.ipn-url` với domain thật
- [ ] Đảm bảo IPN URL có thể truy cập từ internet (MoMo cần gọi được)
- [ ] Test kỹ trên sandbox trước
- [ ] Setup monitoring/logging cho payment flow
- [ ] Chuẩn bị xử lý refund (nếu cần)

## Troubleshooting

### Lỗi: "MoMo API error: Invalid signature"

**Nguyên nhân:** Secret key không đúng hoặc cách tính signature sai

**Giải pháp:**
1. Kiểm tra lại `momo.secretKey` trong application.properties
2. Đảm bảo raw signature được tạo theo đúng thứ tự alphabetical
3. Kiểm tra log để xem raw signature

### Lỗi: "MoMo API không trả về response"

**Nguyên nhân:** URL không đúng hoặc network issue

**Giải pháp:**
1. Kiểm tra `momo.url` trong application.properties
2. Test bằng Postman/curl xem có kết nối được đến MoMo API không
3. Kiểm tra firewall/proxy

### Payment luôn PENDING

**Nguyên nhân:** IPN URL không được MoMo gọi được

**Giải pháp:**
1. Đảm bảo IPN URL có thể truy cập từ internet
2. Sử dụng ngrok/localtunnel cho local development
3. Kiểm tra log xem có nhận được IPN request không

### User thanh toán thành công nhưng status không update

**Nguyên nhân:** Lỗi trong IPN handler hoặc signature verification failed

**Giải pháp:**
1. Kiểm tra log của IPN handler
2. Test IPN manually bằng cách gửi POST request giả lập
3. Đảm bảo signature verification hoạt động đúng

## Testing với Sandbox

### Test Flow

1. Gọi API thanh toán để lấy `paymentUrl`
2. Mở `paymentUrl` trong browser
3. Trên trang MoMo sandbox, có thể:
   - Quét QR code bằng MoMo app sandbox
   - Click "Test Payment Success" để giả lập thanh toán thành công
   - Click "Test Payment Failed" để giả lập thanh toán thất bại

### Test với Ngrok (cho IPN URL)

```bash
# Install ngrok
# https://ngrok.com/

# Chạy ngrok
ngrok http 8080

# Copy HTTPS URL (ví dụ: https://abc123.ngrok.io)
# Cập nhật application.properties:
momo.ipn-url=https://abc123.ngrok.io/api/payments/momo-ipn
```

## Liên Hệ & Hỗ Trợ

- **MoMo Business Portal:** https://business.momo.vn/
- **MoMo API Documentation:** https://developers.momo.vn/
- **MoMo Support:** support@momo.vn

## Version History

- **v1.0** (2024-01-01): Initial implementation với MoMo Payment Gateway
  - Hỗ trợ thanh toán Package, Contract, Add-on
  - IPN callback handling
  - Signature verification

