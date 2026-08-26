#!/usr/bin/env python3
"""Add explanatory Persian comments to project source files without changing behavior.

The product owner requested source code that can be opened later and understood
without having to ask what every major instruction, state, function, file and UI
block does. This script adds deterministic documentation comments to Kotlin,
Gradle, XML, ProGuard and workflow files. Files already marked as documented are
left untouched, making the process idempotent.
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
MARKER = "AS-TEAM-DOCUMENTED"

KOTLIN_ROOT = ROOT / "app" / "src" / "main" / "java"
KOTLIN_FILES = list(KOTLIN_ROOT.rglob("*.kt"))
OTHER_FILES = [
    ROOT / "app" / "build.gradle.kts",
    ROOT / "build.gradle.kts",
    ROOT / "settings.gradle.kts",
    ROOT / "gradle.properties",
    ROOT / "app" / "proguard-rules.pro",
    ROOT / "app" / "src" / "main" / "AndroidManifest.xml",
    ROOT / "app" / "src" / "main" / "res" / "values" / "colors.xml",
    ROOT / "app" / "src" / "main" / "res" / "values" / "strings.xml",
    ROOT / "app" / "src" / "main" / "res" / "values" / "styles.xml",
]
OTHER_FILES.extend((ROOT / "app" / "src" / "main" / "res" / "drawable").glob("*.xml"))


def function_name(line: str) -> str | None:
    match = re.search(r"\bfun\s+([A-Za-z0-9_]+)", line)
    return match.group(1) if match else None


def variable_name(line: str) -> str | None:
    match = re.search(r"\b(?:val|var)\s+([A-Za-z0-9_]+)", line)
    return match.group(1) if match else None


def class_name(line: str) -> str | None:
    match = re.search(r"\b(?:data\s+class|enum\s+class|class)\s+([A-Za-z0-9_]+)", line)
    return match.group(1) if match else None


def explain_kotlin_line(stripped: str) -> str | None:
    """Return a short explanation for a meaningful Kotlin line."""
    if not stripped or stripped.startswith(("//", "/*", "*", "package ", "import ")):
        return None
    if stripped in {"{", "}", ")", ") {", "},", "})", "];"}:
        return None
    if stripped.startswith("@"):
        return "این annotation رفتار یا نوع declaration بعدی را برای Compose/Android مشخص می‌کند."

    name = class_name(stripped)
    if name:
        return f"ساختار {name} داده‌ها یا مسئولیت مرتبط با این بخش از برنامه را مدل می‌کند."

    name = function_name(stripped)
    if name:
        return f"تابع {name} منطق یا رابط کاربری مربوط به این بخش را اجرا می‌کند."

    name = variable_name(stripped)
    if name:
        kind = "قابل‌تغییر" if re.search(r"\bvar\s+", stripped) else "ثابت/مرجع"
        return f"متغیر {name} یک مقدار {kind} موردنیاز این بخش را نگهداری می‌کند."

    if stripped.startswith("when"):
        return "این when بر اساس وضعیت فعلی، مسیر یا خروجی مناسب را انتخاب می‌کند."
    if stripped.startswith("if"):
        return "این شرط بررسی می‌کند آیا اجرای شاخه‌ی بعدی لازم است یا خیر."
    if stripped.startswith("else"):
        return "این شاخه حالت جایگزین شرط قبلی را مدیریت می‌کند."
    if stripped.startswith("return"):
        return "این دستور نتیجه را به فراخواننده برمی‌گرداند و اجرای تابع را در این مسیر تمام می‌کند."
    if stripped.startswith("Column("):
        return "Column عناصر رابط کاربری این قسمت را به‌صورت عمودی مرتب می‌کند."
    if stripped.startswith("Row("):
        return "Row عناصر رابط کاربری این قسمت را در یک ردیف افقی قرار می‌دهد."
    if stripped.startswith("Box("):
        return "Box برای هم‌پوشانی یا تراز دقیق عناصر این بخش استفاده می‌شود."
    if stripped.startswith("Card("):
        return "Card محتوای این بخش را داخل یک سطح مجزا و خوانا نمایش می‌دهد."
    if stripped.startswith("Text("):
        return "این Text متن قابل‌مشاهده توسط کاربر را نمایش می‌دهد."
    if stripped.startswith("Button("):
        return "این Button یک عمل قابل لمس را در اختیار کاربر قرار می‌دهد."
    if stripped.startswith("IconButton("):
        return "این IconButton یک عمل لمسی را با آیکون نمایش می‌دهد."
    if stripped.startswith("Icon("):
        return "این Icon نشانه‌ی بصری مرتبط با عملیات یا وضعیت را نمایش می‌دهد."
    if stripped.startswith("Image("):
        return "این Image تصویر یا لوگوی موردنیاز رابط کاربری را نمایش می‌دهد."
    if stripped.startswith("Spacer("):
        return "Spacer فاصله‌ی کنترل‌شده بین عناصر رابط کاربری ایجاد می‌کند."
    if stripped.startswith("LazyColumn("):
        return "LazyColumn محتوای عمودی را بهینه و قابل اسکرول نمایش می‌دهد."
    if stripped.startswith("Scaffold("):
        return "Scaffold چارچوب اصلی صفحه شامل نوار بالا، محتوا و نوار پایین را می‌سازد."
    if stripped.startswith("ModalNavigationDrawer("):
        return "این Drawer منوی همبرگری برنامه را نمایش و کنترل می‌کند."
    if stripped.startswith("NavigationBar("):
        return "این NavigationBar دسترسی سریع به بخش‌های اصلی پایین صفحه را فراهم می‌کند."
    if stripped.startswith("runCatching"):
        return "runCatching خطاهای احتمالی این عملیات را بدون کرش کردن برنامه مدیریت می‌کند."
    if stripped.startswith("scope.launch"):
        return "این coroutine عملیات غیرهمزمان رابط کاربری را بدون مسدود کردن صفحه اجرا می‌کند."
    if ".clickable" in stripped:
        return "clickable این عنصر را قابل لمس می‌کند و callback مربوط را اجرا می‌کند."
    return None


def document_kotlin(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    if MARKER in text:
        return

    lines = text.splitlines()
    output: list[str] = [
        f"// [{MARKER}]",
        f"// فایل {path.name}: این فایل بخشی از سورس CACTUS Collection است و کامنت‌های زیر برای توضیح منطق، UI و مسئولیت قسمت‌های مهم اضافه شده‌اند.",
    ]

    in_triple_string = False
    previous_explanation = False

    for line in lines:
        stripped = line.strip()

        triple_count = line.count('"""')
        if not in_triple_string:
            explanation = explain_kotlin_line(stripped)
            if explanation and not previous_explanation:
                indent = line[: len(line) - len(line.lstrip())]
                output.append(f"{indent}// {explanation}")
                previous_explanation = True
            else:
                previous_explanation = False
        else:
            previous_explanation = False

        output.append(line)

        if triple_count % 2 == 1:
            in_triple_string = not in_triple_string

    path.write_text("\n".join(output) + "\n", encoding="utf-8")


