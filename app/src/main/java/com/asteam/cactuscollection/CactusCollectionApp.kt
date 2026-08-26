package com.asteam.cactuscollection

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.asteam.cactuscollection.ui.theme.CactusBackground
import com.asteam.cactuscollection.ui.theme.CactusBlue
import com.asteam.cactuscollection.ui.theme.CactusLavender
import com.asteam.cactuscollection.ui.theme.CactusMint
import com.asteam.cactuscollection.ui.theme.CactusMuted
import com.asteam.cactuscollection.ui.theme.CactusPeach
import com.asteam.cactuscollection.ui.theme.CactusPink
import com.asteam.cactuscollection.ui.theme.CactusPurple
import com.asteam.cactuscollection.ui.theme.CactusPurpleDark
import com.asteam.cactuscollection.ui.theme.CactusText
import com.asteam.cactuscollection.ui.theme.CactusYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class AppScreen {
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

private data class UserProfile(
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val email: String = ""
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

private class UserPreferences(context: Context) {
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
                onSave = {
                    profile = it
                    preferences.saveProfile(it)
                    currentScreen = AppScreen.HOME
                }
            )

            else -> MainShell(
                currentScreen = currentScreen,
                profile = profile,
                preferences = preferences,
                selectedCatalog = selectedCatalog,
                onCatalogSelected = {
                    selectedCatalog = it
                    currentScreen = AppScreen.CATALOG
                },
                onNavigate = { currentScreen = it },
                onEditProfile = {
                    pendingPhone = profile.phone
                    currentScreen = if (preferences.isRegistered()) AppScreen.PROFILE_FORM else AppScreen.SIGN_UP
                },
                onProfileSaved = {
                    profile = it
                    preferences.saveProfile(it)
                }
            )
        }
    }
}

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF8F5), Color(0xFFF9F0FF), Color(0xFFFFFBF6))
                )
            )
            .statusBarsPadding()
    ) {
        DecorativeBubble(34.dp, CactusPink.copy(alpha = 0.6f), Modifier.padding(top = 70.dp, start = 26.dp).align(Alignment.TopStart))
        DecorativeBubble(22.dp, CactusYellow, Modifier.padding(top = 124.dp, end = 44.dp).align(Alignment.TopEnd))
        DecorativeBubble(18.dp, CactusBlue, Modifier.padding(bottom = 210.dp, start = 40.dp).align(Alignment.BottomStart))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.cactus_logo),
                contentDescription = "CACTUS Collection",
                modifier = Modifier
                    .size(210.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(26.dp))
            Text("استایل خودت رو خلق کن!", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = CactusText)
            Spacer(Modifier.height(8.dp))
            Text("چاپ اختصاصی، کیفیت بی‌نظیر", fontSize = 15.sp, color = CactusMuted)
            Spacer(Modifier.height(42.dp))
            FashionPrintStrip()
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp, color = CactusPurple)
        }
    }
}

@Composable
private fun FashionPrintStrip() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        MiniGraphic(iconRes = R.drawable.ic_hoodie, label = "هودی", background = CactusLavender)
        MiniGraphic(iconRes = R.drawable.ic_tshirt, label = "تیشرت", background = CactusPink)
        MiniGraphic(iconRes = R.drawable.ic_printer, label = "چاپ DTF", background = CactusMint)
    }
}

