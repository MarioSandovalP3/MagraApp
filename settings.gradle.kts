rootProject.name = "cmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ─── Auto-configuración del Android SDK ──────────────────────
// Si no existe local.properties, busca el SDK en ubicaciones comunes
// y crea el archivo automáticamente.
val localPropertiesFile = java.io.File(rootDir, "local.properties")
if (!localPropertiesFile.exists()) {
    val possibleSdkPaths = listOf(
        System.getenv("ANDROID_HOME"),
        System.getenv("ANDROID_SDK_ROOT"),
        "${System.getProperty("user.home")}\\AppData\\Local\\Android\\Sdk",
        "C:\\Android",
        "${System.getProperty("user.home")}\\Android\\Sdk",
        "/usr/local/share/android-sdk",
        "${System.getProperty("user.home")}/Android/Sdk"
    )

    val foundSdk = possibleSdkPaths
        .filterNotNull()
        .filter { it.isNotBlank() }
        .firstOrNull { path ->
            val dir = java.io.File(path)
            dir.exists() && java.io.File(dir, "platforms").exists()
        }

    if (foundSdk != null) {
        val normalizedPath = foundSdk.replace("\\", "\\\\")
        localPropertiesFile.writeText("sdk.dir=$normalizedPath\n")
        println("✅ Android SDK auto-detectado en: $foundSdk")
        println("   → local.properties creado automáticamente")
    } else {
        println("⚠️  Android SDK no detectado. Para compilar en Android:")
        println("   Crea 'local.properties' con: sdk.dir=C:\\\\ruta\\\\al\\\\sdk")
        println("   O define la variable de entorno ANDROID_HOME")
    }
}

// ─── Auto-configuración del JDK ─────────────────────────────
// Gradle con toolchains (foojay-resolver-convention) puede
// descargar automáticamente el JDK necesario si no está instalado.
// La configuración jvmToolchain(17) en los build.gradle.kts
// le indica qué versión descargar.

include(":androidApp")
include(":composeApp")
include(":server")
include(":shared")
