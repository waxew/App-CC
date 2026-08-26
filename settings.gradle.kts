// [AS-TEAM-DOCUMENTED]
// فایل settings.gradle.kts: تنظیمات ساخت پروژه؛ کامنت‌ها نقش بلوک‌های اصلی Gradle را توضیح می‌دهند.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CactusCollection"
include(":app")
