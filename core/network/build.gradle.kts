import java.util.Properties

plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
    alias(libs.plugins.kotlin.serialization)
}

val properties = Properties()
properties.load(File(projectDir, "secrets.properties").reader())

android {
    namespace = "com.verdenroz.verdaxmarket.core.network"
    defaultConfig {
        buildConfigField(
            "String",
            "financeQueryAPIKey",
            properties.getProperty("FINANCE_QUERY_API_KEY")
        )
        buildConfigField(
            "String",
            "socketURL",
            properties.getProperty("SOCKET_URL")
        )
    }
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.common)
    api(libs.okhttp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.serialization.json)
}