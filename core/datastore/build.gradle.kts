import java.util.Locale

plugins {
    alias(libs.plugins.verdaxmarket.android.library)
    alias(libs.plugins.verdaxmarket.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.verdenroz.core.datastore"
}

// Get the build directory
val generatedDir = layout.buildDirectory.dir("generated/source/proto")

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

android.sourceSets.all {
    java.srcDir(generatedDir.map { it.dir("${name}/java") })
    java.srcDir(generatedDir.map { it.dir("${name}/kotlin") })
}


// Configure task dependencies
androidComponents.beforeVariants { variant ->
    tasks.matching { it.name == "ksp${variant.name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }}Kotlin" }.configureEach {
        dependsOn("generate${variant.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }}Proto")
    }
}

dependencies {
    api(libs.androidx.dataStore)
    api(libs.protobuf.kotlin.lite)
    api(projects.core.model)

    implementation(projects.core.common)
}