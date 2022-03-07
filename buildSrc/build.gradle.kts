plugins {
    java
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    mavenCentral()
    jcenter()
    gradlePluginPortal()
}

val gradle_kotlin_version: String by project
val gradle_dokka_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$gradle_dokka_version")
    // updating loom past 10.x breaks java-objc-bridge natives for some reason
    // if this breaks 1.18 builds I'll figure out how to update it
    implementation("net.fabricmc:fabric-loom:0.10.64") // update root buildscript block when changing this
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("org.freemarker:freemarker:2.3.31")
}

