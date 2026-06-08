pluginManagement {
    repositories {
        google()
        maven(url = "https://dl.google.com/dl/android/maven2/")
        mavenCentral()
        gradlePluginPortal()
//        jcenter()
        maven(url = "https://jitpack.io")

    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application" || requested.id.id == "com.android.library") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven(url = "https://dl.google.com/dl/android/maven2/")
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        maven {
            url = uri("https://maven.kriadl.com/maven/")
            credentials {
                username = "vision"
                password = "vision321@"
            }
        }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle/")
        }
        maven {
            url =
                uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }
    }
}

rootProject.name = "MessagerApp"
include(":app")
//include(":jads")
include(":ChipsLayoutManager")
//include(":emoji")
//include(":emoji-google")
