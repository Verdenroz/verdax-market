pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "verdaxmarket"
include(":app")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:network")
include(":feature:home")
include(":feature:quotes")
include(":feature:search")
include(":feature:watchlist")
include(":core:model")
