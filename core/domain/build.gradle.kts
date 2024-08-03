plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
}

android {
    namespace = "com.verdenroz.verdaxmarket.domain"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:model"))
}