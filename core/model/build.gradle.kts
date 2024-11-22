plugins {
    alias(libs.plugins.verdaxmarket.jvm.library)
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
}
