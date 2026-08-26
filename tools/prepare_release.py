#!/usr/bin/env python3
"""Prepare the final CACTUS Collection v1.0.1 publish source.

This script performs small, deterministic release migrations before CI builds the
signed APK. Every operation is idempotent so re-running the workflow is safe.
"""
from pathlib import Path
import re

VERSION_CODE = 2
VERSION_NAME = "1.0.1"

ROOT = Path(__file__).resolve().parents[1]
KOTLIN_ROOT = ROOT / "app" / "src" / "main" / "java" / "com" / "asteam" / "cactuscollection"
BUILD_FILE = ROOT / "app" / "build.gradle.kts"
APP_FILE = KOTLIN_ROOT / "CactusCollectionApp.kt"
MAIN_SCREEN = KOTLIN_ROOT / "ScreensMain.kt"
ONBOARDING_SCREEN = KOTLIN_ROOT / "ScreensOnboarding.kt"
INFO_SCREEN = KOTLIN_ROOT / "ScreensInfo.kt"


def update_version() -> None:
    """Bump Android version metadata without changing the stable application ID."""
    text = BUILD_FILE.read_text(encoding="utf-8")
    text = re.sub(r"versionCode\s*=\s*\d+", f"versionCode = {VERSION_CODE}", text, count=1)
    text = re.sub(r'versionName\s*=\s*"[^"]+"', f'versionName = "{VERSION_NAME}"', text, count=1)
    BUILD_FILE.write_text(text, encoding="utf-8")


def update_app_destinations() -> None:
    """Add a real Search destination while preserving the central Back stack."""
    text = APP_FILE.read_text(encoding="utf-8")

    if "AppScreen.SEARCH" not in text:
        # Add SEARCH to the enum. The regex tolerates the explanatory comments that
        # the documentation pass has already inserted around CATALOG.
        enum_match = re.search(r"(\n\s*CATALOG)(\s*\n})", text)
        if not enum_match:
            raise RuntimeError("Could not locate CATALOG in AppScreen enum.")
        text = text[: enum_match.start()] + enum_match.group(1) + ",\n    // صفحه جستجوی محصولات و خدمات.\n    SEARCH" + enum_match.group(2) + text[enum_match.end():]

    APP_FILE.write_text(text, encoding="utf-8")


def update_main_navigation_and_search() -> None:
    """Fix the dead Search tab, dynamic version label and Search page."""
    text = MAIN_SCREEN.read_text(encoding="utf-8")

    # Search is a main destination and therefore keeps the bottom navigation visible.
    text = text.replace(
        "AppScreen.ORDERS, AppScreen.PROFILE)",
        "AppScreen.ORDERS, AppScreen.PROFILE, AppScreen.SEARCH)",
        1,
    )

    # Route the Search destination to an actual screen instead of a dead button.
    if "AppScreen.SEARCH -> SearchScreen" not in text:
        needle = "                    AppScreen.ORDERS -> OrdersScreen()"
        if needle not in text:
            raise RuntimeError("Could not locate MainShell screen routing block.")
        text = text.replace(
            needle,
            "                    // صفحه جستجو از همان داده‌های دسته‌بندی نسخه محلی استفاده می‌کند.\n"
            "                    AppScreen.SEARCH -> SearchScreen(onCatalogSelected)\n"
            + needle,
            1,
        )

    # Pass a real Search callback to BottomNav.
    if "onSearch = { onNavigate(AppScreen.SEARCH) }" not in text:
        needle = "                        onHome = { onNavigate(AppScreen.HOME) },"
        if needle not in text:
            raise RuntimeError("Could not locate BottomNav call.")
        text = text.replace(
            needle,
            needle + "\n                        onSearch = { onNavigate(AppScreen.SEARCH) },",
            1,
        )

    # Add the callback to BottomNav's signature.
    if "    onSearch: () -> Unit," not in text:
        needle = "    onHome: () -> Unit,\n    onClub: () -> Unit,"
        if needle not in text:
            raise RuntimeError("Could not locate BottomNav signature.")
        text = text.replace(
            needle,
            "    onHome: () -> Unit,\n    // callback دکمه جستجو؛ قبلاً این دکمه بدون عملکرد بود.\n    onSearch: () -> Unit,\n    onClub: () -> Unit,",
            1,
        )

    # Replace the inert Search item with a functional destination.
    text = text.replace(
        '        NavItem(false, Icons.Rounded.Search, "جستجو", {})',
        '        NavItem(currentScreen == AppScreen.SEARCH, Icons.Rounded.Search, "جستجو", onSearch)',
        1,
    )

    # Show a correct title for the Search page.
    if 'AppScreen.SEARCH -> "جستجو"' not in text:
        needle = "    AppScreen.CATALOG -> catalog"
        if needle not in text:
            raise RuntimeError("Could not locate screenTitle CATALOG entry.")
        text = text.replace(
            needle,
            needle + '\n    AppScreen.SEARCH -> "جستجو"',
            1,
        )

    # Never hard-code the displayed app version in the drawer.
    text = re.sub(
        r'Text\("نسخه\s+1\.0\.0",\s*fontSize\s*=\s*11\.sp,\s*color\s*=\s*CactusPurple\)',
        'Text("نسخه ${BuildConfig.VERSION_NAME}", fontSize = 11.sp, color = CactusPurple)',
        text,
        count=1,
    )

    # Add the local Search screen once. It is deliberately simple in v1.0.1 and
    # can later be connected to the server-side product catalogue.
    if "private fun SearchScreen(" not in text:
        marker = "@Composable\nprivate fun CategoryCard"
        if marker not in text:
            raise RuntimeError("Could not locate CategoryCard insertion point.")

        search_screen = '''@Composable
private fun SearchScreen(onCategory: (String) -> Unit) {
    // state متن جستجو را بین recompositionها نگه می‌دارد.
    val queryState = remember { androidx.compose.runtime.mutableStateOf("") }

    // دسته‌های قابل جستجو در نسخه محلی برنامه.
    val searchableCategories = listOf(
        CategoryItem("تیشرت", R.drawable.ic_tshirt, CactusPink),
        CategoryItem("هودی", R.drawable.ic_hoodie, CactusLavender),
        CategoryItem("شلوار", R.drawable.ic_pants, CactusYellow),
        CategoryItem("چاپ اختصاصی", R.drawable.ic_printer, CactusMint),
        CategoryItem("فروش عمده", R.drawable.ic_boxes, CactusPeach),
        CategoryItem("طرح‌های آماده", R.drawable.ic_palette, CactusBlue)
    )

    // با خالی بودن کادر همه دسته‌ها نمایش داده می‌شوند؛ در غیر این صورت عنوان‌ها فیلتر می‌شوند.
    val filteredCategories = if (queryState.value.isBlank()) {
        searchableCategories
    } else {
        searchableCategories.filter { category ->
            category.title.contains(queryState.value.trim(), ignoreCase = true)
        }
    }

    // لیست اسکرولی باعث می‌شود صفحه روی نمایشگرهای کوچک نیز بدون بریدگی قابل استفاده باشد.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // ورودی جستجو؛ در نسخه‌های بعد به جستجوی محصولات سرور متصل می‌شود.
            androidx.compose.material3.OutlinedTextField(
                value = queryState.value,
                onValueChange = { queryState.value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("جستجو در محصولات و خدمات") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                shape = RoundedCornerShape(18.dp)
            )
        }

        if (filteredCategories.isEmpty()) {
            item {
                // پیام واضح در صورت نبود نتیجه به جای صفحه خالی نمایش داده می‌شود.
                Text(
                    "نتیجه‌ای پیدا نشد.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                    textAlign = TextAlign.Center,
                    color = CactusMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            items(filteredCategories) { category ->
                // انتخاب نتیجه، همان کاتالوگ مرتبط را باز می‌کند و در Back Stack ثبت می‌شود.
                CategoryCard(category, Modifier.fillMaxWidth()) {
                    onCategory(category.title)
                }
            }
        }
    }
}

'''
        text = text.replace(marker, search_screen + marker, 1)

    MAIN_SCREEN.write_text(text, encoding="utf-8")


