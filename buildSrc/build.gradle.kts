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

dependencies {
    implementation(libs.buildSrc.plugins.loom)
    implementation(libs.buildSrc.plugins.architectury)
    implementation(libs.buildSrc.plugins.kotlin)
    implementation(libs.buildSrc.plugins.ksp)
    // only required so we can import `KspExperimental` for `@OptIn(KspExperimental::class)`
    implementation(libs.buildSrc.deps.ksp)
    implementation(libs.buildSrc.plugins.shadow)
    implementation(libs.buildSrc.deps.freemarker)
}
