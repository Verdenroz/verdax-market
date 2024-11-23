plugins {
    alias(libs.plugins.verdaxmarket.android.library)
}

android {
    namespace = "com.verdenroz.verdaxmarket.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)
}
