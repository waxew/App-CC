package com.asteam.cactuscollection

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

private data class CategoryItem(
    val title: String,
    @DrawableRes val icon: Int,
    val background: Color
)

private data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val screen: AppScreen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainShell(
    currentScreen: AppScreen,
    profile: UserProfile,
    preferences: UserPreferences,
    selectedCatalog: String,
    onCatalogSelected: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onEditProfile: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showBottomBar = currentScreen in listOf(AppScreen.HOME, AppScreen.CLUB, AppScreen.CATALOG, AppScreen.ORDERS, AppScreen.PROFILE)

    fun navigate(screen: AppScreen) {
        scope.launch { drawerState.close() }
        onNavigate(screen)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = currentScreen,
                onClose = { scope.launch { drawerState.close() } },
                onNavigate = ::navigate,
                onShare = {
                    scope.launch { drawerState.close() }
                    shareApp(context)
                }
            )
        }
    ) {
        Scaffold(
            containerColor = CactusBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AppHeader(
                    title = screenTitle(currentScreen, selectedCatalog),
                    onMenu = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNav(
                        currentScreen = currentScreen,
                        onHome = { onNavigate(AppScreen.HOME) },
                        onClub = { onNavigate(AppScreen.CLUB) },
                        onOrders = { onNavigate(AppScreen.ORDERS) },
                        onProfile = { onNavigate(AppScreen.PROFILE) }
                    )
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(onCatalogSelected)
                    AppScreen.CLUB -> ClubScreen()
                    AppScreen.CATALOG -> CatalogScreen(selectedCatalog) { message -> scope.launch { snackbarHostState.showSnackbar(message) } }
                    AppScreen.ORDERS -> OrdersScreen()
                    AppScreen.SETTINGS -> SettingsScreen(preferences)
                    AppScreen.ABOUT_US -> AboutUsScreen()
                    AppScreen.CONTACT_US -> ContactUsScreen()
                    AppScreen.ABOUT_APP -> AboutAppScreen()
                    AppScreen.PROFILE -> ProfileScreen(profile, onEditProfile)
                    else -> HomeScreen(onCatalogSelected)
                }
            }
        }
    }
}

@Composable
private fun AppHeader(title: String, onMenu: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenu) {
            Icon(Icons.Rounded.Menu, contentDescription = "منوی همبرگری", tint = CactusText, modifier = Modifier.size(28.dp))
        }
        Text(title, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Image(painterResource(R.drawable.cactus_logo), null, modifier = Modifier.size(42.dp), contentScale = ContentScale.Fit)
    }
}

@Composable
private fun AppDrawer(
    currentScreen: AppScreen,
    onClose: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onShare: () -> Unit
) {
    val items = listOf(
        DrawerItem("خانه", Icons.Rounded.Home, AppScreen.HOME),
        DrawerItem("سفارش‌های من", Icons.Rounded.ReceiptLong, AppScreen.ORDERS),
        DrawerItem("باشگاه مشتریان", Icons.Rounded.Star, AppScreen.CLUB),
        DrawerItem("تنظیمات", Icons.Rounded.Settings, AppScreen.SETTINGS),
        DrawerItem("درباره ما", Icons.Rounded.Workspaces, AppScreen.ABOUT_US),
        DrawerItem("تماس با ما", Icons.Rounded.Campaign, AppScreen.CONTACT_US),
        DrawerItem("درباره نرم‌افزار", Icons.Rounded.Info, AppScreen.ABOUT_APP)
    )

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(.86f),
        drawerContainerColor = Color.White
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.cactus_logo), null, modifier = Modifier.size(68.dp))
            Column(Modifier.weight(1f)) {
                Text("CACTUS Collection", fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text("استایل خودت رو خلق کن!", color = CactusMuted, fontSize = 12.sp)
            }
            IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, "بستن") }
        }
        Spacer(Modifier.height(4.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title, fontWeight = FontWeight.SemiBold) },
                icon = { Icon(item.icon, null) },
                selected = currentScreen == item.screen,
                onClick = { onNavigate(item.screen) },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = CactusLavender,
                    selectedIconColor = CactusPurple,
                    selectedTextColor = CactusPurpleDark
                )
            )
        }
        NavigationDrawerItem(
            label = { Text("معرفی به دوستان", fontWeight = FontWeight.SemiBold) },
            icon = { Icon(Icons.Rounded.Share, null) },
            selected = false,
            onClick = onShare,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
        Spacer(Modifier.weight(1f))
        Card(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            colors = CardDefaults.cardColors(containerColor = CactusLavender.copy(alpha = .65f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("درباره نرم‌افزار", fontWeight = FontWeight.ExtraBold, color = CactusPurpleDark)
                Spacer(Modifier.height(5.dp))
                Text(
                    "سفارش چاپ DTF روی لباس، شخصی‌سازی طرح، خرید تکی و عمده و باشگاه مشتریان در یک برنامه.",
                    color = CactusMuted,
                    fontSize = 12.sp,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(7.dp))
                Text("نسخه 1.0.0", fontSize = 11.sp, color = CactusPurple)
            }
        }
    }
}

