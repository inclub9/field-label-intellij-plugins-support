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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
}