import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("kotlin")
    id("minecraft-conventions")
    id("org.spongepowered.mixin")
}

apply<LibLibModulePlugin>()

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-Xuse-experimental=kotlin.Experimental",
            "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
            "-Xinline-classes"
        )
    }
}