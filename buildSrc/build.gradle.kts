plugins {
    java
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    maven { url = uri("https://files.minecraftforge.net/maven") }
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    jcenter()
    mavenCentral()
    gradlePluginPortal()
}

val gradle_Kotlin_version: String by project
val gradle_ForgeGradle_version: String by project
val gradle_MixinGradle_version: String by project
val gradle_Dokka_version: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_Kotlin_version")
    implementation("net.minecraftforge.gradle:ForgeGradle:$gradle_ForgeGradle_version")
    implementation("org.spongepowered:mixingradle:$gradle_MixinGradle_version")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$gradle_Dokka_version")
    // rolled back from 4.0.4 to 4.0.1 due to issues with sources relocation:
    // https://github.com/johnrengelman/shadow/issues/425
    implementation("com.github.jengelman.gradle.plugins:shadow:4.0.1")
    implementation("org.freemarker:freemarker:2.3.31")
}

