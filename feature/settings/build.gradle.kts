plugins {
    alias(libs.plugins.verdaxmarket.android.feature)
    alias(libs.plugins.verdaxmarket.android.library.compose)
    alias(libs.plugins.gms)
}

android {
    namespace = "com.verdenroz.verdaxmarket.feature.settings"
}

dependencies {
    implementation(projects.core.data)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}