@Composable
private fun MiniGraphic(@DrawableRes iconRes: Int, label: String, background: Color) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(iconRes), contentDescription = label, modifier = Modifier.size(42.dp), tint = Color.Unspecified)
            Spacer(Modifier.height(5.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SignUpScreen(initialPhone: String, onContinue: (String) -> Unit, onSkip: () -> Unit) {
    var phone by remember { mutableStateOf(initialPhone) }
    var showSmsDialog by remember { mutableStateOf(false) }
    val validPhone = phone.filter(Char::isDigit).length >= 10

    if (showSmsDialog) {
        AlertDialog(
            onDismissRequest = { showSmsDialog = false },
            icon = { Icon(Icons.Rounded.Info, contentDescription = null, tint = CactusPurple) },
            title = { Text("سامانه پیامکی هنوز متصل نیست") },
            text = {
                Text("در نسخه فعلی پیامک تأیید ارسال نمی‌شود. برای تکمیل نسخه آزمایشی برنامه می‌توانید اطلاعات پروفایل را وارد کنید.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showSmsDialog = false
                    onContinue(phone)
                }) { Text("ادامه و تکمیل پروفایل") }
            },
            dismissButton = { TextButton(onClick = { showSmsDialog = false }) { Text("انصراف") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CactusBackground)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = {}) { Icon(Icons.Rounded.ArrowBack, contentDescription = "بازگشت", tint = Color.Transparent) }
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.size(112.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = CactusLavender),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌵", fontSize = 54.sp)
                    Text("سلام!", fontWeight = FontWeight.Bold, color = CactusPurpleDark)
                }
            }
        }
        Spacer(Modifier.height(30.dp))
        Text("ثبت‌نام با شماره موبایل", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = CactusText)
        Spacer(Modifier.height(8.dp))
        Text("برای شروع، شماره موبایل خود را وارد کنید", fontSize = 14.sp, color = CactusMuted)
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { value -> phone = value.filter { it.isDigit() }.take(11) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("شماره موبایل") },
            placeholder = { Text("مثلاً 09121234567") },
            leadingIcon = { Text("+98", fontWeight = FontWeight.Bold, color = CactusPurple) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showSmsDialog = true },
            enabled = validPhone,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CactusPurple)
        ) {
            Text("ارسال کد تأیید", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Text("یا", color = CactusMuted)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("بعداً ثبت نام می‌کنم", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onSkip) { Text("رد کردن") }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Verified, contentDescription = null, modifier = Modifier.size(18.dp), tint = CactusMuted)
            Spacer(Modifier.width(6.dp))
            Text("اطلاعات شما فقط روی دستگاه ذخیره می‌شود", fontSize = 12.sp, color = CactusMuted)
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ProfileFormScreen(
    phone: String,
    initial: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(initial.firstName) }
    var lastName by remember { mutableStateOf(initial.lastName) }
    var city by remember { mutableStateOf(initial.city) }
    var email by remember { mutableStateOf(initial.email) }

    Scaffold(
        containerColor = CactusBackground,
        topBar = {
            SimpleTopBar(title = "تکمیل اطلاعات", onBack = onBack)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 18.dp)
        ) {
            item {
                InfoCard(
                    title = "پروفایل شما",
                    body = "برای ثبت سفارش، پیگیری چاپ و استفاده از باشگاه مشتریان این اطلاعات استفاده می‌شود."
                )
            }
            item { ReadOnlyField(label = "شماره موبایل", value = phone) }
            item {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it.take(30) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("نام") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("نام خانوادگی") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("شهر") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.take(80) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ایمیل (اختیاری)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item {
                Button(
                    onClick = {
                        onSave(UserProfile(phone, firstName.trim(), lastName.trim(), city.trim(), email.trim()))
                    },
                    enabled = firstName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) { Text("ذخیره و ورود به برنامه", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "بازگشت")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CactusBackground)
    )
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, color = CactusMuted)
        Spacer(Modifier.height(5.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CactusLavender.copy(alpha = 0.55f)
        ) {
            Text(if (value.isBlank()) "ثبت نشده" else value, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun MainShell(
    currentScreen: AppScreen,
    profile: UserProfile,
    preferences: UserPreferences,
    selectedCatalog: String,
    onCatalogSelected: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onEditProfile: () -> Unit,
    onProfileSaved: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    fun navigate(screen: AppScreen) {
        scope.launch { drawerState.close() }
        onNavigate(screen)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.84f),
                drawerContainerColor = Color.White
            ) {
                DrawerContent(
                    currentScreen = currentScreen,
                    profile = profile,
                    onClose = { scope.launch { drawerState.close() } },
                    onNavigate = ::navigate,
                    onShare = {
                        shareApp(context)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = CactusBackground,
            snackbarHost = { SnackbarHost(snackbar) },
            topBar = {
                if (currentScreen in listOf(AppScreen.HOME, AppScreen.CLUB, AppScreen.ORDERS, AppScreen.PROFILE, AppScreen.CATALOG)) {
                    AppTopBar(
                        title = when (currentScreen) {
                            AppScreen.CLUB -> "باشگاه مشتریان"
                            AppScreen.ORDERS -> "سفارش‌های من"
                            AppScreen.PROFILE -> "حساب کاربری"
                            AppScreen.CATALOG -> selectedCatalog
                            else -> "CACTUS Collection"
                        },
                        onMenu = { scope.launch { drawerState.open() } }
                    )
                }
            },
            bottomBar = {
                if (currentScreen in listOf(AppScreen.HOME, AppScreen.CLUB, AppScreen.ORDERS, AppScreen.PROFILE, AppScreen.CATALOG)) {
                    BottomNavigation(
                        currentScreen = currentScreen,
                        onHome = { onNavigate(AppScreen.HOME) },
                        onClub = { onNavigate(AppScreen.CLUB) },
                        onOrders = { onNavigate(AppScreen.ORDERS) },
                        onProfile = { onNavigate(AppScreen.PROFILE) }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        profile = profile,
                        onCategory = onCatalogSelected,
                        onClub = { onNavigate(AppScreen.CLUB) },
                        onMessage = { scope.launch { snackbar.showSnackbar(it) } }
                    )
                    AppScreen.CLUB -> CustomerClubScreen(
                        profile = profile,
                        onMessage = { scope.launch { snackbar.showSnackbar(it) } }
                    )
                    AppScreen.ORDERS -> OrdersScreen(onExplore = { onNavigate(AppScreen.HOME) })
                    AppScreen.SETTINGS -> SettingsScreen(
                        preferences = preferences,
                        onBack = { onNavigate(AppScreen.HOME) },
                        onCheckUpdate = {
                            scope.launch {
                                snackbar.showSnackbar(
                                    if (BuildConfig.UPDATE_MANIFEST_URL.isBlank())
                                        "ساختار به‌روزرسانی آماده است؛ آدرس سرور نسخه هنوز تنظیم نشده."
                                    else "در حال بررسی نسخه جدید…"
                                )
                            }
                        }
                    )
                    AppScreen.ABOUT_US -> AboutUsScreen(onBack = { onNavigate(AppScreen.HOME) })
                    AppScreen.CONTACT_US -> ContactUsScreen(
                        onBack = { onNavigate(AppScreen.HOME) },
                        onEmail = { openSupportEmail(context) }
                    )
                    AppScreen.ABOUT_APP -> AboutAppScreen(onBack = { onNavigate(AppScreen.HOME) })
                    AppScreen.PROFILE -> ProfileScreen(profile = profile, onEdit = onEditProfile)
                    AppScreen.CATALOG -> CatalogScreen(category = selectedCatalog, onMessage = { scope.launch { snackbar.showSnackbar(it) } })
                    else -> HomeScreen(profile, onCatalogSelected, { onNavigate(AppScreen.CLUB) }) { scope.launch { snackbar.showSnackbar(it) } }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(title: String, onMenu: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.cactus_logo),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(9.dp))
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        },
        actions = {
            IconButton(onClick = onMenu) {
                Icon(Icons.Rounded.Menu, contentDescription = "منوی همبرگری", tint = CactusText)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun DrawerContent(
    currentScreen: AppScreen,
    profile: UserProfile,
    onClose: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onShare: () -> Unit
) {
    val items = listOf(
        DrawerEntry("خانه", Icons.Rounded.Home, AppScreen.HOME),
        DrawerEntry("سفارش‌های من", Icons.Rounded.ReceiptLong, AppScreen.ORDERS),
        DrawerEntry("باشگاه مشتریان", Icons.Rounded.Workspaces, AppScreen.CLUB),
        DrawerEntry("تنظیمات", Icons.Rounded.Settings, AppScreen.SETTINGS),
        DrawerEntry("درباره ما", Icons.Rounded.Info, AppScreen.ABOUT_US),
        DrawerEntry("تماس با ما", Icons.Rounded.Call, AppScreen.CONTACT_US),
        DrawerEntry("درباره نرم‌افزار", Icons.Rounded.Language, AppScreen.ABOUT_APP)
    )

    Column(modifier = Modifier.fillMaxHeight().statusBarsPadding().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, contentDescription = "بستن") }
            Spacer(Modifier.weight(1f))
            Image(painterResource(R.drawable.cactus_logo), null, modifier = Modifier.size(64.dp).clip(CircleShape))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            if (profile.fullName.isBlank()) "استایل خودت رو خلق کن!" else "سلام ${profile.firstName}!",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text("چاپ DTF، پوشاک اختصاصی و خرید تکی/عمده", color = CactusMuted, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        Divider(color = Color(0xFFEFEAF1))
        Spacer(Modifier.height(10.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title) },
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentScreen == item.screen,
                onClick = { onNavigate(item.screen) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = CactusLavender,
                    selectedIconColor = CactusPurple,
                    selectedTextColor = CactusPurpleDark
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        NavigationDrawerItem(
            label = { Text("معرفی به دوستان") },
            icon = { Icon(Icons.Rounded.Share, contentDescription = null) },
            selected = false,
            onClick = onShare,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        Spacer(Modifier.weight(1f))
        Surface(shape = RoundedCornerShape(18.dp), color = CactusLavender.copy(alpha = 0.7f)) {
            Column(Modifier.padding(14.dp)) {
                Text("درباره نرم‌افزار", fontWeight = FontWeight.Bold, color = CactusPurpleDark)
                Spacer(Modifier.height(5.dp))
                Text(
                    "سفارش چاپ DTF روی لباس، شخصی‌سازی طرح، طرح‌های آماده و خرید تکی یا عمده؛ همه در یک اپ.",
                    fontSize = 12.sp,
                    color = CactusMuted,
                    lineHeight = 19.sp
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("نسخه 1.0.0", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 11.sp, color = CactusMuted)
        Spacer(Modifier.navigationBarsPadding())
    }
}

private data class DrawerEntry(val title: String, val icon: ImageVector, val screen: AppScreen)

@Composable
private fun HomeScreen(
    profile: UserProfile,
    onCategory: (String) -> Unit,
    onClub: () -> Unit,
    onMessage: (String) -> Unit
) {
    val categories = listOf(
        CategoryItem("تیشرت", R.drawable.ic_tshirt, CactusPink),
        CategoryItem("هودی", R.drawable.ic_hoodie, CactusLavender),
        CategoryItem("شلوار", R.drawable.ic_pants, CactusYellow),
        CategoryItem("چاپ اختصاصی", R.drawable.ic_printer, CactusMint),
        CategoryItem("فروش عمده", R.drawable.ic_boxes, CactusPeach),
        CategoryItem("طرح‌های آماده", R.drawable.ic_palette, CactusBlue)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (profile.fullName.isNotBlank()) {
                Text("سلام ${profile.firstName} 👋", fontSize = 15.sp, color = CactusMuted)
            }
        }
        item { HeroBanner(onClick = { onCategory("چاپ اختصاصی") }) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("دسته‌بندی‌ها", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.weight(1f))
                Text("انتخاب سریع", color = CactusMuted, fontSize = 12.sp)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                categories.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { category ->
                            CategoryCard(category, Modifier.weight(1f), onClick = { onCategory(category.title) })
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        item { QuickPriceCard(onClick = { onMessage("محاسبه‌گر قیمت چاپ در مرحله بعد به قیمت‌گذاری واقعی متصل می‌شود.") }) }
        item { ClubPreview(onClick = onClub) }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

private data class CategoryItem(val title: String, @DrawableRes val iconRes: Int, val color: Color)

@Composable
private fun HeroBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(196.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(Color(0xFF8A66D0), Color(0xFF6E49B7), Color(0xFFB779D4)))
            )
        ) {
             آمادٌr { mutabت‌نام با شما�: ImageVector, val screen: Appog by.Oت‌نام �creen.CLUB -                TetiveBubble(34.dp, 8نام با شما�: ImageVector,2val screen: Appog by.Oت‌نStart))

   reen.CLUB 
            Text("difi           modifier = Modifier.paddinxSize().padding(paddin,
            verticccccalAlignment = Alignment.CenterVertically) {
         )
                  Column(horizoer = Modifier.paddin(1f))
                      Text("🌵", اختصاصی", R.drawc Color.White)
    Size = 25.sp, fontWeight = FontWeight.Bold)
old)
                Spacerrrrr(Modifier.height(8.dp))7                Text(
xt("🌵", لباس، شای آما�سر��یو!rawc Color.White)
    ا�: ImageVector9f)Size = 15.sp, colo            Spacerrrrr(Modifier.height(8.dp))2-                Teeeeeeeee(
                    onCliccccck = onClub) }
onC                    onEmai = ButtonDefaults.buttonColors(containerColor = CactusPxFFFFFBF6))7E9F)                 contennnnn= RoundedCornerShape(18.dp),
            colors      onEmai =adding = PaddingValues(horizontal = 16.dp, Cactusal = 14.dp),                    .shadoxt("ذخیع، ش٭‌ه�ح.drawight = FontWeight.Bold) }
            }
                }
                            title = RoundedCornerShape(28.dp),
            color   onEmai = Buttonfaults.cardColors(containerColor = CactusL با شما�: ImageVector,3                  }
                  Text("cons.       colors      onEmairResource(R.drawable.cactusdie, Cact         colors      onEmai =addinption = null,
                    modifiiiiier = Modifier.padding(16.dp),clip(C8.dp),0
            colors      onEmai Color.Unspecified)
                   .shadox           }
            }
        }
        itComposable
private fun MainShryCard(catego Spa:oryItem(val tfier = Mod:ier.paddiick = { o> Unit) {
    Card(
        modifier = Modifimr.height(8.dp))11clip(Circckriva(k = onClub) }
onC     shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = backgrcreen = Ca     elevation = CardDefaults.cardElevation(5.dp)
1   ) {
        Column(
            modifier = Modifier.paddinxSize().padding(paddin,
            contenalArrangement = Arrangement.spaced(ModiBetween   ) {
             آم�ainterResource(iconReson, contcontentDescription = label,modifier = Modifier.size(64.dp)5 tint = Color.Unspecified)
            Spacertem.title) },
 Weight = FontWeight.ExtraBold, color ze = 14.sp, color = CactusMuted)            }
    itComposable
private fun MainShriceCard(onClick = { o> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().heightircckriva(k = onClub) }
onC     shape = RoundedCornerShape(28.dp),4        colors = CardDefaults.cardColors(containerColor = backgrWhite)
    )    elevation = CardDefaults.cardElevation(5.dp)
3   ) {
        Columndifier.padding(16.dp),8verticalAlignment = Alignment.CenterVertically) {
        MiniGrrrrrdifier.fillMa4.dp)5 tintCircledCornerShape(18.dp),
    ground(CactusBackgr
     ntAlignment = Alignment.Center) {
                Columncons.Rounded.Share, copp.dpBag, tint = CactusPurple) },
                 Sp        Spacer(Modifier.weight(.heig14                Te(Modifier.paddin(1f))
                      Teمعرفۨه‌گ،ع", col�ت چاپ در rawight = FontWeight.Bold) old)
                Spacerنسخه ش٭س، شخصً۪ ث�دستفب اس�� در مارخاب سری elstSize = 12.sp, color = CactusMuted)
        }
      Sp        Spacercons.Rounded.Close, hevronLeft, tint = CactusPurple) },
         }
    itComposable
private fun MainSheview(onClick = onC> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().heightircckriva(k = onClub) }
onC     shape = RoundedCornerShape(28.dp),4        colors = CardDefaults.cardColors(containerColor = backgrWavender.copy(alpha = 0.7f)) {
2              Columndifier.padding(16.dp),8verticalAlignment = Alignment.CenterVertically) {
        MiniGrrrrr(Modifier.paddin(1f))
                      Teمعرگاه مشتریان", Icons.ze = 18.sp, fontWeight = FontWeight.ExtraBold)
    r = CactusPurpleDark)
                Spacer(Modifier.height(5.dp))
                Text(
     "نه �ی)")شگی چ؈شا‌د ب��ط��پ و ؄�دـ�ه�ح.d�ع،ک�پ elsست�مابت چ��ً۪ , fo�  ث�ش�بٴگاه.tSize = 12.sp, color = CactusMuted)
    ,eight = 19.sp
 8           }
        }
        difier.fillMa4.dp)6 tintCircleShape))
    round(CactusBhite)
    ) ntAlignment = Alignment.Center) {
                Columncons.Rounded.Share, tar, tint = CactusPxFFFFFBF6))B33C)fier = Modifier.size(64.dp)34                Te    }
        itComposable
