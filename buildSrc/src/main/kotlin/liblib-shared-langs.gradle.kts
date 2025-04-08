import gradle.kotlin.dsl.accessors._3ad33576735bd3c2f3bc8765e93a6b18.main
import gradle.kotlin.dsl.accessors._3ad33576735bd3c2f3bc8765e93a6b18.sourceSets
import gradle.kotlin.dsl.accessors._3ad33576735bd3c2f3bc8765e93a6b18.test
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("kotlin")
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.release = 21
}

configure<KotlinProjectExtension> {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // because for some unknown reason, unless instructed otherwise, IDEA decides the API should be Kotlin 1.4
        languageVersion = "2.1"
        apiVersion = "2.1"

        jvmTarget = "21"
        javaParameters = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xjvm-default=all",
            "-Xopt-in=kotlin.ExperimentalStdlibApi,kotlin.ExperimentalUnsignedTypes,kotlin.contracts.ExperimentalContracts",
            "-Xinline-classes"
        )
    }
}


sourceSets {
    main {
        resources.srcDir(generatedResourcesDir)
    }
}
