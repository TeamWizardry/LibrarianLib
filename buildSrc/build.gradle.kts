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

val gradle_kotlin_version = project.property("gradle_kotlin_version") as String

dependencies {
    // https://mvnrepository.com/artifact/dev.architectury.loom/dev.architectury.loom.gradle.plugin
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.17.491")
    // https://mvnrepository.com/artifact/architectury-plugin/architectury-plugin.gradle.plugin
    implementation("architectury-plugin:architectury-plugin.gradle.plugin:3.5.169")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$gradle_kotlin_version")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.11")
    // only required so we can import `KspExperimental` for `@OptIn(KspExperimental::class)`
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.11")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.6")
    implementation("org.freemarker:freemarker:2.3.31")
}