def update_onboarding_validation() -> None:
    """Tighten phone validation and make the profile form safe on small screens."""
    text = ONBOARDING_SCREEN.read_text(encoding="utf-8")

    # Scroll imports are required so fields remain reachable when the keyboard is open.
    if "import androidx.compose.foundation.rememberScrollState" not in text:
        text = text.replace(
            "import androidx.compose.foundation.background\n",
            "import androidx.compose.foundation.background\n"
            "import androidx.compose.foundation.rememberScrollState\n"
            "import androidx.compose.foundation.verticalScroll\n",
            1,
        )

    # Iranian mobile numbers in this UI must be exactly 11 digits and begin with 09.
    text = text.replace(
        "val valid = phone.filter(Char::isDigit).length >= 10",
        'val valid = phone.matches(Regex("^09\\\\d{9}$"))',
        1,
    )

    # The profile form previously used a fixed Column and could be clipped by the
    # soft keyboard or on short displays. verticalScroll removes that usability bug.
    old_modifier = "Column(Modifier.fillMaxSize().background(CactusBackground).statusBarsPadding()) {"
    new_modifier = "Column(Modifier.fillMaxSize().background(CactusBackground).statusBarsPadding().verticalScroll(rememberScrollState())) {"
    if old_modifier in text:
        text = text.replace(old_modifier, new_modifier, 1)

    ONBOARDING_SCREEN.write_text(text, encoding="utf-8")


def update_about_screen() -> None:
    """Keep About Software short: app description + dynamic public version only."""
    text = INFO_SCREEN.read_text(encoding="utf-8")
    start_marker = "@Composable\ninternal fun AboutAppScreen()"
    end_marker = "\n@Composable\ninternal fun ProfileScreen"

    if start_marker not in text or end_marker not in text:
        raise RuntimeError("AboutAppScreen markers were not found; refusing a blind source rewrite.")

    start = text.index(start_marker)
    end = text.index(end_marker, start)

    replacement = '''@Composable
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
'''

    text = text[:start] + replacement + text[end:]
    INFO_SCREEN.write_text(text, encoding="utf-8")


def main() -> None:
    # ترتیب عملیات مهم است: ابتدا نسخه و منطق UI اصلاح می‌شود، سپس About جایگزین می‌شود.
    update_version()
    update_app_destinations()
    update_main_navigation_and_search()
    update_onboarding_validation()
    update_about_screen()
    print(f"Prepared CACTUS Collection {VERSION_NAME} (versionCode {VERSION_CODE}).")


if __name__ == "__main__":
    main()
