// [AS-TEAM-DOCUMENTED]
// فایل ScreensInfo.kt: این فایل بخشی از سورس CACTUS Collection است و کامنت‌های زیر برای توضیح منطق، UI و مسئولیت قسمت‌های مهم اضافه شده‌اند.
package com.asteam.cactuscollection

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asteam.cactuscollection.ui.theme.CactusBackground
import com.asteam.cactuscollection.ui.theme.CactusLavender
import com.asteam.cactuscollection.ui.theme.CactusMint
import com.asteam.cactuscollection.ui.theme.CactusMuted
import com.asteam.cactuscollection.ui.theme.CactusPurple
import com.asteam.cactuscollection.ui.theme.CactusPurpleDark
import com.asteam.cactuscollection.ui.theme.CactusText

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
internal fun OrdersScreen() {
    // Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند.
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box برای هم‌پوشانی یا تراز دقیق عناصر این بخش استفاده می‌شود.
        Box(
            Modifier.size(98.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(Icons.Rounded.ReceiptLong, null, tint = CactusPurple, modifier = Modifier.size(50.dp))
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(18.dp))
        Text("هنوز سفارشی ثبت نشده", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CactusText)
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(8.dp))
        Text(
            "بعد از ثبت سفارش، وضعیت بررسی طرح، چاپ، بسته‌بندی و ارسال از همین بخش قابل پیگیری خواهد بود.",
            textAlign = TextAlign.Center,
            color = CactusMuted,
            lineHeight = 21.sp
        )
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
internal fun SettingsScreen(preferences: UserPreferences) {
    // متغیر notificationsEnabled یک مقدار قابل‌تغییر موردنیاز این بخش را نگهداری می‌کند.
    var notificationsEnabled by remember { mutableStateOf(preferences.notificationsEnabled()) }
    val context = LocalContext.current

    // LazyColumn محتوای عمودی را بهینه و قابل اسکرول نمایش می‌دهد.
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingCard(
                icon = { Icon(Icons.Rounded.Notifications, null, tint = CactusPurple) },
                title = "اعلان‌ها",
                subtitle = "اطلاع‌رسانی وضعیت سفارش‌ها، پیشنهادها و باشگاه مشتریان"
            ) {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        preferences.setNotificationsEnabled(it)
                    }
                )
            }
        }
        item {
            // Card محتوای این بخش را داخل یک سطح مجزا و خوانا نمایش می‌دهد.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp)
            ) {
                // Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند.
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Box برای هم‌پوشانی یا تراز دقیق عناصر این بخش استفاده می‌شود.
                        Box(
                            Modifier.size(46.dp).clip(CircleShape).background(CactusMint),
                            contentAlignment = Alignment.Center
                        ) {
                            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
                            Icon(Icons.Rounded.Update, null, tint = CactusPurpleDark)
                        }
                        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
                            Text("بررسی بروزرسانی", fontWeight = FontWeight.ExtraBold)
                            Text("بررسی نسخه جدید برنامه", color = CactusMuted, fontSize = 12.sp)
                        }
                    }
                    // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "سرویس بروزرسانی خودکار پس از اتصال سرور فعال می‌شود.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
                    ) {
                        // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
                        Text("بررسی نسخه جدید", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
private fun SettingCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    // Card محتوای این بخش را داخل یک سطح مجزا و خوانا نمایش می‌دهد.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp)
    ) {
        // Row عناصر رابط کاربری این قسمت را در یک ردیف افقی قرار می‌دهد.
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).clip(CircleShape).background(CactusLavender),
                contentAlignment = Alignment.Center
            ) { icon() }
            // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
                Text(title, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = CactusMuted, fontSize = 12.sp)
            }
            trailing()
        }
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
internal fun AboutUsScreen() {
    CenterInfoPage(
        icon = Icons.Rounded.Workspaces,
        title = "گروه توسعه و برنامه نویسی AS Team",
        body = "تمامی حقوق مربوط به این برنامه انحصاری میباشد"
    )
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
internal fun ContactUsScreen() {
    // متغیر context یک مقدار ثابت/مرجع موردنیاز این بخش را نگهداری می‌کند.
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box برای هم‌پوشانی یا تراز دقیق عناصر این بخش استفاده می‌شود.
        Box(
            Modifier.size(94.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(Icons.Rounded.Email, null, tint = CactusPurple, modifier = Modifier.size(48.dp))
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(20.dp))
        Text("گروه توسعه و برنامه نویسی AS Team", fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(18.dp))
        Text("ایمیل پشتیبانی", color = CactusMuted)
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(5.dp))
        Text("as.team.support@gmail.com", color = CactusPurpleDark, fontWeight = FontWeight.Bold)
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = {
                // متغیر intent یک مقدار ثابت/مرجع موردنیاز این بخش را نگهداری می‌کند.
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:as.team.support@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی CACTUS Collection")
                }
                // runCatching خطاهای احتمالی این عملیات را بدون کرش کردن برنامه مدیریت می‌کند.
                runCatching { context.startActivity(intent) }
                    .onFailure { Toast.makeText(context, "برنامه ایمیل پیدا نشد.", Toast.LENGTH_SHORT).show() }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(Icons.Rounded.Email, null)
            Spacer(Modifier.width(8.dp))
            // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
            Text("ارسال ایمیل")
        }
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
internal fun AboutAppScreen() {
    // صفحه درباره نرم‌افزار عمداً کوتاه است و هیچ نام بسته یا جزئیات فنی داخلی را به کاربر نمایش نمی‌دهد.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CactusBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // لوگوی اصلی برند.
        Image(
            painter = painterResource(R.drawable.cactus_logo),
            contentDescription = "CACTUS Collection",
            modifier = Modifier.size(144.dp)
        )

        Spacer(Modifier.height(20.dp))

        // نام برنامه.
        Text(
            "CACTUS Collection",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = CactusText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // فقط توضیح کاربردی کوتاه درباره عملکرد برنامه.
        Text(
            """برنامه‌ای برای مشاهده محصولات پوشاک، سفارش چاپ DTF اختصاصی، انتخاب طرح‌های آماده، ثبت سفارش تکی یا عمده و پیگیری سفارش‌ها.

طراحی شده برای ساده‌تر شدن سفارش چاپ و پوشاک اختصاصی CACTUS Collection.""".trimIndent(),
            color = CactusMuted,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        // شماره نسخه مستقیماً از BuildConfig خوانده می‌شود تا همیشه با خروجی واقعی یکسان باشد.
        Text(
            "نسخه ${BuildConfig.VERSION_NAME}",
            color = CactusPurpleDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun ProfileScreen(profile: UserProfile, onEdit: () -> Unit) {
    // Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند.
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(28.dp))
        Box(
            Modifier.size(104.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(Icons.Rounded.AccountCircle, null, tint = CactusPurple, modifier = Modifier.size(72.dp))
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(14.dp))
        Text(
            profile.fullName.ifBlank { "مهمان CACTUS" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        // این شرط بررسی می‌کند آیا اجرای شاخه‌ی بعدی لازم است یا خیر.
        if (profile.phone.isNotBlank()) {
            Text(profile.phone, color = CactusMuted)
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(22.dp)
        ) {
            // Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند.
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileLine("نام", profile.fullName.ifBlank { "ثبت نشده" })
                ProfileLine("شهر", profile.city.ifBlank { "ثبت نشده" })
                ProfileLine("ایمیل", profile.email.ifBlank { "ثبت نشده" })
            }
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(Icons.Rounded.Settings, null)
            Spacer(Modifier.width(8.dp))
            // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
            Text(if (profile.firstName.isBlank()) "ثبت‌نام / تکمیل حساب" else "ویرایش اطلاعات", fontWeight = FontWeight.Bold)
        }
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
private fun ProfileLine(label: String, value: String) {
    // Row عناصر رابط کاربری این قسمت را در یک ردیف افقی قرار می‌دهد.
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = CactusMuted, fontSize = 12.sp, modifier = Modifier.width(70.dp))
        // این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد.
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
private fun CenterInfoPage(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    // Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند.
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Box برای هم‌پوشانی یا تراز دقیق عناصر این بخش استفاده می‌شود.
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            // این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد.
            Icon(icon, null, tint = CactusPurple, modifier = Modifier.size(50.dp))
        }
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(20.dp))
        Text(title, textAlign = TextAlign.Center, fontSize = 21.sp, fontWeight = FontWeight.Black)
        // Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند.
        Spacer(Modifier.height(11.dp))
        Text(body, textAlign = TextAlign.Center, color = CactusMuted, lineHeight = 22.sp)
    }
}
