plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
}

android {
    namespace = "com.verdenroz.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}