private fun MainShrrClubScreen(
     e: UserProfile,
    osage: (String) -> Unit
) {
   var firstName bLikedmember { mutableStateOf(false) }
    val valictedoundLikedmember { mutableStateOf(false)}

@C  ModalNlumn(
        modifier = Modifier.fillMaxSize().background(CactusBxFFFFFBF6)8F5FB)     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column ClubPreview(
         p) -> Uni    item {
            Row(modifier = Modifier.fillMaxWidth(), verticntalArrangement = Arrangement.spacedBy(12.dp))0{
                      evieA = {
 "نه �ی)")� fontSiRounded.Share, tar, Yellow, Modifier.paddin(1f))
       sage("محانه �ی)")شی پی�: 1,250           Spacer    evieA = {
 "ما�ه��ons.Rounded.SettinFavtalctusBlue)

   fier.paddin(1f))
       sage("محا٨ س� ما�ه��o��روزص�سشدم ب؅اده استش�ست؛ آ�.           Spacer    evieA = {
 " و ؄�ا", fontSiRounded.SettinP, CactusBlue)
er,
     ier.paddin(1f))
       sage("محا و ؄�دـ�ه�ح.d�تفب �") },
�ی ٨رر؛ آ�.           Spacer    evieA = {
 "ا‌ا", fontSiRounded.Settinaces, AppScrBlue, Modifier.paddin(1f))
       sage("محا�سخه فعلۄ می‌گ،ع",�سششا‌ای آمابری"
   },
