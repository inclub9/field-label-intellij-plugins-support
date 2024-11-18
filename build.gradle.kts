plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.github.inclub9"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://cache-redirector.jetbrains.com/plugins.jetbrains.com/maven")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("com.github.inclub9:field-label:2.5.1")
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2024.1.1")
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf(
        "com.intellij.java"
    ))
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("241.*")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.9"
            apiVersion = "1.9"
        }
    }

    runIde {
        jvmArgs("-Xmx2g")
        autoReloadPlugins.set(true)  // เพิ่มการ auto reload plugins
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}