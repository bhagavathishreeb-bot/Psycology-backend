# ManoTaranga Backend API Reference

Complete API documentation for the ManoTaranga mental wellness platform backend.

---

## Overview

| Item | Value |
|------|-------|
| **Base URL** | `http://localhost:8080` (or your deployed URL) |
| **Content-Type** | `application/json` (except Career Application) |
| **CORS** | Enabled for `http://localhost:5173`, `http://localhost:3000` |

---

## Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings` | Create counseling session booking |
| POST | `/api/course-purchases` | Create course enrollment |
| POST | `/api/shop-orders` | Create shop order |
| POST | `/api/payments/create-order` | Create Razorpay payment order |
| POST | `/api/payments/webhook` | Razorpay webhook (do not call directly) |
| POST | `/api/chat` | Psychology chatbot (AI) |
| POST | `/api/career-applications` | Submit job application |

---

## 1. Create Booking

Creates a counseling session booking. Sends confirmation email to the customer and notification to admin via Brevo.

**Endpoint:** `POST /api/bookings`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | Full name |
| email | string | Yes | Valid email address |
| age | number | Yes | Age (1–120) |
| occupation | string | Yes | Occupation/job |
| phone | string | Yes | Phone number |
| dob | string | Yes | Date of birth |
| gender | string | Yes | Gender |
| city | string | Yes | City |
| preferredLanguage | string | Yes | Preferred language (e.g. English, Hindi) |
| whatBringsToTherapy | string | Yes | Reason for seeking therapy |
| howLongConcerns | string | Yes | Duration of concerns |
| concerns | object | No | Map of concern names to boolean (e.g. `{"Anxiety": true}`) |
| otherConcern | string | No | Other concerns |
| seenPsychologistBefore | string | No | Yes/No |
| previousDiagnosis | string | No | Previous diagnosis if any |
| diagnosisDuration | string | No | Duration of diagnosis |
| session | string | Yes | Session type (e.g. Counseling Session) |
| sessionDuration | string | Yes | Duration (e.g. 45 mins) |
| sessionPrice | number | Yes | Price (e.g. 599) |

**Sample Payload:**
```json
{
  "name": "Priya Sharma",
  "email": "priya.sharma@example.com",
  "age": 28,
  "occupation": "Software Engineer",
  "phone": "+919876543210",
  "dob": "1996-05-15",
  "gender": "Female",
  "city": "Bangalore",
  "preferredLanguage": "English",
  "whatBringsToTherapy": "I've been feeling anxious and overwhelmed at work for the past few months.",
  "howLongConcerns": "3-6 months",
  "concerns": {
    "Anxiety": true,
    "Depression": false,
    "Stress": true
  },
  "otherConcern": "Sleep issues",
  "seenPsychologistBefore": "No",
  "previousDiagnosis": null,
  "diagnosisDuration": null,
  "session": "Counseling Session",
  "sessionDuration": "45 mins",
  "sessionPrice": 599
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1
}
```

**Error Response:** `400 Bad Request` (validation errors)
```json
{
  "error": "Validation failed",
  "details": {
    "email": "Invalid email format",
    "age": "Age is required"
  }
}
```

---

## 2. Create Course Purchase

Creates a course enrollment when a user enrolls in a course.

**Endpoint:** `POST /api/course-purchases`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| courseId | number | Yes | Course ID |
| courseTitle | string | No | Course title |
| price | number | Yes | Price paid |
| originalPrice | number | No | Original price (if discounted) |
| customerName | string | Yes | Customer name |
| customerEmail | string | Yes | Valid email |
| customerPhone | string | Yes | Phone number |
| paymentStatus | string | No | `pending` / `paid` / `failed` (default: pending) |
| paymentId | string | No | Payment gateway transaction ID |

**Sample Payload:**
```json
{
  "courseId": 101,
  "courseTitle": "Mindfulness & Stress Management",
  "price": 1999,
  "originalPrice": 2999,
  "customerName": "Rahul Verma",
  "customerEmail": "rahul.verma@example.com",
  "customerPhone": "+919123456789",
  "paymentStatus": "pending",
  "paymentId": "razorpay_order_xyz123"
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1
}
```

---

## 3. Create Shop Order

Creates an order when a user purchases shop items (journals, books, etc.).

