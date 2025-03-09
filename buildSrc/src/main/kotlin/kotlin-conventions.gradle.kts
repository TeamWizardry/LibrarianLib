import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
}

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // because for some unknown reason, unless instructed otherwise, IDEA decides the API should be Kotlin 1.4
        languageVersion = "2.1"
        apiVersion = "2.1"

        jvmTarget = "17"
        javaParameters = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.ExperimentalStdlibApi,kotlin.ExperimentalUnsignedTypes,kotlin.contracts.ExperimentalContracts",
            "-Xinline-classes",
            "-Xjvm-default=all"
        )
    }
}
