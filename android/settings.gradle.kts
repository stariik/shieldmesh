pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // TODO: Add JitPack for Pollinet dependency when available
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ShieldMesh"
include(":app")
