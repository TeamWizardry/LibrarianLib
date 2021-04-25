plugins {
    java
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    maven { url = uri("https://maven.quiltmc.org/repository/release/") }
    jcenter()
    mavenCentral()
    gradlePluginPortal()
}

val gradle_kotlin_version: String by project
val gradle_dokka_version: String by project
val gradle_loom_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$gradle_dokka_version")
    implementation("org.quiltmc:loom:$gradle_loom_version")
    // rolled back from 4.0.4 to 4.0.1 due to issues with sources relocation:
    // https://github.com/johnrengelman/shadow/issues/425
    implementation("com.github.jengelman.gradle.plugins:shadow:4.0.1")
    implementation("org.freemarker:freemarker:2.3.31")
}

