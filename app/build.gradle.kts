import dependency.ProjectConfig
import dependency.Dependencies

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.androidcopilot"
    compileSdk = ProjectConfig.COMPILE_SDK

    defaultConfig {
        applicationId = ProjectConfig.APPLICATION_ID
        minSdk = ProjectConfig.ANDROID_MIN_SDK
        targetSdk = ProjectConfig.TARGET_SDK
        versionCode = ProjectConfig.APP_VERSION_CODE
        versionName = ProjectConfig.APP_VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "OPENAI_TOKEN", dependency.Config.openaiToken())
            buildConfigField("String", "OPENAI_API", dependency.Config.openaiApi())
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "OPENAI_TOKEN", dependency.Config.openaiToken())
            buildConfigField("String", "OPENAI_API", dependency.Config.openaiApi())
        }
    }
    compileOptions {
        sourceCompatibility = ProjectConfig.SOURCE_JAVA_VERSION
        targetCompatibility = ProjectConfig.TARGET_JAVA_VERSION
    }
    kotlinOptions {
        jvmTarget = ProjectConfig.KOTLIN_JVM_TARGET
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ProjectConfig.COMPOSE_COMPILER_EXTENSION
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        noCompress.add("tflite")
    }
}
kotlin {
    jvmToolchain(ProjectConfig.KOTLIN_JVM_TOOLCHAIN)
}
dependencies {

    implementation(Dependencies.Moshi)
    implementation(Dependencies.MoshiAdapters)
    implementation("com.google.android.material:material:1.9.0")
    kapt(Dependencies.MoshiCodeGen)

    implementation(Dependencies.Retrofit)
    implementation(Dependencies.RetrofitMoshiConverter)

    implementation(Dependencies.AndroidxCoreKtx)
    implementation(Dependencies.LifecycleRuntimeKtx)
    implementation(Dependencies.ActivityCompose)
    implementation(platform(Dependencies.ComposeBom))
    implementation(Dependencies.ComposeUi)
    implementation(Dependencies.ComposeUiGraphics)
    implementation(Dependencies.ComposeUiToolingPreview)
    implementation(Dependencies.ComposeMaterial3)
    implementation(Dependencies.ComposeMaterialIconExtended)
    implementation(Dependencies.ComposeConstraintLayout)
    implementation(Dependencies.ColiCompose)
    implementation(Dependencies.ComposeAudiowaveForm)

    implementation(Dependencies.AndroidXStartup)
    implementation(Dependencies.AccompanistPermission)
    implementation(Dependencies.NavigationCompose)

    implementation(Dependencies.RoomRuntime)
    implementation(Dependencies.RoomKtx)
    implementation(Dependencies.RoomPaging)
    annotationProcessor(Dependencies.RoomCompiler)
    kapt(Dependencies.RoomCompiler)


    implementation(Dependencies.AndroidHlit)
    implementation(Dependencies.HlitComposeNavigation)
    kapt(Dependencies.HiltAndroidCompiler)

    implementation(Dependencies.AallamOpenAi)
    implementation(Dependencies.KtorAndroidClient)
    implementation(Dependencies.ComposeWindowSizeClass)

    implementation(Dependencies.LincollincolAmplituda)

    implementation(Dependencies.TensorflowLiteTaskAudio)
    implementation(Dependencies.TensorflowLiteGpuDelegatePlugin)

    testImplementation(Dependencies.Junit)
    androidTestImplementation(Dependencies.AndroidxTestExtJunit)
    androidTestImplementation(Dependencies.AndroidTestEspressoCore)
    androidTestImplementation(platform(Dependencies.ComposeBom))
    androidTestImplementation(Dependencies.AndroidxComposeUiTestJunit)
    debugImplementation(Dependencies.ComposeUiTooling)
    debugImplementation(Dependencies.ComposeUiTestManifest)
}