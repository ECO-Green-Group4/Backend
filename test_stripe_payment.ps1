# PowerShell script để test Stripe Payment API
# Chạy script này bằng: .\test_stripe_payment.ps1

Write-Host "=== STRIPE PAYMENT TEST SCRIPT ===" -ForegroundColor Green
Write-Host ""

# Cấu hình
$baseUrl = "http://localhost:8080"
$apiBase = "$baseUrl/api"

# Bước 1: Test tạo Payment Intent
Write-Host "1. Testing Create Payment Intent..." -ForegroundColor Yellow

$paymentIntentRequest = @{
    orderId = 1
    amount = 100000
    description = "Test payment from PowerShell script"
    customerEmail = "test@example.com"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$apiBase/stripe/create-payment-intent" `
        -Method POST `
        -ContentType "application/json" `
        -Body $paymentIntentRequest `
        -ErrorAction Stop

    Write-Host "✓ Payment Intent created successfully!" -ForegroundColor Green
    Write-Host "  Payment Intent ID: $($response.paymentIntentId)" -ForegroundColor Cyan
    Write-Host "  Client Secret: $($response.clientSecret)" -ForegroundColor Cyan
    Write-Host "  Amount: $($response.amount) VND" -ForegroundColor Cyan
    Write-Host "  Status: $($response.status)" -ForegroundColor Cyan
    Write-Host ""
    
    # Lưu Payment Intent ID để test sau
    $paymentIntentId = $response.paymentIntentId
}
catch {
    Write-Host "✗ Failed to create Payment Intent" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Bước 2: Test tạo Checkout Session
Write-Host "2. Testing Create Checkout Session..." -ForegroundColor Yellow

$checkoutRequest = @{
    orderId = 1
    amount = 100000
    productName = "Pin xe điện Tesla Model 3"
    description = "Pin Long Range - 75 kWh"
    customerEmail = "test@example.com"
    quantity = 1
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$apiBase/stripe/create-checkout-session" `
        -Method POST `
        -ContentType "application/json" `
        -Body $checkoutRequest `
        -ErrorAction Stop

    Write-Host "✓ Checkout Session created successfully!" -ForegroundColor Green
    Write-Host "  Session ID: $($response.sessionId)" -ForegroundColor Cyan
    Write-Host "  Checkout URL: $($response.checkoutUrl)" -ForegroundColor Cyan
    Write-Host "  Amount: $($response.amount) VND" -ForegroundColor Cyan
    Write-Host ""
    
    # Hỏi user có muốn mở URL không
    $openUrl = Read-Host "Do you want to open the checkout URL in browser? (Y/N)"
    if ($openUrl -eq "Y" -or $openUrl -eq "y") {
        Start-Process $response.checkoutUrl
        Write-Host "✓ Opened checkout URL in browser" -ForegroundColor Green
    }
    Write-Host ""
}
catch {
    Write-Host "✗ Failed to create Checkout Session" -ForegroundColor Red
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Bước 3: Test lấy Payment Intent (nếu đã tạo ở bước 1)
if ($paymentIntentId) {
    Write-Host "3. Testing Get Payment Intent..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-RestMethod -Uri "$apiBase/stripe/payment-intent/$paymentIntentId" `
            -Method GET `
            -ErrorAction Stop

        Write-Host "✓ Retrieved Payment Intent successfully!" -ForegroundColor Green
        Write-Host "  ID: $($response.id)" -ForegroundColor Cyan
        Write-Host "  Status: $($response.status)" -ForegroundColor Cyan
        Write-Host "  Amount: $($response.amount) VND" -ForegroundColor Cyan
        Write-Host ""
    }
    catch {
        Write-Host "✗ Failed to get Payment Intent" -ForegroundColor Red
        Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
    }

    # Bước 4: Test hủy Payment Intent
    Write-Host "4. Testing Cancel Payment Intent..." -ForegroundColor Yellow
    
    $cancelConfirm = Read-Host "Do you want to cancel the Payment Intent? (Y/N)"
    if ($cancelConfirm -eq "Y" -or $cancelConfirm -eq "y") {
        try {
            $response = Invoke-RestMethod -Uri "$apiBase/stripe/payment-intent/$paymentIntentId/cancel" `
                -Method POST `
                -ErrorAction Stop

            Write-Host "✓ Payment Intent canceled successfully!" -ForegroundColor Green
            Write-Host "  Status: $($response.paymentIntent.status)" -ForegroundColor Cyan
            Write-Host ""
        }
        catch {
            Write-Host "✗ Failed to cancel Payment Intent" -ForegroundColor Red
            Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
            Write-Host ""
        }
    }
}

Write-Host "=== TEST COMPLETED ===" -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "1. Open Stripe Dashboard to see test payments: https://dashboard.stripe.com/test/payments" -ForegroundColor White
Write-Host "2. Setup webhook using Stripe CLI: stripe listen --forward-to http://localhost:8080/api/stripe/webhook" -ForegroundColor White
Write-Host "3. Test payment using test cards (4242 4242 4242 4242)" -ForegroundColor White
Write-Host ""
Write-Host "For detailed guide, see: STRIPE_INTEGRATION_GUIDE.md" -ForegroundColor Cyan

