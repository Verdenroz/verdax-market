plugins {
    alias(libs.plugins.verdaxmarket.android.feature)
    alias(libs.plugins.verdaxmarket.android.library.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.verdenroz.verdaxmarket.feature.search"
    buildFeatures {
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.logging)

    implementation(libs.instantsearch.android)
    implementation(libs.instantsearch.compose)
}