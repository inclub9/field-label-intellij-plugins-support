plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "inclub9.intellij.plugin"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

// ใช้แค่ toolchain อย่างเดียว
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

intellij {
    version.set("2023.3.8")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    runIde {
        maxHeapSize = "2g"
        jvmArgs("-XX:MaxMetaspaceSize=512m")
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("233")
        untilBuild.set("242.*")
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            apiVersion = "1.8"
            languageVersion = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    // ลบ sourceCompatibility และ targetCompatibility ออกจาก compileJava
    compileJava {
        options.encoding = "UTF-8"
    }
}