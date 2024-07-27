plugins {
    alias(libs.plugins.verdaxmarket.jvm.library)
    alias(libs.plugins.verdaxmarket.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}