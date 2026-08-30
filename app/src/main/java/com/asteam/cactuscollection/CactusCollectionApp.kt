// [AS-TEAM-DOCUMENTED]
// این فایل هسته‌ی ناوبری و وضعیت کاربر در برنامه CACTUS Collection است.
// هدف کامنت‌ها این است که هنگام باز کردن سورس روی لپ‌تاپ، نقش هر بخش و هر دستور مهم مشخص باشد.
package com.asteam.cactuscollection

// Context برای دسترسی به SharedPreferences و سرویس‌های پایه‌ی اندروید استفاده می‌شود.
import android.content.Context
// BackHandler کلید Back اندروید را داخل Compose مدیریت می‌کند تا کاربر بی‌دلیل از برنامه خارج نشود.
import androidx.activity.compose.BackHandler
// Composable مشخص می‌کند تابع، رابط کاربری Compose تولید می‌کند.
import androidx.compose.runtime.Composable
// CompositionLocalProvider جهت نمایش رابط فارسی را برای کل درخت UI فراهم می‌کند.
import androidx.compose.runtime.CompositionLocalProvider
// getValue امکان استفاده از delegated state با عبارت by را فراهم می‌کند.
import androidx.compose.runtime.getValue
// mutableStateListOf برای نگهداری تاریخچه‌ی صفحات و پیاده‌سازی Back Stack استفاده می‌شود.
import androidx.compose.runtime.mutableStateListOf
// mutableStateOf وضعیت‌های قابل مشاهده توسط Compose را ایجاد می‌کند.
import androidx.compose.runtime.mutableStateOf
// remember مقدار را بین recompositionها حفظ می‌کند.
import androidx.compose.runtime.remember
// setValue امکان تغییر delegated state با عبارت by را فراهم می‌کند.
import androidx.compose.runtime.setValue
// LocalContext کانتکست Activity جاری را داخل Compose در اختیار ما می‌گذارد.
import androidx.compose.ui.platform.LocalContext
// LocalLayoutDirection جهت نوشتار را برای UI کنترل می‌کند.
import androidx.compose.ui.platform.LocalLayoutDirection
// LayoutDirection.Rtl جهت راست‌به‌چپ مناسب رابط فارسی است.
import androidx.compose.ui.unit.LayoutDirection

// این enum تمام مقصدهای اصلی برنامه را تعریف می‌کند؛ هر مقدار نماینده‌ی یک صفحه است.
internal enum class AppScreen {
    // صفحه‌ی آغازین و نمایش لوگو.
    SPLASH,
    // مرحله‌ی دریافت شماره موبایل.
    SIGN_UP,
    // فرم تکمیل مشخصات کاربر.
    PROFILE_FORM,
    // صفحه‌ی اصلی برنامه.
    HOME,
    // باشگاه مشتریان.
    CLUB,
    // فهرست و پیگیری سفارش‌ها.
    ORDERS,
    // تنظیمات برنامه.
    SETTINGS,
    // درباره‌ی تیم توسعه.
    ABOUT_US,
    // راه‌های ارتباط با پشتیبانی.
    CONTACT_US,
    // توضیح کوتاه درباره‌ی خود نرم‌افزار و نسخه.
    ABOUT_APP,
    // حساب کاربری و مشخصات مشتری.
    PROFILE,
    // کاتالوگ دسته‌بندی انتخاب‌شده.
    CATALOG,
    // صفحه جستجوی محصولات و خدمات.
    SEARCH,
    // سبد خرید کاربر.
    CART,
    // استودیوی طراحی اختصاصی پوشاک.
    DESIGN_STUDIO,
    // محاسبه و ثبت سفارش عمده.
    WHOLESALE,
    // پروژه‌های طراحی ذخیره‌شده.
    SAVED_DESIGNS,
    // مرحله ثبت اطلاعات تحویل و نهایی‌سازی سفارش.
    CHECKOUT
}

