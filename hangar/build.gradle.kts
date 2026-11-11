plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.proyecto_juego_kiko"
    compileSdk { version = release(37) }

    defaultConfig {
        applicationId = "com.example.proyecto_juego_kiko.hangar"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity.ktx)
    implementation(libs.material)
}
