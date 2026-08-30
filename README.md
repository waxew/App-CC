# CACTUS Collection — Android

نسخه `2.0.0` اپلیکیشن CACTUS Collection برای فروش پوشاک، سفارش چاپ DTF، طراحی اختصاصی لباس، سفارش تکی/عمده و باشگاه مشتریان است.

## وضعیت نسخه 2.0.0

این نسخه از حالت Prototype خارج شده و مسیر کامل Local-first را روی خود گوشی اجرا می‌کند:

- کاتالوگ محصولات و جستجو
- سبد خرید پایدار با تغییر تعداد و حذف
- ثبت اطلاعات تحویل و ایجاد سفارش
- تاریخچه سفارش، اشتراک خلاصه سفارش و سفارش مجدد
- استودیو طراحی پوشاک: نوع لباس، رنگ، محل چاپ، ابعاد، متن و انتخاب PNG/JPG
- تخمین قیمت طرح و ذخیره پروژه‌های طراحی
- سفارش عمده با تخفیف پلکانی
- باشگاه مشتریان با امتیاز واقعی بر اساس سفارش‌ها
- Drawer راست‌به‌چپ، Back Stack صحیح و نمایش تعداد سبد خرید در Header
- بررسی نسخه جدید با فایل `latest.json`
- قیمت‌ها با جداکننده سه‌رقمی

## تکنولوژی

- Kotlin
- Jetpack Compose
- Material 3
- Application ID: `com.asteam.cactuscollection`
- Version code: `3`
- Version name: `2.0.0`
- Minimum Android: API 24
- Compile / Target SDK: 35
- Java: 17

## معماری داده

`UserPreferences` اطلاعات پروفایل و تنظیمات سبک را نگه می‌دارد. `CactusStore` مسئول سبد، طرح‌های ذخیره‌شده، سفارش‌ها و امتیاز باشگاه است. داده‌های نسخه 2.0.0 به‌صورت Local-first ذخیره می‌شوند تا برنامه بدون Backend هم قابل استفاده و تست باشد.

## سرویس‌های خارجی

موارد زیر عمداً بدون کلید یا سرویس جعلی باقی مانده‌اند و برای فعال شدن نیازمند ارائه‌دهنده واقعی هستند:

- OTP پیامکی
- درگاه پرداخت آنلاین
- Sync ابری سفارش‌ها
- Push Notification
- وضعیت زنده تولید و ارسال
- پنل مدیریت چندکاربره

UI و لایه داده برای اتصال این سرویس‌ها در نسخه سروری آماده شده‌اند.

## Build

```bash
./gradlew assembleRelease
./gradlew assembleDebug
./gradlew lintRelease
```

GitHub Actions نیز Release و Debug را Build می‌کند، Lint و امضای APK را بررسی می‌کند و Artifact خروجی تولید می‌کند.

## قابلیت آپدیت

برای نصب نسخه‌های بعد روی نسخه فعلی:

- `applicationId` تغییر نکند.
- `versionCode` در هر انتشار افزایش یابد.
- APK Release با همان Signing Identity معتبر امضا شود.

## مستندات

- `ARCHITECTURE.md` — معماری و مرز Backend
- `CHANGELOG.md` — تغییرات نسخه‌ها
- `latest.json` — manifest بررسی بروزرسانی

## Repository

`waxew/App-CC`
