plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

group = "inclub9"
version = "1.0-SNAPSHOT"

// ใช้ Java 17 แทน 23
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.matching("Oracle"))
    }
}

repositories {
    mavenCentral()
}

// ปรับ Kotlin compile options เป็น Java 17
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        apiVersion = "1.9"
        languageVersion = "1.9"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

intellij {
    version.set("2023.3.3")
    type.set("IC")
    updateSinceUntilBuild.set(false)
    plugins.set(listOf(
        "com.intellij.java",
        "org.jetbrains.kotlin"
    ))
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("241.*")
    }

    runIde {
        jvmArgs = listOf(
            "-XX:+UseG1GC",
            "-Xmx2g",
            "--add-exports=java.base/jdk.internal.vm.annotation=ALL-UNNAMED",
            "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED",
            "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED"
        )
        autoReloadPlugins.set(true)
    }

    wrapper {
        gradleVersion = "8.5"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation("junit:junit:4.13.2")
}