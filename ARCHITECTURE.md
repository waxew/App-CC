# Architecture — CACTUS Collection 2.0.0

## UI

- `CactusCollectionApp.kt`: ناوبری مرکزی و Back Stack.
- `ScreensOnboarding.kt`: Splash، ثبت‌نام محلی و فرم پروفایل.
- `ScreensMain.kt`: پوسته اصلی، Header، Drawer و Bottom Navigation.
- `Commerce.kt`: کاتالوگ، جستجو، سبد، Checkout، سفارش‌ها، استودیو طراحی، عمده و باشگاه.
- `ScreensInfo.kt`: تنظیمات، بررسی نسخه، پروفایل و صفحات اطلاعاتی.
- `ui/theme/*`: Design System فعلی.

## Local-first Data

`CactusStore` یک API محلی برای Commerce فراهم می‌کند و از SharedPreferences + JSON استفاده می‌کند. در Backend آینده این API می‌تواند به Repository شبکه/Room مهاجرت کند بدون بازنویسی صفحه‌ها.

Entities پیشنهادی سرور:

- users
- products / variants
- designs
- orders / order_items
- order_status_history
- loyalty_transactions
- coupons
- app_versions

Storage پیشنهادی:

- user-artwork
- product-media
- order-mockups

## External Integration Boundary

OTP، Payment، Cloud Sync، Push و وضعیت واقعی چاپ/ارسال نیازمند Credential واقعی هستند و هیچ Secret جعلی داخل برنامه قرار نمی‌گیرد.

## Update

نسخه نصب‌شده از `BuildConfig.VERSION_CODE` با `latest.json` مقایسه می‌شود. manifest می‌تواند URL APK جدید را اعلام کند.
