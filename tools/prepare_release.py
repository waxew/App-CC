#!/usr/bin/env python3
"""Prepare CACTUS Collection v1.0.1 source before the release build.

The script is intentionally deterministic so GitHub Actions can run it safely more
than once. It updates version metadata and keeps the About screen intentionally
short, per product requirements.
"""
from pathlib import Path
import re

VERSION_CODE = 2
VERSION_NAME = "1.0.1"

ROOT = Path(__file__).resolve().parents[1]
BUILD_FILE = ROOT / "app" / "build.gradle.kts"
INFO_SCREEN = ROOT / "app" / "src" / "main" / "java" / "com" / "asteam" / "cactuscollection" / "ScreensInfo.kt"


def update_version() -> None:
    text = BUILD_FILE.read_text(encoding="utf-8")
    text = re.sub(r"versionCode\s*=\s*\d+", f"versionCode = {VERSION_CODE}", text, count=1)
    text = re.sub(r'versionName\s*=\s*"[^"]+"', f'versionName = "{VERSION_NAME}"', text, count=1)
    BUILD_FILE.write_text(text, encoding="utf-8")


def update_about_screen() -> None:
    text = INFO_SCREEN.read_text(encoding="utf-8")
    start_marker = "@Composable\ninternal fun AboutAppScreen()"
    end_marker = "\n@Composable\ninternal fun ProfileScreen"

    if start_marker not in text or end_marker not in text:
        raise RuntimeError("AboutAppScreen markers were not found; refusing a blind source rewrite.")

    start = text.index(start_marker)
    end = text.index(end_marker, start)

    replacement = '''@Composable
internal fun AboutAppScreen() {
    // صفحه درباره نرم‌افزار عمداً کوتاه نگه داشته شده و اطلاعات فنی مثل نام بسته را به کاربر نمایش نمی‌دهد.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CactusBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // لوگوی اصلی برند برای حفظ هویت بصری برنامه نمایش داده می‌شود.
        Image(
            painter = painterResource(R.drawable.cactus_logo),
            contentDescription = "CACTUS Collection",
            modifier = Modifier.size(144.dp)
        )

        Spacer(Modifier.height(20.dp))

        // عنوان صفحه.
        Text(
            "CACTUS Collection",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = CactusText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // فقط چند خط توضیح کاربردی درباره برنامه؛ بدون نمایش package name یا جزئیات داخلی پروژه.
        Text(
            "برنامه‌ای برای مشاهده محصولات پوشاک، سفارش چاپ DTF اختصاصی، انتخاب طرح‌های آماده، ثبت سفارش تکی یا عمده و پیگیری سفارش‌ها.\n\nطراحی شده برای ساده‌تر شدن سفارش چاپ و پوشاک اختصاصی CACTUS Collection.",
            color = CactusMuted,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        // نسخه به‌صورت خودکار از BuildConfig خوانده می‌شود تا در نسخه‌های بعدی فراموش نشود.
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
    update_version()
    update_about_screen()
    print(f"Prepared CACTUS Collection {VERSION_NAME} (versionCode {VERSION_CODE}).")


if __name__ == "__main__":
    main()
