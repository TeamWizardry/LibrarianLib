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
    gradlePluginPortal()
}

val gradle_kotlin_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
    implementation("net.fabricmc:fabric-loom:1.9.2") // update root buildscript block when changing this
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta11")
    implementation("org.freemarker:freemarker:2.3.31")
}

