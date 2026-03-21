# Razorpay Payment Gateway Setup

## 1. Create Razorpay Account

1. Sign up at [Razorpay](https://razorpay.com/)
2. Complete KYC for live payments (optional for testing)

## 2. Get API Keys

1. Go to [Dashboard → Settings → API Keys](https://dashboard.razorpay.com/app/keys)
2. Generate **Test** keys for development
3. Copy **Key ID** and **Key Secret**

## 3. Configure Backend

Add to your environment or `application.yml`:

```yaml
razorpay:
  key-id: rzp_test_xxxxxxxxxxxx
  key-secret: xxxxxxxxxxxxxxxxxxxx
  webhook-secret: xxxxxxxxxxxxxxxxxxxx  # From step 4
```

Or use environment variables:
```
RAZORPAY_KEY_ID=rzp_test_xxxx
RAZORPAY_KEY_SECRET=xxxx
RAZORPAY_WEBHOOK_SECRET=xxxx
```

## 4. Configure Webhook

1. Go to [Dashboard → Webhooks](https://dashboard.razorpay.com/app/webhooks)
2. Click **+ Add New Webhook**
3. **URL:** `https://your-domain.com/api/payments/webhook`
   - For local testing, use [ngrok](https://ngrok.com/) to expose localhost
4. **Events:** Select `payment.captured` and `payment.failed`
5. Copy the **Webhook Secret** and add to config

## 5. Payment Flow Summary

```
Frontend                    Backend                     Razorpay
   |                           |                            |
   |-- POST /course-purchases ->|                            |
   |<- { id: 1 } --------------|                            |
   |                           |                            |
   |-- POST /payments/create-order ->|                     |
   |   { orderType: "course", entityId: 1, amount: 1999 }   |
   |                           |-- Create Order ----------->|
   |                           |<- order_xxx --------------|
   |<- { razorpayOrderId, razorpayKeyId } -|               |
   |                           |                            |
   |-- Open Razorpay Checkout (user pays) --------------->|
   |                           |                            |
   |                           |<-- Webhook: payment.captured --|
   |                           |-- Update status to "paid"   |
```

## 6. Test Cards (Test Mode)

| Card Number | Scenario |
|-------------|----------|
| 4111 1111 1111 1111 | Success |
| 4000 0000 0000 0002 | Failure |
| 5267 3181 8797 5449 | Success (international) |

Use any future expiry date and any CVV.
