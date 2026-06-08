import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import org.gradle.api.tasks.Exec
import org.gradle.api.logging.Logger
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories

//import org.jetbrains.kotlin.gradle.idea.proto.com.google.protobuf.option


val REQUIRED_PAGE_ALIGN = 0x4000L

fun String.capitalizeVariant() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }

fun patchElfDirectory(root: Path, logger: Logger) {
    if (!Files.exists(root)) return
    Files.walk(root).use { stream ->
        stream.filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".so") }
            .forEach { tryPatchElf(it, logger) }
    }
}

fun tryPatchElf(path: Path, logger: Logger) {
    val bytes = Files.readAllBytes(path)
    if (bytes.size < 64 || bytes[0] != 0x7f.toByte() || bytes[1] != 'E'.code.toByte() || bytes[2] != 'L'.code.toByte() || bytes[3] != 'F'.code.toByte()) {
        return
    }

    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val klass = buffer.get(4).toInt() and 0xff
    val patched = when (klass) {
        1 -> patch32(buffer)
        2 -> patch64(buffer)
        else -> false
    }

    if (patched) {
        Files.write(path, bytes)
        logger.info("Aligned PT_LOAD segments to 16 KB for ${path.fileName}")
    }
}

fun patch32(buffer: ByteBuffer): Boolean {
    var patched = false
    val phoff = buffer.getInt(28).toLong() and 0xffffffffL
    val phentsize = buffer.getShort(42).toInt() and 0xffff
    val phnum = buffer.getShort(44).toInt() and 0xffff
    if (phentsize <= 0 || phnum <= 0) return false

    val capacity = buffer.capacity()
    for (i in 0 until phnum) {
        val entryOffset = phoff + i.toLong() * phentsize.toLong()
        if (entryOffset < 0 || entryOffset + phentsize > capacity) break
        val offset = entryOffset.toInt()
        val type = buffer.getInt(offset)
        if (type == 1) {
            val alignOffset = offset + 28
            val current = buffer.getInt(alignOffset).toLong() and 0xffffffffL
            if (current < REQUIRED_PAGE_ALIGN) {
                buffer.putInt(alignOffset, REQUIRED_PAGE_ALIGN.toInt())
                patched = true
            }
        }
    }
    return patched
}

fun patch64(buffer: ByteBuffer): Boolean {
    var patched = false
    val phoff = buffer.getLong(32)
    val phentsize = buffer.getShort(54).toInt() and 0xffff
    val phnum = buffer.getShort(56).toInt() and 0xffff
    if (phentsize <= 0 || phnum <= 0) return false

    val capacity = buffer.capacity()
    for (i in 0 until phnum) {
        val entryOffset = phoff + i.toLong() * phentsize.toLong()
        if (entryOffset < 0 || entryOffset + phentsize > capacity) break
        val offset = entryOffset.toInt()
        val type = buffer.getInt(offset)
        if (type == 1) {
            val alignOffset = offset + 48
            val current = buffer.getLong(alignOffset)
            if (current < REQUIRED_PAGE_ALIGN) {
                buffer.putLong(alignOffset, REQUIRED_PAGE_ALIGN)
                patched = true
            }
        }
    }
    return patched
}

