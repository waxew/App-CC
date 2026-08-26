package com.asteam.cactuscollection

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asteam.cactuscollection.ui.theme.CactusBackground
import com.asteam.cactuscollection.ui.theme.CactusBlue
import com.asteam.cactuscollection.ui.theme.CactusLavender
import com.asteam.cactuscollection.ui.theme.CactusMint
import com.asteam.cactuscollection.ui.theme.CactusMuted
import com.asteam.cactuscollection.ui.theme.CactusPink
import com.asteam.cactuscollection.ui.theme.CactusPurple
import com.asteam.cactuscollection.ui.theme.CactusPurpleDark
import com.asteam.cactuscollection.ui.theme.CactusText
import com.asteam.cactuscollection.ui.theme.CactusYellow
import kotlinx.coroutines.delay

@Composable
internal fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF9F6), Color(0xFFF8EFFF), Color(0xFFFFFBF8))
                )
            )
            .statusBarsPadding()
    ) {
        DecorativeDot(30, CactusPink.copy(alpha = .6f), Modifier.align(Alignment.TopStart).padding(42.dp))
        DecorativeDot(18, CactusYellow, Modifier.align(Alignment.TopEnd).padding(top = 90.dp, end = 38.dp))
        DecorativeDot(22, CactusBlue, Modifier.align(Alignment.BottomStart).padding(bottom = 180.dp, start = 30.dp))

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.cactus_logo),
                contentDescription = "CACTUS Collection",
                modifier = Modifier.size(210.dp).shadow(10.dp, CircleShape).clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(24.dp))
            Text("استایل خودت رو خلق کن!", fontSize = 25.sp, fontWeight = FontWeight.Black, color = CactusText)
            Spacer(Modifier.height(7.dp))
            Text("چاپ اختصاصی، کیفیت بی‌نظیر", color = CactusMuted, fontSize = 15.sp)
            Spacer(Modifier.height(34.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SplashMiniCard(R.drawable.ic_hoodie, "هودی", CactusLavender)
                SplashMiniCard(R.drawable.ic_tshirt, "تیشرت", CactusPink)
                SplashMiniCard(R.drawable.ic_printer, "چاپ DTF", CactusMint)
            }
            Spacer(Modifier.height(30.dp))
            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = CactusPurple, strokeWidth = 3.dp)
        }
    }
}

@Composable
private fun DecorativeDot(size: Int, color: Color, modifier: Modifier) {
    Box(modifier.size(size.dp).clip(CircleShape).background(color))
}

@Composable
private fun SplashMiniCard(@DrawableRes icon: Int, label: String, background: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), null, modifier = Modifier.size(38.dp), tint = Color.Unspecified)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun SignUpScreen(
    initialPhone: String,
    onContinue: (String) -> Unit,
    onSkip: () -> Unit
) {
    var phone by remember { mutableStateOf(initialPhone) }
    var showSmsInfo by remember { mutableStateOf(false) }
    val valid = phone.filter(Char::isDigit).length >= 10

    if (showSmsInfo) {
        AlertDialog(
            onDismissRequest = { showSmsInfo = false },
            icon = { Icon(Icons.Rounded.Info, null, tint = CactusPurple) },
            title = { Text("سامانه پیامکی هنوز متصل نیست") },
            text = { Text("فعلاً کد تأیید ارسال نمی‌شود. برای نسخه آزمایشی می‌توانید اطلاعات حساب را تکمیل کنید یا ثبت‌نام را به بعد موکول کنید.") },
            confirmButton = {
                TextButton(onClick = {
                    showSmsInfo = false
                    onContinue(phone)
                }) { Text("ادامه") }
            },
            dismissButton = { TextButton(onClick = { showSmsInfo = false }) { Text("انصراف") } }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(CactusBackground).statusBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(38.dp))
        Card(
            modifier = Modifier.size(118.dp),
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(containerColor = CactusLavender),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌵", fontSize = 48.sp)
                    Text("CACTUS", fontWeight = FontWeight.Black, color = CactusPurpleDark, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        Text("ثبت‌نام با شماره موبایل", fontSize = 25.sp, fontWeight = FontWeight.Black, color = CactusText)
        Spacer(Modifier.height(8.dp))
        Text("برای شروع، شماره موبایل خود را وارد کنید", color = CactusMuted, fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter(Char::isDigit).take(11) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("شماره موبایل") },
            placeholder = { Text("09121234567") },
            leadingIcon = { Text("+98", color = CactusPurple, fontWeight = FontWeight.Bold) },
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showSmsInfo = true },
            enabled = valid,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("ارسال کد تأیید", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text("یا", color = CactusMuted)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(18.dp)) {
            Text("بعداً ثبت نام می‌کنم", color = CactusPurpleDark, fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onSkip) { Text("رد کردن", color = CactusPurple) }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Lock, null, modifier = Modifier.size(16.dp), tint = CactusMuted)
            Text("  اطلاعات شما کاملاً محفوظ است", color = CactusMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
internal fun ProfileFormScreen(
    phone: String,
    initial: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(initial.firstName) }
    var lastName by remember { mutableStateOf(initial.lastName) }
    var city by remember { mutableStateOf(initial.city) }
    var email by remember { mutableStateOf(initial.email) }

    Column(Modifier.fillMaxSize().background(CactusBackground).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "بازگشت") }
            Text("تکمیل اطلاعات کاربر", fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.cactus_logo), null, modifier = Modifier.size(92.dp))
            Spacer(Modifier.height(14.dp))
            Text("خوش اومدی به خانواده CACTUS", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("اطلاعات پایه را وارد کن تا سفارش‌ها و باشگاه مشتریان شخصی‌سازی شوند.", color = CactusMuted, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))
            ProfileField(firstName, { firstName = it }, "نام")
            ProfileField(lastName, { lastName = it }, "نام خانوادگی")
            ProfileField(city, { city = it }, "شهر")
            ProfileField(email, { email = it }, "ایمیل (اختیاری)", KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { onSave(UserProfile(phone, firstName.trim(), lastName.trim(), city.trim(), email.trim())) },
                enabled = firstName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CactusPurple),
                shape = RoundedCornerShape(18.dp)
            ) { Text("ذخیره و ورود به برنامه", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
