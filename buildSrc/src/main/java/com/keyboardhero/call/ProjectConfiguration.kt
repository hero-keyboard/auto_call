package com.keyboardhero.call

object Versions {
    const val COMPILE_SDK = 31
    const val TARGET_SDK = 31
    const val MIN_SDK = 21

    const val BUILD_TOOLS = "30.0.2"
    const val GRADLE_PLUGIN = "7.0.4"
    const val KOTLIN_PLUGIN = "1.5.10"

    const val COROUTINES = "1.5.0"
    const val CORE_KTX = "1.3.2"
    const val ACTIVITY = "1.3.0-rc01"
    const val APPCOMPAT = "1.2.0"
    const val CONSTRAINT_LAYOUT = "2.0.4"
    const val RECYCLER_VIEW = "1.1.0"
    const val ROOM = "2.3.0"
    const val LEGACY_SUPPORT_V4 = "1.0.0"
    const val LIFECYCLE = "2.4.0"
    const val NAVIGATION = "2.3.5"
    const val CRYPTO = "1.1.0-alpha03"
    const val HILT = "2.37"
    const val OKHTTP_LOGGING = "4.9.0"
    const val RETROFIT = "2.9.0"
    const val RETROFIT_SERIALIZATION = "0.8.0"
    const val SERIALIZATION_JSON = "1.0.1"
}

object Libs {
    object Gradle {
        const val ANDROID_BUILD_TOOL = "com.android.tools.build:gradle:${Versions.GRADLE_PLUGIN}"
        const val KOTLIN_PLUGIN =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_PLUGIN}"
    }

    object Kotlin {
        const val STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN_PLUGIN}"
        const val SERIALIZATION_JSON =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_JSON}"
    }

    object Coroutines {
        const val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
        const val ANDROID =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    }

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"

        const val LEGACY_SUPPORT_V4 =
            "androidx.legacy:legacy-support-v4:${Versions.LEGACY_SUPPORT_V4}"

        const val CORE_KOTLIN_EXTENSION = "androidx.core:core-ktx:${Versions.CORE_KTX}"

        const val CONSTRAINT_LAYOUT =
            "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"

        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"

        const val ACTIVITY_KOTLIN_EXTENSION =
            "androidx.activity:activity-ktx:${Versions.ACTIVITY}"

        const val CRYPTO = "androidx.security:security-crypto:${Versions.CRYPTO}"

        object Lifecycle {
            const val RUNTIME =
                "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
            const val COMPILER =
                "androidx.lifecycle:lifecycle-compiler:${Versions.LIFECYCLE}"
            const val VIEWMODEL_KTX =
                "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
            const val LIVEDATA_KTX =
                "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
        }

        object Room {
            const val RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
            const val COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
            const val KTX = "androidx.room:room-ktx:${Versions.ROOM}"
        }

        object Navigation {
            const val UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"
            const val FRAGMENT_KTX =
                "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
            const val SAFE_ARGS_PLUGIN =
                "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}"
        }
    }

    object DI {
        const val HILT = "com.google.dagger:hilt-android:${Versions.HILT}"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"
        const val GRADLE_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT}"
    }

    object Network {
        const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
        const val RETROFIT_CONVERTER = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val LOGGING = "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP_LOGGING}"
    }
}
