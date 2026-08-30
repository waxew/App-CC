// [AS-TEAM-DOCUMENTED]
// فایل build.gradle.kts: تنظیمات ساخت پروژه؛ کامنت‌ها نقش بلوک‌های اصلی Gradle را توضیح می‌دهند.
// پلاگین‌های موردنیاز برای ساخت اپ اندروید و کامپایل Kotlin در این بلوک فعال می‌شوند.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// تنظیمات اصلی Android مانند SDK، نسخه برنامه، امضا و نوع Build در این بلوک قرار دارد.
android {
    namespace = "com.asteam.cactuscollection"
    compileSdk = 35

    // تنظیمات عمومی نسخه نصب‌شونده روی دستگاه‌ها در defaultConfig تعریف می‌شود.
    defaultConfig {
        applicationId = "com.asteam.cactuscollection"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "2.0.0"

        buildConfigField("String", "UPDATE_MANIFEST_URL", "\"https://raw.githubusercontent.com/waxew/App-CC/main/latest.json\"")
    }

    // signingConfigs هویت امضای نسخه Release را مشخص می‌کند تا آپدیت‌های آینده روی نسخه قبلی نصب شوند.
    signingConfigs {
        create("release") {
            storeFile = file("cactus-release.jks")
            storePassword = "CactusCC2026!"
            keyAlias = "cactus"
            keyPassword = "CactusCC2026!"
        }
    }

    // buildTypes تفاوت ساخت نسخه Release و سایر خروجی‌ها را کنترل می‌کند.
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

// کتابخانه‌های مورد استفاده برنامه در dependencies اعلام می‌شوند.
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
