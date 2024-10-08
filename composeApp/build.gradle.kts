import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.libres)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.compose.compiler)
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if(localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val keystoreFile = File("/home/runner/work/EinkaufszettelNext/EinkaufszettelNext/app/keystore/android_keystore.keystore")
val isCI = keystoreFile.exists()
val appVersionName = project.properties["app.versionName"] as String
val appVersionCode = (project.properties["app.versionCode"] as String).toInt()
val supabaseUrl = "\"${localProperties["supabase.url"] ?: System.getenv("SUPABASE_URL")}\""
val supabaseKey = "\"${localProperties["supabase.key"] ?: System.getenv("SUPABASE_KEY")}\""
val googleClientId = "\"${localProperties["google.clientId"] ?: System.getenv("GOOGLE_CLIENT_ID")}\""
val appNamespace = "io.github.jan.einkaufszettel"

version = appVersionName

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.semver)
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.libres)
            implementation(libs.voyager.navigator)
           // implementation(libs.composeImageLoader)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.voyager)
            implementation(libs.bundles.supabase)
            implementation(libs.sqlDelight.coroutines)
            implementation(libs.windowsizeclass)
            implementation(libs.okio)
            implementation(libs.richeditor)
            //implementation(libs.filepicker)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.filekit)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activityCompose)
            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.compose.uitooling)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqlDelight.driver.android)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.zoomable)
        }

        jsMain.dependencies {
            implementation(libs.sqlDelight.driver.js)
            implementation(devNpm("copy-webpack-plugin", "11.0.0"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.0"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(libs.ktor.client.js)
        }

    }
}

android {
    namespace = appNamespace
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        applicationId = "io.github.jan.einkaufszettel.androidApp"
        versionCode = appVersionCode
        versionName = appVersionName
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    signingConfigs {
       /* create("release") {
            storeFile = if(isCI) keystoreFile else rootProject.file("einkaufszettel.keystore")
            storePassword = localProperties.getProperty("SIGNING_STORE_PASSWORD") ?: System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = localProperties.getProperty("SIGNING_KEY_ALIAS") ?: System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = localProperties.getProperty("SIGNING_KEY_PASSWORD") ?: System.getenv("SIGNING_KEY_PASSWORD")
        }*/
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        named("debug") {
         //   signingConfig = signingConfigs.getByName("release")
        }
    }
}

afterEvaluate {
    tasks {
        val configureJs: Task.() -> Unit = {
            dependsOn(named("jsDevelopmentExecutableCompileSync"))
            dependsOn(named("jsProductionExecutableCompileSync"))
            dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

            /*dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
            dependsOn(named("wasmJsProductionExecutableCompileSync"))
            dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))*/
        }
        named("jsBrowserProductionWebpack").configure(configureJs)
       // named("wasmJsBrowserProductionWebpack").configure(configureJs)
    }
}

libres {
    // https://github.com/Skeptick/libres#setup
}
tasks.getByPath("jsProcessResources").dependsOn("libresGenerateResources")

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
    packageName = appNamespace
    buildConfigField("String", "SUPABASE_URL", supabaseUrl)
    buildConfigField("String", "SUPABASE_KEY", supabaseKey)
    buildConfigField("String", "GOOGLE_CLIENT_ID", googleClientId)
    buildConfigField("String", "APP_VERSION_NAME", "\"$appVersionName\"")
}

sqldelight {
    databases {
        create("Einkaufszettel") {
            packageName.set("io.github.jan.einkaufszettel.db")
            generateAsync.set(true)
        }
    }
}
