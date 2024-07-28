plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.android.room)
    alias(libs.plugins.verdaxmarket.hilt)
}

android {
    namespace = "com.verdenroz.verdaxmarket.database"
}

dependencies {
    api(project(":core:model"))

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
