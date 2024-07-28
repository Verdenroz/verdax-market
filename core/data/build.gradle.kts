plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.verdenroz.verdaxmarket.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:network"))
    api(project(":core:database"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}
