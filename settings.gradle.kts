pluginManagement {
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

rootProject.name = "vetcare-android-kotlin-compose-mvvm"
include(":app")

// Redirigir build fuera de iCloud Drive para evitar duplicados con espacios
gradle.beforeProject {
    val iCloudBuildDir = file("/tmp/vetcare-build")
    layout.buildDirectory.set(iCloudBuildDir.resolve(name))
}