@Composable
private fun HomeScreen(onCategory: (String) -> Unit) {
    val categories = listOf(
        CategoryItem("تیشرت", R.drawable.ic_tshirt, CactusPink),
        CategoryItem("هودی", R.drawable.ic_hoodie, CactusLavender),
        CategoryItem("شلوار", R.drawable.ic_pants, CactusYellow),
        CategoryItem("چاپ اختصاصی", R.drawable.ic_printer, CactusMint),
        CategoryItem("فروش عمده", R.drawable.ic_boxes, CactusPeach),
        CategoryItem("طرح‌های آماده", R.drawable.ic_palette, CactusBlue)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(5.dp)) {
                Box(
                    Modifier.fillMaxWidth().height(190.dp).background(
                        Brush.linearGradient(listOf(Color(0xFF7A54C4), Color(0xFFB486E8), Color(0xFFF1B6D8)))
                    )
                ) {
                    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
                        Text("چاپ اختصاصی", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("روی لباس‌های خاص تو!", color = Color.White.copy(alpha = .9f), fontSize = 15.sp)
                        Spacer(Modifier.height(15.dp))
                        Button(
                            onClick = { onCategory("چاپ اختصاصی") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7DA7)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, null)
                            Spacer(Modifier.width(7.dp))
                            Text("شروع طراحی", fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        Modifier.align(Alignment.CenterEnd).padding(end = 18.dp).size(108.dp).clip(CircleShape).background(Color.White.copy(alpha = .20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_hoodie), null, modifier = Modifier.size(84.dp), tint = Color.Unspecified)
                    }
                }
            }
        }

        item {
            Text("چی دوست داری بسازی؟", fontSize = 19.sp, fontWeight = FontWeight.Black, color = CactusText)
        }

        items(categories.chunked(2)) { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { item ->
                    CategoryCard(item, Modifier.weight(1f)) { onCategory(item.title) }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(CactusLavender), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Star, null, tint = CactusPurple, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("باشگاه مشتریان CACTUS", fontWeight = FontWeight.ExtraBold)
                        Text("امتیاز بگیر، در چالش‌ها شرکت کن و تجربه‌ات را با بقیه به اشتراک بگذار.", color = CactusMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(item: CategoryItem, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(126.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = item.background),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(painterResource(item.icon), null, modifier = Modifier.size(58.dp), tint = Color.Unspecified)
            Spacer(Modifier.height(8.dp))
            Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ClubScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF65429F), Color(0xFF986FD0)))).padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = .18f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.AccountCircle, null, tint = Color.White, modifier = Modifier.size(42.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("باشگاه مشتریان", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text("سطح نقره‌ای", color = Color.White.copy(alpha = .8f), fontSize = 12.sp)
                        }
                        Text("1,250 ★", color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = 0.62f,
                        modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(10.dp)),
                        color = Color(0xFFFFD166),
                        trackColor = Color.White.copy(alpha = .20f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("750 امتیاز تا سطح طلایی", color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                ClubQuickAction("امتیازها", Icons.Rounded.Star, Modifier.weight(1f))
                ClubQuickAction("نظرات", Icons.Rounded.Comment, Modifier.weight(1f))
                ClubQuickAction("چالش‌ها", Icons.Rounded.Verified, Modifier.weight(1f))
                ClubQuickAction("پست‌ها", Icons.Rounded.Workspaces, Modifier.weight(1f))
            }
        }

        item { Text("پست‌های اخیر", fontWeight = FontWeight.Black, fontSize = 19.sp) }
        item {
            CommunityPost(
                name = "سارا طراحی",
                time = "۲ ساعت پیش",
                text = "چاپ جدیدم روی هودی مشکی آماده شد. ترکیب بنفش روی پارچه تیره خیلی خوب جواب داد.",
                icon = R.drawable.ic_hoodie,
                likes = "42",
                comments = "18"
            )
        }
        item {
            CommunityPost(
                name = "علی گرافیک",
                time = "دیروز",
                text = "برای کالکشن تابستونی این طرح مینیمال رو تست کردم. نظرتون درباره جای چاپ چیه؟",
                icon = R.drawable.ic_tshirt,
                likes = "36",
                comments = "11"
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CactusLavender),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = CactusPurple, modifier = Modifier.size(38.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("چالش فعال: طرح تابستون من", fontWeight = FontWeight.ExtraBold, color = CactusPurpleDark)
                        Text("طرح خودت را بساز؛ بهترین طرح امتیاز ویژه می‌گیرد.", color = CactusMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubQuickAction(label: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(vertical = 12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = CactusPurple)
            Spacer(Modifier.height(5.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CommunityPost(name: String, time: String, text: String, @DrawableRes icon: Int, likes: String, comments: String) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).clip(CircleShape).background(CactusLavender), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Person, null, tint = CactusPurple)
                }
                Spacer(Modifier.width(9.dp))
                Column {
                    Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Text(time, color = CactusMuted, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(text, color = CactusText, fontSize = 13.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(CactusLavender, CactusPink))),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(icon), null, modifier = Modifier.size(92.dp), tint = Color.Unspecified)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("♥ $likes", color = CactusPurple, fontSize = 12.sp)
                Text("💬 $comments", color = CactusMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CatalogScreen(category: String, onMessage: (String) -> Unit) {
    val products = when (category) {
        "هودی" -> listOf("هودی مشکی چاپ سفارشی", "هودی یاسی مینیمال", "هودی اورسایز طرح Galaxy")
        "شلوار" -> listOf("شلوار بگ چاپ کناری", "اسلش مشکی چاپ اختصاصی", "شلوار راحتی طرح مینیمال")
        "فروش عمده" -> listOf("پکیج 20 عددی", "پکیج 50 عددی", "استعلام 100 عدد به بالا")
        "طرح‌های آماده" -> listOf("کالکشن مینیمال", "کالکشن Streetwear", "کالکشن فارسی")
        "چاپ اختصاصی" -> listOf("چاپ روی سینه", "چاپ پشت کامل", "چاپ آستین / پاچه")
        else -> listOf("تیشرت سفید چاپ اختصاصی", "تیشرت مشکی طرح آماده", "تیشرت اورسایز Premium")
    }
    val icon = when (category) {
        "هودی" -> R.drawable.ic_hoodie
        "شلوار" -> R.drawable.ic_pants
        "فروش عمده" -> R.drawable.ic_boxes
        "طرح‌های آماده" -> R.drawable.ic_palette
        "چاپ اختصاصی" -> R.drawable.ic_printer
        else -> R.drawable.ic_tshirt
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CactusLavender)) {
                Column(Modifier.padding(16.dp)) {
                    Text(category, fontSize = 21.sp, fontWeight = FontWeight.Black, color = CactusPurpleDark)
                    Spacer(Modifier.height(5.dp))
                    Text("نمونه‌های نسخه 1.0؛ انتخاب رنگ، سایز، محل چاپ و آپلود طرح در توسعه بعدی به سفارش متصل می‌شود.", color = CactusMuted, fontSize = 12.sp)
                }
            }
        }
        items(products) { product ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onMessage("«$product» انتخاب شد؛ صفحه سفارش در نسخه بعد تکمیل می‌شود.") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)).background(CactusLavender.copy(alpha = .65f)), contentAlignment = Alignment.Center) {
                        Icon(painterResource(icon), null, modifier = Modifier.size(55.dp), tint = Color.Unspecified)
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(product, fontWeight = FontWeight.ExtraBold)
                        Text("قابل شخصی‌سازی", color = CactusPurple, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNav(
    currentScreen: AppScreen,
    onHome: () -> Unit,
    onClub: () -> Unit,
    onOrders: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        NavItem(currentScreen == AppScreen.HOME || currentScreen == AppScreen.CATALOG, Icons.Rounded.Home, "خانه", onHome)
        NavItem(false, Icons.Rounded.Search, "جستجو", {})
        NavItem(currentScreen == AppScreen.ORDERS, Icons.Rounded.ShoppingCart, "سفارش‌ها", onOrders)
        NavItem(currentScreen == AppScreen.CLUB, Icons.Rounded.Star, "باشگاه", onClub)
        NavItem(currentScreen == AppScreen.PROFILE, Icons.Rounded.Person, "پروفایل", onProfile)
    }
}

@Composable
private fun NavItem(selected: Boolean, icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(72.dp).clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(if (selected) CactusLavender else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (selected) CactusPurple else CactusMuted, modifier = Modifier.size(22.dp))
        }
        Text(
            label,
            fontSize = 10.sp,
            color = if (selected) CactusPurpleDark else CactusMuted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun screenTitle(screen: AppScreen, catalog: String): String = when (screen) {
    AppScreen.HOME -> "CACTUS Collection"
    AppScreen.CLUB -> "باشگاه مشتریان"
    AppScreen.ORDERS -> "سفارش‌های من"
    AppScreen.SETTINGS -> "تنظیمات"
    AppScreen.ABOUT_US -> "درباره ما"
    AppScreen.CONTACT_US -> "تماس با ما"
    AppScreen.ABOUT_APP -> "درباره نرم‌افزار"
    AppScreen.PROFILE -> "حساب کاربری"
    AppScreen.CATALOG -> catalog
    else -> "CACTUS Collection"
}

private fun shareApp(context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "CACTUS Collection")
        putExtra(Intent.EXTRA_TEXT, "CACTUS Collection — سفارش چاپ DTF و پوشاک اختصاصی. لینک دانلود پس از انتشار فروشگاهی اضافه می‌شود.")
    }
    context.startActivity(Intent.createChooser(intent, "معرفی به دوستان"))
}
