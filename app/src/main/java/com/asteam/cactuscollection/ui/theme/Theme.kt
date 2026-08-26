// [AS-TEAM-DOCUMENTED]
// فایل Theme.kt: این فایل بخشی از سورس CACTUS Collection است و کامنت‌های زیر برای توضیح منطق، UI و مسئولیت قسمت‌های مهم اضافه شده‌اند.
package com.asteam.cactuscollection.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// متغیر CactusColorScheme یک مقدار ثابت/مرجع موردنیاز این بخش را نگهداری می‌کند.
private val CactusColorScheme = lightColorScheme(
    primary = CactusPurple,
    onPrimary = Color.White,
    primaryContainer = CactusLavender,
    onPrimaryContainer = CactusPurpleDark,
    secondary = Color(0xFFE7779E),
    secondaryContainer = CactusPink,
    background = CactusBackground,
    surface = CactusSurface,
    onBackground = CactusText,
    onSurface = CactusText,
    outline = Color(0xFFD7CEDB)
)

// این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند.
@Composable
fun CactusCollectionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CactusColorScheme,
        content = content
    )
}
