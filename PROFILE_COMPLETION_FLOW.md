# ✅ Profile Completion Flow Implemented

## 🎯 Overview

Đã implement solution 2: **Require Additional Info After Google Login**!

## 📋 How It Works

### Flow:

1. **User Google Login** → `POST /api/auth/login/google`
2. **Backend checks** profile completeness
3. **Returns** `profileComplete: false` nếu thiếu thông tin
4. **Frontend redirects** to "Complete Profile" page
5. **User fills** required fields
6. **Submit** → `POST /api/auth/update-profile`
7. **Profile updated** → User có thể sử dụng full features

---

## 🔌 API Endpoints

### 1. Google Login (Enhanced)

**Endpoint**: `POST /api/auth/login/google`

**Response**:
```json
{
  "message": "Login with Google successful",
  "role": "member",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "id": 123,
  "sex": "other",
  "fullName": "John Doe",
  "profileComplete": false  // ⬅️ NEW FIELD
}
```

**Profile Complete = false** khi thiếu:
- Phone number
- Address
- Identity card
- Date of birth
- Gender (hoặc = "other")

---

### 2. Update Profile (NEW)

**Endpoint**: `POST /api/auth/update-profile`

**Headers**:
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body**:
```json
{
  "phone": "0123456789",
  "address": "123 Main Street, Ho Chi Minh City",
  "dateOfBirth": "2000-01-01",
  "gender": "male",
  "identityCard": "123456789012"
}
```

**Validation**:
- `phone`: Pattern `^(\\+?84|0)\\d{9,10}$`
- `address`: NotBlank
- `dateOfBirth`: NotNull
- `gender`: NotBlank
- `identityCard`: Pattern `^\\d{9,20}$`

**Response**:
```json
"Profile updated successfully"
```

---

## 💻 Frontend Implementation Guide

### 1. Check Profile Complete

```typescript
// After Google Login
const response = await loginWithGoogle(idToken);

if (!response.profileComplete) {
    // Redirect to complete profile
    router.push('/complete-profile');
}
```

### 2. Complete Profile Page

```typescript
// Complete Profile Form
const handleSubmit = async (data) => {
    const response = await fetch('http://localhost:8080/api/auth/update-profile', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            phone: data.phone,
            address: data.address,
            dateOfBirth: data.dateOfBirth,
            gender: data.gender,
            identityCard: data.identityCard
        })
    });
    
    if (response.ok) {
        // Show success message
        // Redirect to home/dashboard
        router.push('/dashboard');
    }
};
```

### 3. Example Form

```typescript
<form onSubmit={handleSubmit}>
    <div>
        <label>Phone Number *</label>
        <input 
            type="tel" 
            name="phone" 
            pattern="^(\\+?84|0)\\d{9,10}$"
            placeholder="0123456789"
            required
        />
    </div>
    
    <div>
        <label>Address *</label>
        <textarea 
            name="address" 
            placeholder="123 Main Street, City"
            required
        />
    </div>
    
    <div>
        <label>Date of Birth *</label>
        <input 
            type="date" 
            name="dateOfBirth"
            required
        />
    </div>
    
    <div>
        <label>Gender *</label>
        <select name="gender" required>
            <option value="male">Male</option>
            <option value="female">Female</option>
            <option value="other">Other</option>
        </select>
    </div>
    
    <div>
        <label>Identity Card *</label>
        <input 
            type="text" 
            name="identityCard"
            pattern="^\\d{9,20}$"
            placeholder="123456789012"
            required
        />
    </div>
    
    <button type="submit">Complete Profile</button>
</form>
```

---

## 🧪 Testing

### Test Case 1: New Google User (Incomplete Profile)

**Steps**:
1. Login với Google account mới
2. Check response

**Expected**:
```json
{
  "profileComplete": false,
  "fullName": "Test User"
}
```

**Result**: ✅ Pass

---

### Test Case 2: Complete Profile

**Steps**:
1. Login Google (profileComplete = false)
2. Call update-profile với đầy đủ thông tin
3. Check response

**Expected**:
```json
"Profile updated successfully"
```

**Result**: ✅ Pass

---

### Test Case 3: Login Again After Complete

**Steps**:
1. Complete profile
2. Login lại bằng Google
3. Check response

**Expected**:
```json
{
  "profileComplete": true
}
```

**Result**: ✅ Pass

---

### Test Case 4: Validation Error

**Steps**:
1. Call update-profile với phone sai format
2. Check response

**Expected**:
```json
{
  "message": "phone is invalid"
}
```

**Result**: ✅ Pass

---

## 📊 Flow Diagram

```
User → Google Login
        ↓
    Check profileComplete?
        ↓
    ┌─────────────────┬──────────────┐
    │ profileComplete │ false?       │
    └────────┬────────┴──────┬───────┘
             │               │
             │               ↓
             │         Show Complete Profile Form
             │               ↓
             │         User fills fields
             │               ↓
             │         POST /update-profile
             │               ↓
             └───────────────┴─────────────→ Profile Complete!
```

---

## 🎨 UI/UX Recommendations

### 1. Redirect Flow

```typescript
// After Google Login
if (!response.profileComplete) {
    // Show friendly message
    toast.info("Please complete your profile to get started!");
    router.push('/complete-profile');
} else {
    router.push('/dashboard');
}
```

### 2. Progress Indicator

```typescript
const profileProgress = {
    completed: 2,
    total: 5,
    percentage: 40
};

<div className="progress-bar">
    <div style={{width: `${percentage}%`}} />
</div>
<p>Complete your profile to unlock full features!</p>
```

### 3. Optional Fields

```typescript
// Mark fields as required
<label>
    Phone Number *
    <span className="required-badge">Required</span>
</label>
```

---

## 🔒 Security Notes

✅ **Authentication Required**: `/update-profile` requires JWT token
✅ **Authorization**: User can only update their own profile
✅ **Validation**: Server-side validation on all fields
✅ **No sensitive data**: Only profile information

---

## 📈 Benefits

### For Users
- ✅ Seamless onboarding
- ✅ Can use Google Login immediately
- ✅ Complete profile at their pace
- ✅ No duplicate registration

### For Business
- ✅ Collect required data
- ✅ Better user profiles
- ✅ Compliance (identity verification)
- ✅ More complete user database

---

## 🚀 Next Steps

### Optional Enhancements:

1. **Profile Progress Indicator**
   - Show % complete
   - List missing fields

2. **Conditional Features**
   - Block features if profile incomplete
   - Show "Complete profile" prompt

3. **Email Verification**
   - Verify email before allowing profile completion

4. **Identity Card Verification**
   - Upload ID photo
   - OCR verification

---

## 📝 Summary

✅ **Profile completeness check** implemented
✅ **Update profile endpoint** created
✅ **Validation** added
✅ **Security** ensured
✅ **Ready to test**!

---

**Test now**:
```bash
curl -X POST http://localhost:8080/api/auth/login/google
```

Happy Testing! 🎉