fun patchNativeLibOutputs(task: org.gradle.api.Task, logger: Logger) {
    task.outputs.files.filter { it.isDirectory }.forEach { dir ->
        patchElfDirectory(dir.toPath(), logger)
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

apply(plugin = "io.github.xilinjia.krdb")

val releaseApk = layout.buildDirectory.file("outputs/apk/release/app-release.apk")

val checkRelease16kPages =
    tasks.register("checkRelease16kPages", org.gradle.api.tasks.Exec::class) {
        group = "verification"
        description =
            "Validates that all native libraries in the release APK use 16 KB PT_LOAD alignment."
        dependsOn("assembleRelease")
        inputs.files(releaseApk).withPropertyName("releaseApk")
        onlyIf {
            val apk = releaseApk.get().asFile
            if (!apk.exists()) {
                logger.lifecycle("Skipping checkRelease16kPages; APK not present at ${apk.absolutePath}")
                return@onlyIf false
            }
            true
        }
        doFirst {
            commandLine(
                "bash",
                project.rootProject.file("tools/check-16k-pages.sh").absolutePath,
                releaseApk.get().asFile.absolutePath
            )
        }
    }

tasks.matching { it.name == "assembleRelease" }.configureEach {
    finalizedBy(checkRelease16kPages)
}

tasks.matching { it.name.startsWith("merge") && it.name.endsWith("NativeLibs") }.configureEach {
    doLast {
        patchNativeLibOutputs(this, logger)
    }
}

tasks.matching { it.name.startsWith("strip") && it.name.contains("DebugSymbol") }.configureEach {
    doLast {
        patchNativeLibOutputs(this, logger)
    }
}

val releaseBundle = layout.buildDirectory.file("outputs/bundle/release/app-release.aab")

val alignReleaseBundleNativeLibs by tasks.registering(Exec::class) {
    group = "verification"
    description = "Pads native libs in the release bundle so they begin on 16 KB boundaries."
    val alignScript = rootProject.file("tools/align-bundle-native-libs.py")
    inputs.file(releaseBundle)
    commandLine("python3", alignScript.absolutePath, releaseBundle.get().asFile.absolutePath)
    onlyIf {
        releaseBundle.get().asFile.exists()
    }
}

val checkReleaseBundle16kPages =
    tasks.register("checkReleaseBundle16kPages", org.gradle.api.tasks.Exec::class) {
        group = "verification"
        description =
            "Validates that all native libraries in the release bundle use 16 KB PT_LOAD alignment."
        dependsOn("bundleRelease")
        inputs.files(releaseBundle).withPropertyName("releaseBundle")
        onlyIf {
            val bundle = releaseBundle.get().asFile
            if (!bundle.exists()) {
                logger.lifecycle(
                    "Skipping checkReleaseBundle16kPages; bundle not present at ${bundle.absolutePath}"
                )
                return@onlyIf false
            }
            true
        }
        doFirst {
            commandLine(
                "bash",
                project.rootProject.file("tools/check-16k-pages.sh").absolutePath,
                releaseBundle.get().asFile.absolutePath
            )
        }
    }

tasks.matching { it.name == "bundleRelease" }.configureEach {
    finalizedBy(alignReleaseBundleNativeLibs, checkReleaseBundle16kPages)
}

kotlin {
    jvmToolchain(17)
}
//1.1.8

android {
    namespace = "com.messenger.phone.number.text.sms.service.apps"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.messenger.phone.number.text.sms.service.apps"
        renderscriptTargetApi = 35
        renderscriptSupportModeEnabled = true
//        applicationId = "com.demo.myadsmanage"
        minSdk = 24
        targetSdk = 35

        versionCode = 231
        versionName = "2.4.1"
//        versionCode = 229
//        versionName = "2.3.9"

        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }

        manifestPlaceholders.putAll(
            mapOf(
                "izooto_app_id" to "a536fade4907c52440e9895aa0e1a092d6aeef69"
            )
        )

        setProperty("archivesBaseName","Message V$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk.debugSymbolLevel = "FULL"
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
//
    }

    aaptOptions {
        noCompress("tflite")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    kapt {
//        javacOptions {
//            option("-source", "17")
//            option("-target", "17")
//        }
        correctErrorTypes = true
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    ksp {
        arg("add-opens", "java.base/java.io=ALL-UNNAMED")
    }


    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")

    }
//    ndkVersion = "28.0.12674087 rc2"
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.com.google.android.material)
    implementation(libs.androidx.constraintlayout.constraintlayout)
    implementation(libs.org.kotlinx.coroutines.android)
    implementation(libs.org.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.com.google.dagger.hilt)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.androidx.emoji2)
    implementation(libs.androidx.activity)
//    implementation(project(":emoji"))
    testImplementation(libs.junit.v412)

    // ── Migration test dependencies ──────────────────────────────────────────
    testImplementation("org.robolectric:robolectric:4.12.1")
    testImplementation("androidx.room:room-testing:2.7.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")

    kapt(libs.com.google.dagger.compiler)
    implementation(libs.com.github.bumptech.glide)
    annotationProcessor(libs.com.github.bumptech.compiler)
    kapt(libs.com.github.bumptech.compiler)
    implementation(libs.androdx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    // Room's compiler uses kotlinx.serialization during schema export.
    // Pin a modern Json runtime on kapt classpath to avoid NoSuchMethodError.
    kapt(libs.kotlinx.serialization.json)
    implementation(libs.com.intuit.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.android.lottie)
    implementation(libs.androidx.multidex)
    implementation(libs.com.tibbi.android.smsmms)
    implementation(libs.io.libphonenumber.android)
    implementation(libs.com.googlecode.libphonenumber)

//    implementation(libs.com.google.play.services.ads)

    implementation(platform(libs.com.google.firebase.bom))
    implementation(libs.com.google.firebase.analytics.ktx)
//    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.com.google.firebase.messaging)
    implementation(libs.com.google.app.update.ktx)
    implementation(libs.com.facebook.audience.network.sdk)
    implementation(libs.com.google.ads.mediation.facebook)
//    implementation(libs.com.google.play.services.ads)
    implementation(libs.com.facebook.marketing)
    compileOnly("com.facebook.infer.annotation:infer-annotation:0.18.0")
    implementation(libs.com.google.android.billing.ktx)
//    implementation(project(":emoji-google"))
    coreLibraryDesugaring(libs.com.desugar.jdk.libs)
    implementation(libs.com.github.indicatorFastScroll)
    implementation(libs.com.google.gson)
    implementation(libs.me.saket.link.movement.method)
    implementation(libs.com.android.khirr)
    implementation(libs.com.simple.commons) {
        exclude(group = "com.andrognito.patternlockview")
        exclude(group = "com.duolingo.open")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
//    implementation(libs.com.google.mlkit.translate)
    implementation(libs.it.xabaras.recyclerview.swipedecorator)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.browser)
    implementation(libs.com.google.smart.reply)
    implementation(libs.androidx.biometric)
    implementation(libs.org.android.jsoup)
// implementation(libs.flexbox)
    implementation(libs.ez.vcard)
    implementation(libs.circleimageview)
    implementation(libs.shimmer)
    implementation("com.tbuonomo:dotsindicator:5.0")
//    implementation("com.beloo.widget:ChipsLayoutManager:0.3.7@aar")
    implementation(libs.user.messaging.platform)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coil)
    implementation(libs.androidx.percentlayout)
    implementation(libs.firebase.config.ktx)
    val playServicesVersion = "18.1.0"
    implementation("com.google.android.gms:play-services-maps:$playServicesVersion") {
        exclude(group = "com.android.support")
    }
    implementation("com.google.android.gms:play-services-location:21.1.0") {
        exclude(group = "com.android.support")
    }
    implementation("com.google.android.libraries.places:places:3.3.0") {
        exclude(group = "com.android.support")
    }
    implementation(libs.google.maps.services)
//    implementation(libs.metrica.analytics)
    implementation(libs.androidx.room.paging)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    implementation(libs.okhttp)
    implementation("com.google.android.play:review-ktx:2.0.2")
    implementation("com.github.skydoves:transformationlayout:1.1.3")
    implementation("com.github.ome450901:SimpleRatingBar:1.4.3")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.microsoft.clarity:clarity:3.4.1")

//    implementation(libs.com.google.firebase.crashlytics)

//    implementation("com.vision.aftercall.sdk:aftercall:1.6.9")

    implementation(libs.bundles.androidx.camera)
    implementation(libs.androidx.window)
    implementation(libs.purchases)
    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(project(":ChipsLayoutManager"))
//    implementation("com.onesignal:OneSignal:5.1.36")
    implementation("com.onesignal:OneSignal:[5.1.0, 5.99.99]")

    // Classic AdMob / GMA
    implementation("com.google.android.gms:play-services-ads:25.0.0")

//Google Platform Mediation
    implementation("com.facebook.android:facebook-android-sdk:18.1.3")
    implementation("com.google.ads.mediation:applovin:13.5.1.0")
    implementation("com.google.ads.mediation:unity:4.16.6.0")
    implementation("com.google.ads.mediation:vungle:7.7.0.1")
    implementation("com.google.ads.mediation:mintegral:17.0.81.0")
    implementation("com.google.ads.mediation:inmobi:11.1.0.1")
    implementation("com.google.ads.mediation:pangle:7.8.5.9.0") {
        exclude(group = "com.pangle.global", module = "tiktok-business-android-sdk-comp")
    }
    implementation("com.google.ads.mediation:ironsource:9.4.2.0")  // IronSource Mediation Adapter for AdMob
    implementation("com.unity3d.ads:unity-ads:4.16.0")  // Unity Ads SDK (required for the adapter)


//    implementation("com.google.ads.mediation:ironsource:8.5.0.1")   // IronSource Mediation Adapter for AdMob
//    implementation("com.google.ads.mediation:unity:4.13.1.0")    // Unity Ads Mediation Adapter for AdMob
//    implementation("com.unity3d.ads:unity-ads:4.13.1")    // Unity Ads SDK (required for the adapter)



//Applovin
    implementation("com.applovin:applovin-sdk:13.5.1")
    implementation("com.applovin.mediation:google-adapter:24.9.0.0")
    implementation("com.applovin.mediation:unityads-adapter:4.16.6.0")
    implementation("com.applovin.mediation:facebook-adapter:6.21.0.0")
    implementation("com.applovin.mediation:vungle-adapter:7.7.0.0")



    // krdb: Realm Kotlin SDK for Kotlin 2.x
    implementation("io.github.xilinjia.krdb:library-base:3.2.5")

    // ── Instrumented test dependencies ────────────────────────────────────────
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
