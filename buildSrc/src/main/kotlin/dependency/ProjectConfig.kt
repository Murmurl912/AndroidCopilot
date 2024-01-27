package dependency

import org.gradle.api.JavaVersion

object ProjectConfig {

    const val APPLICATION_ID = "com.example.androidcopilot"

    const val ANDROID_MIN_SDK  = 26
    const val COMPILE_SDK = 33
    const val TARGET_SDK = 33

    const val APP_VERSION_NAME = "1.0"
    const val APP_VERSION_CODE = 1

    val SOURCE_JAVA_VERSION: JavaVersion = JavaVersion.VERSION_17
    val TARGET_JAVA_VERSION: JavaVersion = JavaVersion.VERSION_17
    val KOTLIN_JVM_TARGET = "17"
    val KOTLIN_JVM_TOOLCHAIN = 17
    val COMPOSE_COMPILER_EXTENSION = "1.4.3"
}