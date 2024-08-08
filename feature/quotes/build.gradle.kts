plugins {
    alias(libs.plugins.verdaxmarket.android.feature)
    alias(libs.plugins.verdaxmarket.android.library.compose)
}

android {
    namespace = "com.verdenroz.verdaxmarket.feature.quotes"

}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
}