// مدل ساده‌ی اطلاعات پروفایل که در حافظه‌ی محلی برنامه ذخیره می‌شود.
internal data class UserProfile(
    // شماره موبایل کاربر.
    val phone: String = "",
    // نام کاربر.
    val firstName: String = "",
    // نام خانوادگی کاربر.
    val lastName: String = "",
    // شهر کاربر.
    val city: String = "",
    // ایمیل اختیاری کاربر.
    val email: String = ""
) {
    // fullName نام و نام خانوادگی را بدون فاصله‌های اضافه در یک رشته ترکیب می‌کند.
    val fullName: String
        // فقط بخش‌های غیرخالی انتخاب و سپس با یک فاصله به هم متصل می‌شوند.
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

// این کلاس مسئول خواندن و نوشتن اطلاعات سبک کاربر در SharedPreferences است.
internal class UserPreferences(context: Context) {
    // فایل تنظیمات خصوصی برنامه؛ فقط همین اپلیکیشن به آن دسترسی مستقیم دارد.
    private val prefs = context.getSharedPreferences("cactus_user", Context.MODE_PRIVATE)

    // اگر کاربر ثبت‌نام کرده یا قبلاً ثبت‌نام را رد کرده باشد، onboarding تمام‌شده محسوب می‌شود.
    fun isOnboarded(): Boolean = prefs.getBoolean("registered", false) || prefs.getBoolean("skipped", false)
    // مشخص می‌کند کاربر واقعاً فرم ثبت‌نام را کامل کرده است یا خیر.
    fun isRegistered(): Boolean = prefs.getBoolean("registered", false)

    // این تابع انتخاب «بعداً ثبت‌نام می‌کنم» را ذخیره می‌کند.
    fun skipRegistration() {
        // مقدار skipped به‌صورت غیرهمزمان روی حافظه ذخیره می‌شود.
        prefs.edit().putBoolean("skipped", true).apply()
    }

    // مشخصات تکمیل‌شده‌ی کاربر را در حافظه‌ی محلی ذخیره می‌کند.
    fun saveProfile(profile: UserProfile) {
        // ویرایشگر SharedPreferences برای ثبت چند مقدار به‌صورت یکجا ساخته می‌شود.
        prefs.edit()
            // شماره موبایل ذخیره می‌شود.
            .putString("phone", profile.phone)
            // نام ذخیره می‌شود.
            .putString("first_name", profile.firstName)
            // نام خانوادگی ذخیره می‌شود.
            .putString("last_name", profile.lastName)
            // شهر ذخیره می‌شود.
            .putString("city", profile.city)
            // ایمیل ذخیره می‌شود.
            .putString("email", profile.email)
            // علامت ثبت‌نام موفق فعال می‌شود.
            .putBoolean("registered", true)
            // چون ثبت‌نام کامل شده، وضعیت رد کردن ثبت‌نام پاک می‌شود.
            .putBoolean("skipped", false)
            // تغییرات بدون مسدود کردن UI نوشته می‌شوند.
            .apply()
    }

    // اطلاعات ذخیره‌شده را به مدل UserProfile تبدیل می‌کند.
    fun loadProfile(): UserProfile = UserProfile(
        // اگر شماره‌ای ذخیره نشده باشد، رشته‌ی خالی برگردانده می‌شود.
        phone = prefs.getString("phone", "").orEmpty(),
        // نام از SharedPreferences خوانده می‌شود.
        firstName = prefs.getString("first_name", "").orEmpty(),
        // نام خانوادگی از SharedPreferences خوانده می‌شود.
        lastName = prefs.getString("last_name", "").orEmpty(),
        // شهر از SharedPreferences خوانده می‌شود.
        city = prefs.getString("city", "").orEmpty(),
        // ایمیل از SharedPreferences خوانده می‌شود.
        email = prefs.getString("email", "").orEmpty()
    )

    // وضعیت فعال یا غیرفعال بودن اعلان‌ها را ذخیره می‌کند.
    fun setNotificationsEnabled(enabled: Boolean) {
        // مقدار جدید اعلان‌ها در تنظیمات خصوصی برنامه نوشته می‌شود.
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    // وضعیت فعلی اعلان‌ها را می‌خواند؛ مقدار پیش‌فرض true است.
    fun notificationsEnabled(): Boolean = prefs.getBoolean("notifications", true)
}

// ریشه‌ی رابط کاربری برنامه؛ تمام صفحه‌ها و مسیرهای ناوبری از اینجا کنترل می‌شوند.
@Composable
fun CactusCollectionApp() {
    // کل رابط به‌صورت RTL نمایش داده می‌شود تا چیدمان فارسی درست باشد.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        // Context فعلی برای ساخت UserPreferences دریافت می‌شود.
        val context = LocalContext.current
        // نمونه‌ی تنظیمات فقط یک‌بار ساخته و بین recompositionها حفظ می‌شود.
        val preferences = remember { UserPreferences(context) }
        // فروشگاه محلی فقط یک‌بار ساخته می‌شود تا سبد، سفارش‌ها و طرح‌ها پایدار بمانند.
        val store = remember { CactusStore(context) }
        // صفحه‌ی جاری ابتدا Splash است.
        var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
        // تاریخچه‌ی صفحات برای عملکرد صحیح دکمه‌ی Back نگهداری می‌شود.
        val backStack = remember { mutableStateListOf<AppScreen>() }
        // پروفایل موجود در شروع برنامه از حافظه‌ی محلی بازیابی می‌شود.
        var profile by remember { mutableStateOf(preferences.loadProfile()) }
        // شماره‌ی موقت هنگام جابه‌جایی بین مراحل ثبت‌نام نگهداری می‌شود.
        var pendingPhone by remember { mutableStateOf(profile.phone) }
        // دسته‌ی پیش‌فرض کاتالوگ روی تیشرت قرار می‌گیرد.
        var selectedCatalog by remember { mutableStateOf("تیشرت") }

        // این تابع تمام تغییر صفحه‌ها را از یک نقطه مدیریت می‌کند تا Back Stack قابل پیش‌بینی باشد.
        fun navigateTo(destination: AppScreen, keepHistory: Boolean = true) {
            // اگر مقصد همان صفحه‌ی فعلی باشد، هیچ کاری انجام نمی‌شود تا تاریخچه تکراری نشود.
            if (destination == currentScreen) return

            // رفتن به خانه به‌معنای شروع مسیر اصلی است؛ تاریخچه پاک می‌شود تا Back دوباره وارد صفحه‌ی قبلی نشود.
            if (destination == AppScreen.HOME) {
                // پاک کردن تاریخچه باعث می‌شود Back از صفحه خانه رفتار طبیعی اندروید را داشته باشد.
                backStack.clear()
            // در سایر مقصدها، در صورت درخواست، صفحه‌ی فعلی به تاریخچه اضافه می‌شود.
            } else if (keepHistory && currentScreen != AppScreen.SPLASH) {
                // جلوگیری از ثبت چندباره‌ی یک صفحه‌ی یکسان در انتهای Back Stack.
                if (backStack.lastOrNull() != currentScreen) {
                    // صفحه‌ی فعلی به آخر تاریخچه اضافه می‌شود.
                    backStack.add(currentScreen)
                }
            }

            // در پایان صفحه‌ی مقصد به‌عنوان صفحه‌ی فعال تعیین می‌شود.
            currentScreen = destination
        }

        // این تابع یک مرحله به عقب برمی‌گردد و اگر تاریخچه‌ای نباشد، کاربر را به خانه هدایت می‌کند.
        fun navigateBack() {
            // مقصد برگشت بر اساس وجود یا نبود تاریخچه تعیین می‌شود.
            currentScreen = if (backStack.isNotEmpty()) {
                // آخرین صفحه از Back Stack هم‌زمان حذف و به‌عنوان مقصد برگشت استفاده می‌شود.
                backStack.removeAt(backStack.lastIndex)
            } else {
                // برای جلوگیری از خروج ناخواسته از صفحات داخلی، مقصد امن خانه است.
                AppScreen.HOME
            }
        }

        // تا وقتی کاربر داخل صفحه‌ای غیر از Splash و Home است، Back توسط برنامه مدیریت می‌شود.
        BackHandler(enabled = currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.HOME) {
            // با فشردن Back به صفحه‌ی قبلی واقعی یا در نهایت خانه برمی‌گردیم.
            navigateBack()
        }

        // بر اساس صفحه‌ی جاری، Composable مناسب نمایش داده می‌شود.
        when (currentScreen) {
            // صفحه‌ی Splash فقط در شروع برنامه نمایش داده می‌شود.
            AppScreen.SPLASH -> SplashScreen(
                // پس از پایان Splash تصمیم می‌گیریم onboarding لازم است یا مستقیماً خانه نمایش داده شود.
                onFinished = {
                    // این جابه‌جایی در تاریخچه ثبت نمی‌شود چون Splash نباید مقصد Back باشد.
                    navigateTo(
                        destination = if (preferences.isOnboarded()) AppScreen.HOME else AppScreen.SIGN_UP,
                        keepHistory = false
                    )
                }
            )

            // صفحه‌ی دریافت شماره موبایل.
            AppScreen.SIGN_UP -> SignUpScreen(
                // اگر قبلاً شماره‌ای وجود داشته باشد داخل فرم قرار می‌گیرد.
                initialPhone = pendingPhone,
                // با ادامه دادن، شماره ذخیره‌ی موقت و فرم تکمیل پروفایل باز می‌شود.
                onContinue = { phone ->
                    // شماره‌ی واردشده برای مرحله‌ی بعد نگهداری می‌شود.
                    pendingPhone = phone
                    // رفتن به فرم پروفایل در Back Stack ثبت می‌شود تا Back به ثبت‌نام برگردد.
                    navigateTo(AppScreen.PROFILE_FORM)
                },
                // کاربر می‌تواند ثبت‌نام را فعلاً رد کند.
                onSkip = {
                    // انتخاب رد کردن onboarding ذخیره می‌شود.
                    preferences.skipRegistration()
                    // سپس کاربر به صفحه‌ی خانه می‌رود.
                    navigateTo(AppScreen.HOME, keepHistory = false)
                }
            )

            // فرم تکمیل یا ویرایش اطلاعات کاربر.
            AppScreen.PROFILE_FORM -> ProfileFormScreen(
                // شماره‌ی مرحله قبل به فرم داده می‌شود.
                phone = pendingPhone,
                // اطلاعات قبلی برای حالت ویرایش در فرم قرار می‌گیرد.
                initial = profile,
                // دکمه‌ی برگشت داخل فرم از همان Back Stack مرکزی استفاده می‌کند.
                onBack = { navigateBack() },
                // پس از ذخیره‌ی موفق اطلاعات این callback اجرا می‌شود.
                onSave = { saved ->
                    // state پروفایل در حافظه‌ی UI به‌روز می‌شود.
                    profile = saved
                    // نسخه‌ی جدید پروفایل روی حافظه‌ی محلی دستگاه نوشته می‌شود.
                    preferences.saveProfile(saved)
                    // پس از ثبت اطلاعات، کاربر به خانه برمی‌گردد و تاریخچه پاک می‌شود.
                    navigateTo(AppScreen.HOME, keepHistory = false)
                }
            )

            // تمام صفحه‌های اصلی بعد از onboarding داخل پوسته‌ی مشترک شامل نوار بالا، Drawer و Bottom Navigation نمایش داده می‌شوند.
            else -> MainShell(
                // صفحه‌ی فعال برای انتخاب محتوای داخل پوسته ارسال می‌شود.
                currentScreen = currentScreen,
                // اطلاعات پروفایل برای نمایش نام و حساب کاربری ارسال می‌شود.
                profile = profile,
                // store وضعیت فروشگاهی Local-first را در اختیار پوسته اصلی قرار می‌دهد.
                store = store,
                // تنظیمات محلی در اختیار صفحات تنظیمات قرار می‌گیرد.
                preferences = preferences,
                // نام دسته‌ی انتخاب‌شده برای کاتالوگ ارسال می‌شود.
                selectedCatalog = selectedCatalog,
                // انتخاب هر دسته از خانه، صفحه‌ی کاتالوگ را باز می‌کند.
                onCatalogSelected = { category ->
                    // نام دسته‌ی انتخابی ذخیره می‌شود.
                    selectedCatalog = category
                    // کاتالوگ باز و صفحه‌ی قبلی در Back Stack ثبت می‌شود.
                    navigateTo(AppScreen.CATALOG)
                },
                // تمام مقصدهای Drawer و Bottom Navigation از تابع مرکزی ناوبری عبور می‌کنند.
                onNavigate = { destination -> navigateTo(destination) },
                // ویرایش حساب کاربری از پروفایل یا Drawer انجام می‌شود.
                onEditProfile = {
                    // شماره فعلی برای جلوگیری از ورود دوباره در فرم نگهداری می‌شود.
                    pendingPhone = profile.phone
                    // کاربر ثبت‌شده مستقیماً فرم پروفایل و کاربر مهمان صفحه ثبت‌نام را می‌بیند.
                    navigateTo(if (preferences.isRegistered()) AppScreen.PROFILE_FORM else AppScreen.SIGN_UP)
                }
            )
        }
    }
}