**Endpoint:** `POST /api/shop-orders`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| items | array | Yes | List of items (min 1) |
| items[].shopItemId | number | Yes | Shop item ID |
| items[].title | string | No | Item title |
| items[].type | string | No | Journal / Book |
| items[].price | number | Yes | Unit price |
| items[].quantity | number | No | Quantity (default: 1) |
| totalAmount | number | Yes | Total order amount |
| customerName | string | Yes | Customer name |
| customerEmail | string | Yes | Valid email |
| customerPhone | string | Yes | Phone number |
| shippingAddress | object | Yes | Shipping address |
| shippingAddress.line1 | string | Yes | Address line 1 |
| shippingAddress.line2 | string | No | Address line 2 |
| shippingAddress.city | string | Yes | City |
| shippingAddress.state | string | Yes | State |
| shippingAddress.pincode | string | Yes | Pincode |
| paymentStatus | string | No | `pending` / `paid` / `failed` |
| paymentId | string | No | Payment gateway ID |

**Sample Payload:**
```json
{
  "items": [
    {
      "shopItemId": 1,
      "title": "Daily Gratitude Journal",
      "type": "Journal",
      "price": 349,
      "quantity": 2
    },
    {
      "shopItemId": 2,
      "title": "Understanding Anxiety",
      "type": "Book",
      "price": 499,
      "quantity": 1
    }
  ],
  "totalAmount": 1197,
  "customerName": "Anita Desai",
  "customerEmail": "anita.desai@example.com",
  "customerPhone": "+919555123456",
  "shippingAddress": {
    "line1": "123, Green Valley Apartments",
    "line2": "Sector 5",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001"
  },
  "paymentStatus": "pending",
  "paymentId": null
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1
}
```

---

## 4. Payment Gateway (Razorpay)

### Payment Flow

1. **Create order** – Call `POST /api/course-purchases` or `POST /api/shop-orders` to create a pending order. Note the returned `id`.
2. **Create Razorpay order** – Call `POST /api/payments/create-order` with the order details.
3. **Open checkout** – Use the returned `razorpayOrderId` and `razorpayKeyId` in Razorpay Checkout on the frontend.
4. **Webhook** – Razorpay sends `payment.captured` or `payment.failed` to your webhook URL. The backend updates the order status automatically.

### 4.1 Create Payment Order

**Endpoint:** `POST /api/payments/create-order`

Call this **after** creating a course purchase or shop order. Returns Razorpay order ID and key for frontend checkout.

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| amount | number | Yes | Amount in rupees (e.g. 599 for ₹599) |
| receipt | string | Yes | Your receipt/order reference (max 40 chars) |
| orderType | string | Yes | `course` or `shop` |
| entityId | number | Yes | ID from course-purchase or shop-order |
| customerName | string | No | Prefill on checkout |
| customerEmail | string | No | Prefill on checkout |

**Sample Payload:**
```json
{
  "amount": 1999,
  "receipt": "course_101_user_1",
  "orderType": "course",
  "entityId": 1,
  "customerName": "Rahul Verma",
  "customerEmail": "rahul@example.com"
}
```

**Success Response:** `200 OK`
```json
{
  "razorpayOrderId": "order_xxxxxxxxxxxx",
  "razorpayKeyId": "rzp_test_xxxxxxxx",
  "amount": 1999,
  "currency": "INR"
}
```

### 4.2 Frontend Checkout (JavaScript)

```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script>
async function payForCourse() {
  // 1. Create course purchase
  const purchaseRes = await fetch('/api/course-purchases', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      courseId: 101,
      courseTitle: 'Mindfulness Course',
      price: 1999,
      customerName: 'Rahul',
      customerEmail: 'rahul@example.com',
      customerPhone: '+919876543210',
      paymentStatus: 'pending'
    })
  });
  const { id } = await purchaseRes.json();

  // 2. Create Razorpay order
  const orderRes = await fetch('/api/payments/create-order', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      amount: 1999,
      receipt: 'course_' + id,
      orderType: 'course',
      entityId: id,
      customerName: 'Rahul',
      customerEmail: 'rahul@example.com'
    })
  });
  const { razorpayOrderId, razorpayKeyId } = await orderRes.json();

  // 3. Open Razorpay Checkout
  const options = {
    key: razorpayKeyId,
    amount: 1999 * 100, // paise
    currency: 'INR',
    name: 'ManoTaranga',
    order_id: razorpayOrderId,
    handler: function(response) {
      alert('Payment successful! ID: ' + response.razorpay_payment_id);
      // Redirect to success page
    },
    prefill: { name: 'Rahul', email: 'rahul@example.com' }
  };
  const rzp = new Razorpay(options);
  rzp.on('payment.failed', function(response) {
    alert('Payment failed: ' + response.error.description);
  });
  rzp.open();
}
</script>
```

### 4.3 Webhook Setup

