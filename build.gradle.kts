val kotlinToolchainVersion = "2.1.10"

buildscript {
    repositories {
        google()
        maven(url = "https://dl.google.com/dl/android/maven2/")
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        val nav_version = "2.5.0"
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath("io.github.xilinjia.krdb:gradle-plugin:3.2.5")
    }
}
plugins {
    id("com.android.application") version "8.6.1" apply false
    id("com.android.library") version "8.6.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
    id("com.google.firebase.crashlytics") version "2.9.1" apply false
    // Align KSP with the Kotlin plugin version to avoid classloading issues.
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" apply false
    alias(libs.plugins.androidTest) apply false
}

subprojects {
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                when (requested.name) {
                    "kotlin-stdlib-jre7" -> {
                        useTarget("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinToolchainVersion")
                        because("kotlin-stdlib-jre7 artifacts were removed; redirect to the replacement module.")
                        return@eachDependency
                    }
                    "kotlin-stdlib-jre8" -> {
                        useTarget("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinToolchainVersion")
                        because("kotlin-stdlib-jre8 artifacts were removed; redirect to the replacement module.")
                        return@eachDependency
                    }
                }
                if (requested.version != null && requested.version != kotlinToolchainVersion) {
                    useVersion(kotlinToolchainVersion)
                    because("Align Kotlin artifacts with the $kotlinToolchainVersion compiler to avoid metadata mismatches.")
                }
            }
        }
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
}
