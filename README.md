# BharatBazaar (ভারত বাज़ार) 🛍️
### India's Favorite Shopping App — Complete Flipkart-Style E-Commerce Android Application

A production-grade, native Android application built with **Kotlin, Jetpack Compose, Material 3, and Room Database**.

---

## 📱 How to Download the APK File for Your Mobile

### Option 1: Automatic Download via GitHub Actions (Easiest)
This repository includes a pre-configured GitHub Actions workflow (`.github/workflows/build-apk.yml`):
1. Push this project to your GitHub account (click the **GitHub** button in the AI Studio header).
2. Go to your repository on GitHub and click the **Actions** tab at the top.
3. Select **"Build Android APK"** and click **"Run workflow"** (or simply push any commit).
4. When the build finishes (takes ~1-2 minutes), scroll down to the **Artifacts** section at the bottom of the page.
5. Click **`BharatBazaar-Debug-APK`** to download the ready-to-install `.apk` directly onto your mobile or computer!
6. Open the downloaded `.apk` file on your Android device and tap **Install**.

---

### Option 2: Build Locally with Gradle / Android Studio
If you clone this project to your local computer:
```bash
# Clone the repository
git clone <your-repo-url>
cd <repo-folder>

# Make the Gradle wrapper executable
chmod +x gradlew

# Build the debug APK
./gradlew assembleDebug
```
The output APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```
Connect your phone with a USB cable or transfer `app-debug.apk` to your phone and tap **Install**.

---

## ✨ Features Included

- **Flipkart-Style Dynamic Homepage**:
  - Auto-scrolling festive sale hero banners with dot indicators
  - Category bubbles (Mobiles, Fashion, Electronics, Home, Grocery, Beauty, Footwear)
  - Deal of the Day with real-time countdown timer
  - Best Sellers & Trending fashion sections
  - BharatBazaar Assured trust badges
- **PIN Code Delivery Availability Checker**:
  - Live 6-digit Indian PIN code check (Metro hubs & All-India coverage)
  - Estimated delivery days & Cash on Delivery (COD) eligibility calculation
- **Dual-Pane Category Explorer**:
  - Vertical category rail + subcategory grid with price starting points
- **Product Search & Filtering**:
  - Real-time search by keyword, brand, or category
  - Sorting by Price (Low/High), Customer Rating, and Discount %
  - BharatBazaar Assured toggle filter & in-stock filter
- **Rich Product Details**:
  - High-resolution image galleries
  - Size and Color variant selection
  - Bank offers & coupons ticker
  - Product specifications & highlights
  - Customer review submission & verified buyer ratings
- **Shopping Cart & Coupons**:
  - Reactive cart with quantity steppers (+/-)
  - Promotional coupon engine (`BHARAT500`, `WELCOME10`, `FESTIVE20`)
  - Complete price breakdown (MRP, product discounts, coupon savings, free delivery threshold)
- **Wishlist**:
  - Save favorite items with one-tap "Move to Cart"
- **3-Step Checkout & Payment Options**:
  - Delivery address selection with "Add New Address" modal
  - Cash on Delivery (COD)
  - UPI (Google Pay, PhonePe, Paytm, BHIM)
  - Credit/Debit Cards & Net Banking
- **Order Tracking**:
  - Visual milestone tracker (Confirmed ➔ Packed ➔ Shipped ➔ Out for Delivery ➔ Delivered)
  - Estimated delivery dates & order line items
- **Full Admin Dashboard**:
  - Business analytics (Total Revenue, Total Orders, Pending Shipments, Low Stock Alerts)
  - Product catalog manager (Add new products with prices, stock, images; delete products)
  - Live order status processor (update orders to Confirmed, Shipped, Delivered, or Cancelled)
