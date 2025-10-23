# Test Stripe Webhook Manually
# Script này giả lập webhook từ Stripe để test payment status update

param(
    [Parameter(Mandatory=$true)]
    [string]$SessionId,
    
    [Parameter(Mandatory=$false)]
    [string]$BackendUrl = "http://localhost:8080"
)

Write-Host "🧪 Testing Stripe Webhook..." -ForegroundColor Cyan
Write-Host "Session ID: $SessionId" -ForegroundColor Yellow
Write-Host "Backend URL: $BackendUrl" -ForegroundColor Yellow

# Tạo webhook payload giả lập event checkout.session.completed
$webhookPayload = @{
    id = "evt_test_" + (Get-Random)
    object = "event"
    api_version = "2024-10-28.acacia"
    created = [Math]::Floor([decimal](Get-Date(Get-Date).ToUniversalTime()-uformat "%s"))
    type = "checkout.session.completed"
    data = @{
        object = @{
            id = $SessionId
            object = "checkout.session"
            payment_status = "paid"
            status = "complete"
            mode = "payment"
            amount_total = 100000
            currency = "vnd"
        }
    }
} | ConvertTo-Json -Depth 10

Write-Host "`n📤 Sending webhook to: $BackendUrl/api/stripe/webhook" -ForegroundColor Green

try {
    $response = Invoke-WebRequest `
        -Uri "$BackendUrl/api/stripe/webhook" `
        -Method POST `
        -Body $webhookPayload `
        -ContentType "application/json" `
        -Headers @{
            "Stripe-Signature" = "t=1234567890,v1=test_signature"
        } `
        -UseBasicParsing

    Write-Host "✅ Webhook call successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    
    if ($response.Content) {
        Write-Host "`nResponse:" -ForegroundColor Cyan
        Write-Host $response.Content
    }
    
    Write-Host "`n✅ Check your database - Payment status should be updated to SUCCESS" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Error calling webhook:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "`nResponse Body:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}

Write-Host "`n💡 Tip: Query database to verify payment status:" -ForegroundColor Cyan
Write-Host "SELECT * FROM payments WHERE gateway_transaction_id = '$SessionId';" -ForegroundColor Gray

