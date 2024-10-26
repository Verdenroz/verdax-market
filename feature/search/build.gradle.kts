import java.util.Properties

plugins {
    alias(libs.plugins.verdaxmarket.android.feature)
    alias(libs.plugins.verdaxmarket.android.library.compose)
}

val properties = Properties()
properties.load(File(projectDir, "secrets.properties").reader())

android {
    namespace = "com.verdenroz.verdaxmarket.feature.search"
    defaultConfig {
        buildConfigField(
            "String",
            "algoliaAppID",
            properties.getProperty("ALGOLIA_APP_ID")
        )

        buildConfigField(
            "String",
            "algoliaAPIKey",
            properties.getProperty("ALGOLIA_API_KEY")
        )
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.instantsearch.android)
    implementation(libs.instantsearch.compose)
}