�۹ی متصنشی ب�  ث��ی‌شود."
               }
        }
    }
}

@em {
            Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("دستا‌ای آماصا fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.weight(1f))
                Text("e(shape = CactusLavender.copy(, = RoundedCornerShape(28.dp)50                   Text("🌵",ه آزمایشی بر�ifier = Modifier.fillMag(horizontal = 16.dp, 0ertical = 14.dp)5fontWeight 15.sp, 0olor = CactusMuted)Dark)
                }
            }
        }
        Spacer
            Row(moSocne) ost                    tiauthactus�تا�ار؀�ه�ح.dra               titi onNa"۲ تاع�وف ب�ra               titLocalC, اختد…"
 �نٱ�مبر�, R.d�ری�ا .�تٱ�ا �یبن؁�� و�ری�ا خلق ڮوستگان")  ث�ش�ب�                  fovisualT "پروٱ�, R.d�ری�ا  •� در �Galaxy                 fovisual {
    able.ic_hoodie, Cactu               folikedm=Name bLikedu               folikerdDewIteame bLiked) 43"سل�42            colors =mentessp
 8            onClick Like rstName =Likedm=N!ame =Likedm             onProfilC=mentenMessage("محا٨ س� ال نمی�ا�ٯ به ق")شڧصی",�‌� کاربری"
    ��گ،ع",�سشی ٨رر�شود."
               }
        }
        Naviga
            Row(moSocne) ost                    tiauthactus��پیا�ی�ار�پ.ra               titi onNa"…"
