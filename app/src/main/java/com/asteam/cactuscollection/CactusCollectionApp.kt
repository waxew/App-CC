package com.asteam.cactuscollection

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

internal enum class AppScreen {
    SPLASH,
    SIGN_UP,
    PROFILE_FORM,
    HOME,
    CLUB,
    ORDERS,
    SETTINGS,
    ABOUT_US,
    CONTACT_US,
    ABOUT_APP,
    PROFILE,
    CATALOG
}

internal data class UserProfile(
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val email: String = ""
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

internal class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("cactus_user", Context.MODE_PRIVATE)

    fun isOnboarded(): Boolean = prefs.getBoolean("registered", false) || prefs.getBoolean("skipped", false)
    fun isRegistered(): Boolean = prefs.getBoolean("registered", false)

    fun skipRegistration() {
        prefs.edit().putBoolean("skipped", true).apply()
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString("phone", profile.phone)
            .putString("first_name", profile.firstName)
            .putString("last_name", profile.lastName)
            .putString("city", profile.city)
            .putString("email", profile.email)
            .putBoolean("registered", true)
            .putBoolean("skipped", false)
            .apply()
    }

    fun loadProfile(): UserProfile = UserProfile(
        phone = prefs.getString("phone", "").orEmpty(),
        firstName = prefs.getString("first_name", "").orEmpty(),
        lastName = prefs.getString("last_name", "").orEmpty(),
        city = prefs.getString("city", "").orEmpty(),
        email = prefs.getString("email", "").orEmpty()
    )

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun notificationsEnabled(): Boolean = prefs.getBoolean("notifications", true)
}

@Composable
fun CactusCollectionApp() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val preferences = remember { UserPreferences(context) }
        var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
        var profile by remember { mutableStateOf(preferences.loadProfile()) }
        var pendingPhone by remember { mutableStateOf(profile.phone) }
        var selectedCatalog by remember { mutableStateOf("تیشرت") }

        when (currentScreen) {
            AppScreen.SPLASH -> SplashScreen(
                onFinished = {
                    currentScreen = if (preferences.isOnboarded()) AppScreen.HOME else AppScreen.SIGN_UP
                }
            )

            AppScreen.SIGN_UP -> SignUpScreen(
                initialPhone = pendingPhone,
                onContinue = { phone ->
                    pendingPhone = phone
                    currentScreen = AppScreen.PROFILE_FORM
                },
                onSkip = {
                    preferences.skipRegistration()
                    currentScreen = AppScreen.HOME
                }
            )

            AppScreen.PROFILE_FORM -> ProfileFormScreen(
                phone = pendingPhone,
                initial = profile,
                onBack = { currentScreen = AppScreen.SIGN_UP },
                onSave = { saved ->
                    profile = saved
                    preferences.saveProfile(saved)
                    currentScreen = AppScreen.HOME
                }
            )

            else -> MainShell(
                currentScreen = currentScreen,
                profile = profile,
                preferences = preferences,
                selectedCatalog = selectedCatalog,
                onCatalogSelected = { category ->
                    selectedCatalog = category
                    currentScreen = AppScreen.CATALOG
                },
                onNavigate = { currentScreen = it },
                onEditProfile = {
                    pendingPhone = profile.phone
                    currentScreen = if (preferences.isRegistered()) AppScreen.PROFILE_FORM else AppScreen.SIGN_UP
                }
            )
        }
    }
}
