# Razorpay Integration API

## Environment Variables

| Variable | Description |
|----------|-------------|
| `RAZORPAY_KEY_ID` | Razorpay API Key ID (e.g. rzp_live_xxx) |
| `RAZORPAY_KEY_SECRET` | Razorpay API Key Secret |

## Endpoints

### 1. POST /api/razorpay/create-order

Creates a Razorpay order. Accepts amount in INR and converts to paise internally.

**Request:**
```json
{
  "amount": 599,
  "receipt": "order_123",
  "currency": "INR"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| amount | number | Yes | Amount in Indian Rupees (e.g. 599 for ₹599) |
| receipt | string | No | Your receipt/order reference (max 40 chars) |
| currency | string | No | Default: INR |

**Success Response (200):**
```json
{
  "order_id": "order_Mn8xKABCDefgh",
  "amount": 59900,
  "key_id": "rzp_live_xxxxxxxx"
}
```

| Field | Description |
|-------|-------------|
| order_id | Razorpay order ID - pass to checkout |
| amount | Amount in paise |
| key_id | Razorpay Key ID for frontend checkout |

**Error Response (400):**
```json
{
  "success": false,
  "error": "Razorpay credentials not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET."
}
```

---

### 2. POST /api/razorpay/verify-payment

Verifies the payment signature after successful payment. Call from frontend after Razorpay handler returns.

**Request:**
```json
{
  "razorpay_order_id": "order_Mn8xKABCDefgh",
  "razorpay_payment_id": "pay_Mn8xKABCDefgh",
  "razorpay_signature": "a1b2c3d4e5f6..."
}
```

| Field | Type | Required |
|-------|------|----------|
| razorpay_order_id | string | Yes |
| razorpay_payment_id | string | Yes |
| razorpay_signature | string | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "message": "Payment verified successfully"
}
```

**Failure Response (400):**
```json
{
  "success": false,
  "message": "Payment verification failed"
}
```

---

## Frontend Flow Example

```javascript
// 1. Create order
const orderRes = await fetch('/api/razorpay/create-order', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ amount: 599, receipt: 'booking_1' })
});
const { order_id, amount, key_id } = await orderRes.json();

// 2. Open Razorpay Checkout
const options = {
  key: key_id,
  amount: amount,
  order_id: order_id,
  handler: function(response) {
    // 3. Verify payment
    fetch('/api/razorpay/verify-payment', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        razorpay_order_id: response.razorpay_order_id,
        razorpay_payment_id: response.razorpay_payment_id,
        razorpay_signature: response.razorpay_signature
      })
    }).then(r => r.json()).then(data => {
      if (data.success) alert('Payment verified!');
      else alert('Verification failed');
    });
  }
};
const rzp = new Razorpay(options);
rzp.open();
```