�ساra               titLocalC,‌ه؈تفب �")و�ماد…"
 �ی تکمیت", R.d�رش�"
 �ر�فقط�أ�و�نٱ��گخلق �اب سری�کنم", f�"
 ��                 fovisualT "پرویت", R.d�رش�"
  •�‌ه؈Sunset                 fovisual {
    able.ic_hoodie, Cactu               folikedm=NedoundLikedu               folikerdDewIteedoundLiked) 37"سل�36            colors =mentessp
 1            onClick Like rstNedoundLikedm=N!edoundLikedm             onProfilC=mentenMessage("محا٨ س� ال نمی�ا�ٯ به ق")شڧصی",�‌� کاربری"
    ��گ،ع",�سشی ٨رر�شود."
               }
        }
        Naviga
      Ch{
 et.snClick JoinnMessage("محا�ی�خ", R.�")  �،ک�پ�سخ و ؄�د�سفاایشی برنش}) }
        item { ClubPr(Modifier.height(6.dp))
     
    itComposable
private fun MainSheview(
         p) -> UProfile,
      Card(
        shape = RoundedCornerShape(22.dp),6        colors = CardDefaults.cardColors(containerColor = backgrWhite)
    )    elevation = CardDefaults.cardElevation(5.dp)
3   ) {
        Column(Modifier.padding(14.dp))8{
                  rticalAlignment = Alignment.CenterVertically) {
                Image(           modifiiiiiiiiier.fillMa4.dp)5clip(CircleShape)
     round(CactusBackgrer),
            elevat  colors =lignment = Alignment.Center) {
                 }
                  Text("cons.Rounded.SettinPersntentint = CactusPurple) },
 fier = Modifier.size(64.dp)3
            Circulllll        Spacer    (Modifier.weight(.heig1
            Circulllll(Modifier.paddin(1f))
                      Teeeeef (value.e.fullName.isBlank()) "است؅ ${profبرک��انیe "سلام ${profile.firstName}!",
     eight = FontWeight.ExtraBold, color ze = 14.sp, 7                   Text("سلام‌��ن؂�رفزا.drawight 12.sp, color = CactusMuted)
        }
      Spllll        Spacer    (horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌵",1,250 awight = FontWeight.Bold) }lontenze = 25.sp, color = CactusMuted)Dark)
                }
        rticalAlignment = Alignment.CenterVertically) {
                Image(((((((((cons.Rounded.Share, tar, tint =er = Modifier.size(18.dp),4tint = Color.UnspeFFBF6))B52E                           🌵", نه �ی)")�"Weight 15.sp, 0olor = CactusMuted)
        }
      Spllllllll        Spacer            }
        }
        (Modifier.height(10.dp))
                TeLradiee,
gr("�Ind) {to                   p,
gr("�nMess0.62fm             onProfer = Modifier.fillMaxWidth().height(196.dpclip(CircleShape)
                 colors = NatusPurple) },
 f               titront= backgrWavender.copy(       }
        }
        (Modifier.height(10.dp)7                Teسلا٪�ر؅‌��عات", f 750 نه �ی)")شگ�قی�"Size = 11.sp, color = CactusMuted)
        Spacer    itComposable
private fun MainShevieA = {
  String, @DrawaImageVector, val sc Color)

@Cofier = Mod:ier.paddiick = { o> Unit) {
    Card(
 horizoer = Modifimr.heightircckriva(k = onClub) }
onC  ontalAlignment = Alignment.CenterHorizontally
    )      Box(
     er.fillMa4.dp)5clip(CircleShape))
    round(Cactus = Ca  ntAlignment = Alignment.Center) {
                Cotem.iccntentint = CactusPurple) },
                   Spacer(Modifier.height(6.dp))
        Text(
      fontSize = 18.sp, 1ontWeight = FontWeight.Bold)
        }
Composable
private fun MainShSocne) ost          authacng,
    onCatati ong,
    onCatat   ng,
    onCatavisualT "پng,
    onCatableRes val icoisual {
  val cnCataliked:   oleancnCatalikes val cnCata =mentes val cnCatak Like> Unit,
    onMessagC=mente> Unit
) {
    val it        shape = RoundedCornerShape(22.dp),4        colors = CardDefaults.cardColors(containerColor = backgrWhite)
    )    elevation = CardDefaults.cardElevation(5.dp)
2   ) {
        Column(Modifier.padding(14.dp)) {
                TerticalAlignment = Alignment.CenterVertically) {
                Image(    er.size(38.dp).clip(CircleShape)
     round(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Column  Cotem.iRounded.SettinPersntentint =er = Modifier.size(18.dp)2 tint = Color.Uurple) },
         }
            Spacer  Spacer(Modifier.height9.dp))
                Text(t {
        Text(lllllllllllll     authacWeight = FontWeight.Bold, color ze = 18.sp, 3                   Text("سل�ti oWeight 15.sp, 0olor = CactusMuted)
        }
      Spllll        }
        }
        (Modifier.height(10.dp))-                Teسل�t�لr ze = 18.sp, 3   ,eight = 19.sp
20                 (Modifier.height(10.dp))
            Circul           modifiiiiier.fillMaxWidth().height(196.dp)50tintCircledCornerShape(18.dp)2-     round(
                Brush.rcul inearGradient(listOf(Color(0xFF8A66D0251D31or(0xFFB779D45E3E8Dor(0xFFB779D49C76BE                  },        )
                  Columncons.           Brush.rculrResource(iconReoisual {
          elevat  colors                    modifi Color.Unspecified)
                    modifier = Modifier.size(38.dp)12-    Appog by.Oت‌ن) {
  � �creen.CLUB,
 sp, co              )
            }
                            "سف�visualT "پ                modifier = Modifier.size(3ppog by.Oت‌نStart))

   reen.CLUB 
            color   onEmai = BukgrWhite)
                    fontSize = = FontWeight.Bold, color               fontSize = 12.sp, colo           )
            }
        }
        (Modifier.height(10.dp))-                TerticalAlignment = Alignment.CenterVertically) {
                Image(
ton(onClick = onClose)Likefier = Modifier.size(64.dp)34                  Column  Cotem.ilue.liked) Rounded.SettinFavtalct"سل�Rounded.SettinFavtalctBorpy(, tint = Cactuslue.liked) 0xFFEFEAF1)95D83)"سل�Muted)
        }
      Spllll        Spacer    abel, ikes.to,
    ()awight 12.sp, color = CactusMuted)
        }
      Spllll(Modifier.weight(.heig1
            Circullllltton(onClick = onSkip) C=mente,ntPadding = PaddingValues(horizontal = 16.dp,4                  Column  Coسلا$ =mentess�ا��rawight 12.sp, color = CactusMuted)
        }
      Spllll        Spacer    }
        itComposable
