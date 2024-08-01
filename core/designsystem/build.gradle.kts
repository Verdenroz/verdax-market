plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.android.library.compose)
}

android {
    namespace = "com.verdenroz.verdaxmarket.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    implementation(project(":core:common"))

    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.ui.text.google.fonts)
}