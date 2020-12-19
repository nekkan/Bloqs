import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version VersionList.kotlin
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    jcenter()
}

val osArch = System.getProperty("os.arch")

val lwjglNatives = when(OperatingSystem.current()) {
    OperatingSystem.LINUX -> getLinuxForLwjgl()
    OperatingSystem.MAC_OS -> "natives-macos"
    OperatingSystem.WINDOWS -> if(osArch.contains("64")) "natives-windows" else "natives-windows-x86"
    else -> error("Unrecognized or unsupported operating system. Please set `lwjglNatives` manually.")
}

fun getLinuxForLwjgl(): String = with(osArch) {
    when {
        startsWith("arm") || startsWith("aarch") -> {
            val name = if(contains("armv8") || contains("64")) "arm64" else "arm32"
            "natives-linux-$name"
        }
        else -> "natives-linux"
    }
}

dependencies {
    implementation(platform(notation = "org.lwjgl:lwjgl-bom:${VersionList.lwjgl}"))
    implementation(group = "org.lwjgl", name = "lwjgl")
    implementation(group = "org.lwjgl", name = "lwjgl-assimp")
    implementation(group = "org.lwjgl", name = "lwjgl-glfw")
    implementation(group = "org.lwjgl", name = "lwjgl-openal")
    implementation(group = "org.lwjgl", name = "lwjgl-opengl")
    implementation(group = "org.lwjgl", name = "lwjgl-stb")
    implementation(group = "org.lwjgl", name = "lwjgl-vulkan")
    runtimeOnly(group = "org.lwjgl", name = "lwjgl", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-stb", classifier = lwjglNatives)
    if(lwjglNatives == "natives-macos") {
        runtimeOnly(group = "org.lwjgl", name = "lwjgl-vulkan", classifier = lwjglNatives)
    }
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = VersionList.coroutines)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += CompilerArgs
    }
}

// Ensure that the encoding is set to UTF-8, no matter what the system default is this fixes some edge cases with special
// characters not displaying correctly.
// Learn more: http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
