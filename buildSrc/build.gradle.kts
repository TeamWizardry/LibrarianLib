plugins {
    java
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
    mavenCentral()
    gradlePluginPortal()
}

val gradle_kotlin_version: String by project

dependencies {
    // https://mvnrepository.com/artifact/dev.architectury.loom/dev.architectury.loom.gradle.plugin
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.7.423")
    // https://mvnrepository.com/artifact/architectury-plugin/architectury-plugin.gradle.plugin
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.4.161")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
//    implementation("net.fabricmc:fabric-loom:1.9.2") // update root buildscript block when changing this
//    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta11")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("org.freemarker:freemarker:2.3.31")
}

