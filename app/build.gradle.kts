plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.realestateassistant.pro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.realestateassistant.pro"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Настройка экспорта схемы для Room
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // WorkManager с Hilt интеграцией
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // Gson для работы с JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Обновлённая навигация для Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // Room зависимости
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    testImplementation(libs.room.testing)
    
    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    
    // SQLCipher для шифрования базы данных
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Extended иконки для Material Design
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    
    // Timber для логирования
    implementation("com.jakewharton.timber:timber:4.7.1")
    
    // Библиотека календаря для Jetpack Compose
    implementation("com.kizitonwose.calendar:compose:2.5.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Pager для реализации свайпа изображений
    implementation("androidx.compose.foundation:foundation:1.6.0")
    // Добавим дополнительные модули Compose для поддержки snapping
    implementation("androidx.compose.foundation:foundation-layout:1.6.0")
    implementation("androidx.compose.animation:animation:1.6.0")
    implementation("androidx.compose.animation:animation-core:1.6.0")

    // Yandex MapKit
    implementation("com.yandex.android:maps.mobile:4.15.0-lite")
    
    // Mockito для тестирования
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("org.mockito:mockito-core:5.7.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("org.mockito:mockito-android:5.7.0")
}