1. Go to [Razorpay Dashboard → Webhooks](https://dashboard.razorpay.com/app/webhooks)
2. Add URL: `https://your-domain.com/api/payments/webhook`
3. Select events: `payment.captured`, `payment.failed`
4. Copy the **Webhook Secret** and set `RAZORPAY_WEBHOOK_SECRET` in your environment

---

## 5. Psychology Chatbot

AI chatbot for psychology-related questions. Uses Groq (Llama 3.3 70B).

**Endpoint:** `POST /api/chat`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| message | string | Yes | User's question or message |
| history | array | No | Conversation history for multi-turn chat |

**History format:** Each item: `{ "role": "user" | "assistant", "content": "..." }`

**Sample Payload (single message):**
```json
{
  "message": "What are some techniques to manage anxiety?"
}
```

**Sample Payload (with conversation history):**
```json
{
  "message": "Can you elaborate on the breathing technique?",
  "history": [
    { "role": "user", "content": "I feel anxious before meetings" },
    { "role": "assistant", "content": "That's a common experience. Here are a few techniques: 1. Deep breathing (4-7-8 method)..." }
  ]
}
```

**Success Response:** `200 OK`
```json
{
  "reply": "Certainly! The 4-7-8 breathing technique works like this: Inhale for 4 seconds, hold for 7 seconds, and exhale slowly for 8 seconds. This activates your parasympathetic nervous system and helps calm your body. Try practicing it a few times when you're relaxed so it becomes easier to use when anxiety strikes."
}
```

**Error Response:** `400 Bad Request` (if message is empty)
```json
{
  "error": "Validation failed",
  "details": {
    "message": "Message is required"
  }
}
```

---

## 6. Career Application

Submits a job application with resume upload.

**Endpoint:** `POST /api/career-applications`

**Headers:**
```
Content-Type: multipart/form-data
```

**Request Body (form fields):**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | Applicant name |
| email | string | Yes | Valid email |
| phone | string | Yes | Phone number |
| linkedin | string | No | LinkedIn profile URL |
| experience | string | Yes | Work experience details |
| message | string | No | Cover message |
| resume | file | Yes | Resume (PDF, DOC, etc.) |
| jobTitle | string | Yes | Job title applying for |
| jobType | string | Yes | Full-time / Part-time / Contract |
| jobLocation | string | Yes | Job location |

**Sample Request (cURL):**
```bash
curl -X POST http://localhost:8080/api/career-applications \
  -F "name=Dr. Smita Patel" \
  -F "email=smita.patel@example.com" \
  -F "phone=+919876543210" \
  -F "linkedin=https://linkedin.com/in/smitapatel" \
  -F "experience=5 years as clinical psychologist, M.Phil in Clinical Psychology" \
  -F "message=Interested in joining your team." \
  -F "resume=@/path/to/resume.pdf" \
  -F "jobTitle=Senior Psychologist" \
  -F "jobType=Full-time" \
  -F "jobLocation=Bangalore"
```

**Sample Request (JavaScript fetch):**
```javascript
const formData = new FormData();
formData.append('name', 'Dr. Smita Patel');
formData.append('email', 'smita.patel@example.com');
formData.append('phone', '+919876543210');
formData.append('experience', '5 years as clinical psychologist');
formData.append('resume', fileInput.files[0]);
formData.append('jobTitle', 'Senior Psychologist');
formData.append('jobType', 'Full-time');
formData.append('jobLocation', 'Bangalore');

fetch('http://localhost:8080/api/career-applications', {
  method: 'POST',
  body: formData
});
```

**Success Response:** `201 Created`
```json
{
  "id": 1
}
```

**Error Response:** `400 Bad Request` (missing required fields)
```json
{
  "error": "Required fields: name, email, phone, experience, resume, jobTitle, jobType, jobLocation"
}
```

---

## Error Handling

| Status | Description |
|--------|-------------|
| 400 | Bad Request – validation errors, missing/invalid fields |
| 500 | Internal Server Error – server-side failure |

**Validation Error Format:**
```json
{
  "error": "Validation failed",
  "details": {
    "fieldName": "Error message for this field"
  }
}
```

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Server port | 8080 |
| RAZORPAY_KEY_ID | Razorpay API Key ID | - |
| RAZORPAY_KEY_SECRET | Razorpay API Key Secret | - |
| RAZORPAY_WEBHOOK_SECRET | Webhook Secret (for signature verification) | - |
| DB_URL | Database JDBC URL | H2 in-memory |
| DB_USERNAME | Database username | sa |
| DB_PASSWORD | Database password | (empty) |
| BREVO_API_KEY | Brevo email API key | - |
| BREVO_FROM_EMAIL | Sender email | noreply@manotaranga.com |
| BREVO_FROM_NAME | Sender name | ManoTaranga |
| BREVO_ADMIN_EMAIL | Admin notification email | admin@manotaranga.com |
| GROQ_API_KEY | Groq AI API key | - |
| GROQ_MODEL | Chatbot model ID | llama-3.3-70b-versatile |
| CORS_ORIGINS | Allowed CORS origins (comma-separated) | localhost:5173, localhost:3000 |
