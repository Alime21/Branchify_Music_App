plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "msku.ceng.madlab.branchify_mobile_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "msku.ceng.madlab.branchify_mobile_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)

    // Firebase BOM (Bill of Materials) - manages versions for all Firebase libraries
    implementation(platform(libs.firebase.bom))

    // Firebase Cloud Firestore
    implementation(libs.firebase.firestore)

    // Firebase Authentication
    implementation(libs.firebase.auth)

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Media notification
    implementation("androidx.media:media:1.7.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}