def document_gradle(path: Path) -> None:
    if not path.exists():
        return
    text = path.read_text(encoding="utf-8")
    if MARKER in text:
        return
    header = (
        f"// [{MARKER}]\n"
        f"// فایل {path.name}: تنظیمات ساخت پروژه؛ کامنت‌ها نقش بلوک‌های اصلی Gradle را توضیح می‌دهند.\n"
    )
    replacements = {
        "plugins {": "// پلاگین‌های موردنیاز برای ساخت اپ اندروید و کامپایل Kotlin در این بلوک فعال می‌شوند.\nplugins {",
        "android {": "// تنظیمات اصلی Android مانند SDK، نسخه برنامه، امضا و نوع Build در این بلوک قرار دارد.\nandroid {",
        "    defaultConfig {": "    // تنظیمات عمومی نسخه نصب‌شونده روی دستگاه‌ها در defaultConfig تعریف می‌شود.\n    defaultConfig {",
        "    signingConfigs {": "    // signingConfigs هویت امضای نسخه Release را مشخص می‌کند تا آپدیت‌های آینده روی نسخه قبلی نصب شوند.\n    signingConfigs {",
        "    buildTypes {": "    // buildTypes تفاوت ساخت نسخه Release و سایر خروجی‌ها را کنترل می‌کند.\n    buildTypes {",
        "dependencies {": "// کتابخانه‌های مورد استفاده برنامه در dependencies اعلام می‌شوند.\ndependencies {",
    }
    for needle, replacement in replacements.items():
        if needle in text:
            text = text.replace(needle, replacement, 1)
    path.write_text(header + text, encoding="utf-8")


def document_properties(path: Path) -> None:
    if not path.exists():
        return
    text = path.read_text(encoding="utf-8")
    if MARKER in text:
        return
    path.write_text(
        f"# [{MARKER}]\n# این فایل تنظیمات عمومی Gradle را برای کل پروژه مشخص می‌کند.\n" + text,
        encoding="utf-8",
    )


def document_proguard(path: Path) -> None:
    if not path.exists():
        return
    text = path.read_text(encoding="utf-8")
    if MARKER in text:
        return
    path.write_text(
        f"# [{MARKER}]\n# قوانین سفارشی R8/ProGuard برای بهینه‌سازی و محافظت نسخه Release در این فایل قرار می‌گیرند.\n" + text,
        encoding="utf-8",
    )


def document_xml(path: Path) -> None:
    if not path.exists():
        return
    text = path.read_text(encoding="utf-8")
    if MARKER in text:
        return
    comment = f"<!-- [{MARKER}] فایل {path.name}: منابع یا پیکربندی رابط اندروید؛ عناصر اصلی برای نگهداری آسان‌تر مستندسازی شده‌اند. -->\n"
    if text.startswith("<?xml"):
        first, rest = text.split("\n", 1)
        text = first + "\n" + comment + rest
    else:
        text = comment + text
    path.write_text(text, encoding="utf-8")


def main() -> None:
    for path in KOTLIN_FILES:
        document_kotlin(path)

    for path in OTHER_FILES:
        suffix = path.suffix.lower()
        if path.name.endswith(".gradle.kts") or path.name == "settings.gradle.kts":
            document_gradle(path)
        elif path.name == "gradle.properties":
            document_properties(path)
        elif path.name == "proguard-rules.pro":
            document_proguard(path)
        elif suffix == ".xml":
            document_xml(path)

    print(f"Documented {len(KOTLIN_FILES)} Kotlin files and Android project configuration files.")


if __name__ == "__main__":
    main()
