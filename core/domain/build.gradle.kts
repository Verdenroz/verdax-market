plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
}

android {
    namespace = "com.verdenroz.verdaxmarket.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
}