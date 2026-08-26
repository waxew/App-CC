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

@Composable
internal fun OrdersScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(98.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.ReceiptLong, null, tint = CactusPurple, modifier = Modifier.size(50.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text("هنوز سفارشی ثبت نشده", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CactusText)
        Spacer(Modifier.height(8.dp))
        Text(
            "بعد از ثبت سفارش، وضعیت بررسی طرح، چاپ، بسته‌بندی و ارسال از همین بخش قابل پیگیری خواهد بود.",
            textAlign = TextAlign.Center,
            color = CactusMuted,
            lineHeight = 21.sp
        )
    }
}

@Composable
internal fun SettingsScreen(preferences: UserPreferences) {
    var notificationsEnabled by remember { mutableStateOf(preferences.notificationsEnabled()) }
    val context = LocalContext.current

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(46.dp).clip(CircleShape).background(CactusMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Update, null, tint = CactusPurpleDark)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("بررسی بروزرسانی", fontWeight = FontWeight.ExtraBold)
                            Text("بررسی نسخه جدید برنامه", color = CactusMuted, fontSize = 12.sp)
                        }
                    }
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
                        Text("بررسی نسخه جدید", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).clip(CircleShape).background(CactusLavender),
                contentAlignment = Alignment.Center
            ) { icon() }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = CactusMuted, fontSize = 12.sp)
            }
            trailing()
        }
    }
}

@Composable
internal fun AboutUsScreen() {
    CenterInfoPage(
        icon = Icons.Rounded.Workspaces,
        title = "گروه توسعه و برنامه نویسی AS Team",
        body = "تمامی حقوق مربوط به این برنامه انحصاری میباشد"
    )
}

@Composable
internal fun ContactUsScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(94.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Email, null, tint = CactusPurple, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("گروه توسعه و برنامه نویسی AS Team", fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Text("ایمیل پشتیبانی", color = CactusMuted)
        Spacer(Modifier.height(5.dp))
        Text("as.team.support@gmail.com", color = CactusPurpleDark, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:as.team.support@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی CACTUS Collection")
                }
                runCatching { context.startActivity(intent) }
                    .onFailure { Toast.makeText(context, "برنامه ایمیل پیدا نشد.", Toast.LENGTH_SHORT).show() }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
        ) {
            Icon(Icons.Rounded.Email, null)
            Spacer(Modifier.width(8.dp))
            Text("ارسال ایمیل")
        }
    }
}

@Composable
internal fun AboutAppScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.cactus_logo),
                contentDescription = "CACTUS Collection",
                modifier = Modifier.size(150.dp)
            )
        }
        item {
            Text("درباره نرم‌افزار", fontSize = 23.sp, fontWeight = FontWeight.Black, color = CactusText)
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, null, tint = CactusPurple)
                        Spacer(Modifier.width(9.dp))
                        Text("CACTUS Collection چه کاری انجام می‌دهد؟", fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "CACTUS Collection برای سفارش چاپ DTF روی انواع تیشرت، هودی، شلوار و پوشاک طراحی شده است. کاربر می‌تواند طرح اختصاصی یا طرح آماده انتخاب کند، سفارش تکی یا عمده ثبت کند، وضعیت سفارش را پیگیری کند و از امکانات باشگاه مشتریان استفاده کند.",
                        color = CactusMuted,
                        fontSize = 14.sp,
                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfileScreen(profile: UserProfile, onEdit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        Box(
            Modifier.size(104.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AccountCircle, null, tint = CactusPurple, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(
            profile.fullName.ifBlank { "مهمان CACTUS" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        if (profile.phone.isNotBlank()) {
            Text(profile.phone, color = CactusMuted)
        }
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileLine("نام", profile.fullName.ifBlank { "ثبت نشده" })
                ProfileLine("شهر", profile.city.ifBlank { "ثبت نشده" })
                ProfileLine("ایمیل", profile.email.ifBlank { "ثبت نشده" })
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
        ) {
            Icon(Icons.Rounded.Settings, null)
            Spacer(Modifier.width(8.dp))
            Text(if (profile.firstName.isBlank()) "ثبت‌نام / تکمیل حساب" else "ویرایش اطلاعات", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = CactusMuted, fontSize = 12.sp, modifier = Modifier.width(70.dp))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CenterInfoPage(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(CactusLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = CactusPurple, modifier = Modifier.size(50.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(title, textAlign = TextAlign.Center, fontSize = 21.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(11.dp))
        Text(body, textAlign = TextAlign.Center, color = CactusMuted, lineHeight = 22.sp)
    }
}