private fun MainShrh{
 et.snClick Join> Unit) {
    Card(
     = RoundedCornerShape(22.dp),4     s = CardDefaults.cardColors(containerColor = backgrWavender.copy(       Row(modifier.padding(16.dp),8verticalAlignment = Alignment.CenterVertically) {
        MiniGrrrrrdifier.fillMa4.dp)58tintCircledCornerShape(18.dp),
    ground(CactusBhite)
    ) ntAlignment = Alignment.Center) {
                Columncons.Rounded.Share,P, Cactustint = CactusPurple) },
 fier = Modifier.size(64.dp)3
            Circul        Spacer(Modifier.weight(.heig1
            Circul(Modifier.paddin(1f))
                      Teمعر و ؄�دی ٨ر�: ‌ه؈تفب �")و��, Icons.ight = FontWeight.ExtraBold)
    r = CactusPurpleDark)
                Spacerسلاـ�ه؈صاصی و خ�ت رو خلق �ی ط�ستفه �ی)")شگی چ�.tSize = 12.sp, color = CactusMuted)
            Circul        Spacertton(onClick = onSkip) Joinxt("ذخیع،ک��           }
    itComposable
private fun MainShScreen(onExplore = { o> Unit) {
    Card(
 horizo       }
er = Modifier.paddinxSize().padding(paddin,4        colorsntalAlignment = Alignment.CenterHorizontally
    )    verticalArrangement = Arrangement.spaced) {
             Box(
     er.fillMa4.dp)90lip(CircleShape)
     round(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Cocons.Rounded.Share,tLong, AppScrtint =er = Modifier.size(18.dp)46tint = Color.Uurple) },
         }
    Spacer(Modifier.height(6.dp)) 
            Te🌵",ٲ تنظ۱ش چا٨ت سفاه" else enze = 25.sp, 0ontWeight = FontWeight.ExtraBold)
              (Modifier.height(10.dp)7              معرگ�ه ق")ش�سفارش، پیگۅ�ه�ح�رسی نسخ، طرح در �صۨ‌بندی‌ه�ستف؄ نمی�ارخ�ط�ج�", I�ود.��ط�ی.tSiign = TextAlign.Center, fontSi= CactusMuted)
    ,eight = 19.sp
21       Spacer(Modifier.height(16.dp)2-              (onClick = onSkip) e = { o, = RoundedCornerShape(28.dp)) {
     eمعرف۴�ش��ی‌طر�و�����on 
    itComposExperimentalMaterial3Api::class)
@Composable
private fun SimpleTsScreen(
     ences: UserPreferences,
    sk: () -> Unit) {
  ,ckUpdate = {
 > Unit) {
    Card(
lictnotheica= {
  ember { mutableStateOf(false)ences: User.notheica= {
 d = tru(  
    itld(
            co = {
     eTopBar(title:�یمات", Icons.) {
         colors =rColor = CactusBackground,
            ng ->
            Bo(Modifier.paddinxSize().padding(padding).padding(horizo,8verticalAlignmement = Arrangement.spacedBy(16.dp)
    )              CoTsScreendifi           modifi { IconRounded.Share,Notheica= {
 a               titi"پروۧ�پ� �ا�", fontS               tisubti"پروۧعات �  ط�س�ض�ت؁ارش، پیگب �ش�"ف�", fonست�م�ه مشتریان"
                                    TeSwitch(cdateedm=Nnotheica= {
 ,ckUpdateerS= { email       elevat  colors otheica= {
  grcr           Brush.rculrnces: User.setNotheica= {
 d = tru(i                   }        Circul        Spacer(sScreendifi           modifi { IconRounded.Share, = {
 a               titi"پرو۳ی نسخه جدید…"
 tS               tisubti"پروه فعلۆی ��د."�ی�:", modi                               Tetton(onClick = onSkip) Cdate = {
 xt("ذخیسی نس�           }
        }
        AppS     "�روزرسانی آماب��و����� �ف ۧعات � Icons.�ا؆ R.��نامه", fonستفه ��ن", � 1.0.0", mod��س��فا�سخه ��ٯ�ی�شت�تش�ستت�ر � 1.0.0�ی آماد�ه � خلمبرگ�ط� ه فعلۆی ���ف�‌�ستفعات � Ico�م ب؅ابری"
  ���شظ ."
             }
    itComposable
private fun MainSh(sScreendifiImageVector, val sc String, @Drawasubti"پng, @Drawad)
iighg: sable
priva Unit) {
    Card(
     = RoundedCornerShape(22.dp),0     s = CardDefaults.cardColors(containerColor = backgrWhite)
    )      Row(modifier.paddinxWidth().heightg(16.dp), fontWealAlignment = Alignment.CenterVertically) {
        MiniGrrrrrdifier.fillMa4.dp)46tintCircledCornerShape(18.dp),5    ground(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Columntem.iccntentint = CactusPurple) },
         Circul        Spacer(Modifier.weight(.heig1
            Circul(Modifier.paddin(1f))
                      Teمع� fontWeight = FontWeight.Bold) },
                 Teمع�subti"پSize = 11.sp, color = CactusMuted)
        Spacerrcul        Spacerd)
iighg(        }
    itComposable
private fun MainShsScreen(onBack = { o> Unit) {
    Card(
TopBarAppSP             ti"پروۧره ما", Icons          = { onNa = { os          { IconRounded.Share,AppSc   colorsnea Paddin"�ی�؈�تتان"�onست٧مه", fonسهم؆سخASeمamns         bodyروی Icoگ�؀��؂؈���ۅ�ه�اٷ��گ،خ�ط٪٧مه", fon� �احی واق؅� �۫�ش��i     Composable
private fun DrawertUsScreen(
     : () -> Unit) {
  ,ckUconte> Unit) {
    Card(
Td(
      = {
     eTopBar(title:�ی�با ما", Icons.) {
      s =rColor = CactusBackground,
       ng ->
            Bo(Modifi       Spacerer.paddinxSize().padding(padding).padding(horizo,4        colorslorsntalAlignment = Alignment.CenterHorizontally
    )    verticccccalArrangement = Arrangement.spaced) {
           C     MiniGrrrrrdifier.fillMa4.dp)86lip(CircleShape)
     round(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Cooooocons.Rounded.Share,conteustint = CactusPurple) },
 fier = Modifier.size(64.dp)4
            Circul        Spacer(Modifier.weight(8.dp))2-                Teمعر ی�؈�تتان"�onست٧مه", fonسهم؆سخASeمamns.ight = FontWeight.ExtraBold)
    r ign = TextAlign.Center, fontSize = 11.sp, 9                 (Modifier.height(10.dp))
            Circul     "نت",�ود� �� �ی)� آم�or = CactusMuted, font        Circul     "as.t�am.sEmail(@gonte.comns.ight = FontWeight.ExtraB    r = CactusPurpleDark)
                Sp(Modifier.height(6.dp)) 
            TerrrrdonClick = onSkip) eonteus= RoundedCornerShape(28.dp)) {
     econs.Rounded.Share,conteustint);r(Modifier.weight(.heig
     ;l     "ن؄ نمی��ت",�و�           }
    itComposable
private fun MainShppScreen(onBack = { o> Unit) {
    Card(
Td(
      = {
     eTopBar(title:�ۧره نرم‌افزار", fontWe) {
      s =rColor = CactusBackground,
       ng ->
            Bolumn(
        modifiiiiier = Modifier.paddinxSize().padding(padding).paddi    verticcccctPadding = PaddingValues(horizo,
            verticalArrangement = Arrangement.spacedBy(16.dp)
    )    colorslorsntalAlignment = Alignment.CenterHorizontally
    )         C     MiniGrrrrr
            Row(mooooopainterResource(R.drawable.cactus_logo), null, modifier = Modifier.size(64.dp)130lip(CircleShape)
             Circul        Spacer
            Row(mooooo     " Collection"
     enze = 25.sp, 4ontWeight = FontWeight.Bold)
 lont        Circul        Spacer
            Row(moooooAppS                Brush.rcul" ک� و �تع fo�بر��س��افزار", fontW           Brush.rcul" Collection"
    �ی تکم۱ش چاپ DTF روی لبا", Ic�ت �  �ت", R.d�بر�, R.d�صیر", R.d و�اک اخت؀�ه�ح.d�ع�ست؛ آ�.�بری"
  � I�ود.�۹ی ����ـ�ه؈صاصی و خعمدـ�ه؈ده است؛اب سری elsۧ�م۱ش چاپ یا عمده؛ هٴ�سفا elsۧ�م��ض�ت؁ارش، پی�ارف �ی چ؅اب‌�ستفز�فهبر؆ R.���م�ه مشتریان"
    �؛ آ�ش، هٴب‌�                           Circul        Spacer
            Row(moooooAppS                Brush.rcul"ه فعلی پی�tW           Brush.rcul" � 1.0.0", mod�—��ا��م��بری"
    ���و�� چ��ت سفای �", f�ایشی برنب��و���ف ب�هب، ش عم� مشت؆شی برٮصۨ��ه مشتریان"
    �و�ر همببر؅�رسیه", fon                           Circul        Sp    itComposable
private fun MainSheScreen(profile = proProfile,
    osagofil> Unit) {
    Card(
 horizoer.paddinxSize().padding(paddin,
     ontalAlignment = Alignment.CenterHorizontally
    )      Box(
 (Modifier.height(6.dp)) 
            Te    er.fillMa4.dp)96lip(CircleShape)
     round(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Cotem.iRounded.SettinPersntentint =er = Modifier.size(18.dp)54tint = Color.Uurple) },
         }
    Spacer(Modifier.height(6.dp)) 2              مع�lue.e.fullName.isBlank()) "استب کار fon Icoچ "سل�e.fullName.isBlaenze = 25.sp, 1ontWeight = FontWeight.Bold)
old)
              (Modifier.height(10.dp)22              e,
    (horiوا؅ ما", I�م� خو� ene.fullNaphone          e,
    (horiوا؇ontWee.fullNacity          e,
    (horiو؆ت",�و� Wee.fullNaeonte    Spacer(Modifier.height(10.dp))
            Te onClick = onSkip) ed  ,c= RoundedCornerShape(28.dp)) {
  fier = Modifier.fillMaxWidth(), text              Coمع�lue.e.fullName.isBlank()) "است�سفای �", f��ت�ی�",�ود� ��؈�� خو� "سلا�م؆�ی ب�  �عات � Icon        }
    itComposable
private fun MainShe,
    (hori�= { Tng, val icon:u�ng, @Dra        difier.paddinxWidth().heightg(16.dp)al = 14.dp)7tintCircledCornerShape(18.dp),6    ground(CactusBhite)
    )tg(16.dp),5   )            abel,  { TSi= CactusMuted)
    ,eer = Modifier.paddin(1f))
              مع�lue.on:u�nk()) "است�سفاه" else "سل�on:u�Weight = FontWeight.Bold)
        }
Composable
private fun MainShgScreen(category = selng, onMenu: () (String) -> Unit
) {
   var firslee.fducessp
when ory = sel            �ی", R.drit
)(Color(�ٱ�, R.d�ری�ا   اخترش چا٨�ns.�ٱ�, R.d�ی)")��ق؅� �م� Icoڄns.�ٱ�, R.d���و؄ ن�ی)��ـ�ه؈Galaxy           �ار", R.drit
)(Color(�یر", R.d گی  اختب‌� tint =�ای؄�د۱ی�ا   اختصاصی", R.draw�یر", R.d �ه�ح �ت�ـ�ه؈؅� �م� Icoڄn          �ش عمده", R.drit
)(Color(�پ�ا �� 20 ً ث���ns.�پ�ا �� 50 ً ث���ns.�؛ آ��پ� �؅ 100 ً ث���گ،ب� ؄��           �؀�های آماده", R.drit
)(Color(�بر؄بش��, I� �م� Icoڄns.�بر؄بش��,) -eetwearns.�بر؄بش��,ش چاس�           � اختصاصی", R.drit
)(Color(��در م؄با"�� ��se en��در ٱ �� ��بر؅�� en��در �ا۳ �ت��,/ٱ ����se           > HomeSc(Color(�یت", R.d�رش�"
   اختصاصی", R.draw�یت", R.d�ری�ا خ��ه؈ده اس�raw�یت", R.d���و؄ ن�ی)��Premium"    }
Co   }
lumn(
        modifier = Modifier.fillMaxSize().background(CactusBackground),
        contentPadding = PaddingValues(horizo
    )    colorsalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(moAppS                Brush.ti"پر�ry, Modif           Brush.bodyر�lue.ry = selec= �ش عمده", R.dr�تتً ث�دس�و؄ ��ا�ٯ ارخاب سری els�ه � چاپ��seت", f د�ه خ؋قیمؾ��s��ت‌گذاری واق؄ می‌شود.") }) }"سلا����و��se�ی من", � 1.0.0", moمرحله بعد به ق�اب سریر��گ�م۱��ی)��گۅ���ر�در ٱ��دپر", ��ـ�ه؈�گ،خ�ط٪٧ٮ�د۱�می‌شود.") }) }                     }
    SpacerforEa(e.fduces   ng.fduce                P.fduce     ti"پر�g.fduce,ory ->
   ��ry, Modifck = { onCategoe("محا«$g.fduce»ق�اب سریش})  �ی����ید�ئی)")�ٱ��رش، پی�سخه فعل به قی�",�ود�شود.") }) }
          }
    itComposable
private fun MainShe,
duce     ti"پng, onMenury = selng, onMenu: = { o> Unit) {
    Card(
onRes: Isp
when ory = sel            �ی", R.drit
)able.ic_hoodie, Cact         �ار", R.drit
)able.ic_pants, Cact         �ش عمده", R.drit
)able.ic_boxes, Cact         �؀�های آماده", R.drit
)able.ic_palette, Cact         � اختصاصی", R.drit
)able.ic_printer, Cact         > HomeScable.ic_hoodie, Cact
  }
    it        modifier = Modifier.fillMaxWidth().heightircckriva(k = onClub) }
onC     shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = backgrWhite)
    )    elevation = CardDefaults.cardElevation(5.dp)
2   ) {
        Columndifier.padding(16.dp),4fontWealAlignment = Alignment.CenterVertically) {
        MiniGrrrrrdifier.fillMa4.dp)8 tintCircledCornerShape(18.dp)2-     round(
     Wavender.copy(alpha = 0.7f)) {6    ntAlignment = Alignment.Center) {
                Cooooocons.rResource(iconRes{
    modifier = Modifier.size(64.dp).0tint = Color.Unspecified)
            Spacer        Spacer(Modifier.weight(.heig14                Te(Modifier.paddin(1f))
                      Teمع� },
 Weight = FontWeight.ExtraBold, colo            Spacer(Modifier.height(5.dp))
                Text(
     "ق�ر۱ر،‌سازی طر�or = CactusMuted,) },
 fize = 12.sp)
            }
        }
    oooocons.Rounded.Close, hevronLeft, tint = CactusPurple
        Spacer    itComposable
private fun MainShStart)tionDrawer    motScreen == itcreen)

@CoonMessagreen> Unit,
    onMessagC) -> Unit,
    onMessagScreen> Unit,
    onMessagP = proPrUnit
) {
    val itNionBarsPaddiinerColor = backgrWhite)
     = onalEon = CardDe4               NionBarsPaddi            label ed = currentScreen == item.seen.ABOUT_HOME ||ntScreen == item.seen.ABOUT_CATALOG        onClick = onShare,reen        icon = { Icon(Icons.Rounded.Share,reen  },
            select= { Text("معر سآ�se            select = CardDenav(contai    ) {
            NionBarsPaddi            label ed = curren
            onClick = onShar{ /* Searchn: AppS is ilignmionally dces:rurrto the nLocacScreenrforrarsPa. */          icon = { Icon(Icons.Rounded.Share, earch  },
            select= { Text("معر � آ�جو            select = CardDenav(contai    ) {
            NionBarsPaddi            label ed = currentScreen == item.seen.ABOUT_ORDERS        onClick = onShare,Screen        icon = { Icon(Icons.Rounded.Share, copp.dpfaut  },
            select= { Text("معر �ش، پۧ", font           select = CardDenav(contai    ) {
            NionBarsPaddi            label ed = currentScreen == item.seen.ABOUT_CLUB        onClick = onShare,C) -        icon = { Icon(Icons.Rounded.Share, tar, tint           select= { Text("معر ���ه مش�t           select = CardDenav(contai    ) {
            NionBarsPaddi            label ed = currentScreen == item.seen.ABOUT_PROFILE        onClick = onShare,e,
    onCate  icon = { Icon(Icons.Rounded.Share,Persntentint           select= { Text("معر� ��؈�� خو�            select = CardDenav(contai    ) {
        Composable
private fun MainShnav(contai  = NionBarsPaddi    ts.colors(
          edIconColor = CactusPurple,
          edTextColor = CactusPurpleDark
           ind) {to = backgrWavender.copy(      unseIconColor = CactusPurple
          unseIconColor = CactusPurple
    
mposable
private fun HeroBaTopBarAppSP    ti"پng, onMenu: () -> Unit) {
  ,cImageVector, val scnea Padng, onMenubodyng, @Dra        Td(
      = {
     eTopBar(title: },
 We) {
      s =rColor = CactusBackground,
       ng ->
            Bo(Modifi       Spacerer.paddinxSize().padding(padding).padding(horizo,4        colorslorsntalAlignment = Alignment.CenterHorizontally
    )    verticccccalArrangement = Arrangement.spaced) {
           C     MiniGrrrrrdifier.fillMa4.dp)8clip(CircleShape)
     round(CactusBackgrer),
     ntAlignment = Alignment.Center) {
                Columntem.iccntentint = CactusPurple) },
 fier = Modifier.size(64.dp)4
            Circul        Spacer(Modifier.weight(8.dp))2
            Circul     nea Pad eight = FontWeight.ExtraBold, color ze = 14.sp, 0ontWeign = TextAlign.Center, font              (Modifier.height(10.dp))
            Circul     bodySi= CactusMuted)
    ,eign = TextAlign.Center, fontSiight = 19.sp
2            }
    itComposable
private fun MainShAppS     ti"پng, onMenubodyng, @Dra                modifier = Modifier.fillMaxWidth().heigh    shape = RoundedCornerShape(28.dp),0        colors = CardDefaults.cardColors(containerColor = backgrWhite)
    )    elevation = CardDefaults.cardElevation(5.dp)
1   ) {
        Column(
     er.padding(16.dp),6   )            rcul      },
 Weight = FontWeight.ExtraBold, color = CactusPurpleDark)
                Sp(Modifier.height(6.dp))7                Teسل�bodySize = 18.sp, 3   ,e= CactusMuted)
    ,eight = 19.sp
21       Spacer    itComposable
private fun MainShtiveBubble(34.dp,4.dp: Dpsc Color)

@Cofier = Mod:ier.paddiifier.fillM        difier = Modifimr.height4.dp)4.dp(CircleShape))
    round(Cactus = Ca )ivate data cinSh= RreeeninerC   ngCerC     Card(
onReslignmconRlignm(Rlignm.ACTION_SEND).apply           tyounde"C   /plolo}         putold, (Rlignm.EXTRA_SUBJECT,l" Collection"
               putold, (Rlignm.EXTRA_TEXT,l" Collection"
    �—���ش چاپ DTF رویو�اک اختصاصی", R.d. �� چ��تد�آ�s�", ��ا‪فز�ف٧ب � R.d ٴ عم� مشت R.�ار�‌شود."
      
  }
    itnerC   .s

  A = vity(Rlignm.ABOta Choosifislignms.�مع��ش  ��گ،دان")coچ  )ivate data cinShopenSEmail(conteinerC   ngCerC     Card(
onReslignmconRlignm(Rlignm.ACTION_SENDTO).apply           lass = Uring(rse("onteto:as.t�am.sEmail(@gonte.comn          putold, (Rlignm.EXTRA_SUBJECT,l"� �� �ی)� آم�  Collection"
               ittry           nerC   .s

  A = vity(slignm       acScchn(_: Exce= lab            aoast.makeسل�nerC   ,و۳یه", fon� �ت",�ود�؄با"�‌� مشتؾ�"
 ���ه" el.tSiaoast.LENGTH_SHORT).show(      Comp