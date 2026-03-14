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
        // JitPack for Pollinet SDK (when published)
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ShieldMesh"
include(":app")
