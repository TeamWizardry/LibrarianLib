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
        languageVersion = "1.5"
        apiVersion = "1.5"

        jvmTarget = "1.8"
        javaParameters = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.ExperimentalStdlibApi,kotlin.ExperimentalUnsignedTypes,kotlin.contracts.ExperimentalContracts",
            "-Xinline-classes"
        )
    }
}
