// [AS-TEAM-DOCUMENTED]
// فایل MainActivity.kt: این فایل بخشی از سورس CACTUS Collection است و کامنت‌های زیر برای توضیح منطق، UI و مسئولیت قسمت‌های مهم اضافه شده‌اند.
package com.asteam.cactuscollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.asteam.cactuscollection.ui.theme.CactusCollectionTheme

// ساختار MainActivity داده‌ها یا مسئولیت مرتبط با این بخش از برنامه را مدل می‌کند.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            CactusCollectionTheme {
                CactusCollectionApp()
            }
        }
    }
}
