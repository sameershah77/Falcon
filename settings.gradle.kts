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
        jcenter()
        maven(url = "https://jitpack.io")
        maven (url = "https://maven.google.com")
        mavenCentral()
    }
}

rootProject.name = "Falcon"
include(":app")
 