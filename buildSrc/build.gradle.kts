plugins {
    java
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
}

val gradle_kotlin_version: String by project

dependencies {
    // https://mvnrepository.com/artifact/dev.architectury.loom/dev.architectury.loom.gradle.plugin
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.7.423")
    // https://mvnrepository.com/artifact/architectury-plugin/architectury-plugin.gradle.plugin
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.4.161")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.0.21-1.0.28")
    // only required so we can import `KspExperimental` for `@OptIn(KspExperimental::class)`
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.28")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.6")
    implementation("org.freemarker:freemarker:2.3.31")
}

