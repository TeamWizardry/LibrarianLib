import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.devtools.ksp.KspExperimental

plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
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
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)

        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters = true
        optIn.addAll(
            "kotlin.ExperimentalStdlibApi",
            "kotlin.ExperimentalUnsignedTypes",
            "kotlin.contracts.ExperimentalContracts",
        )
        freeCompilerArgs.addAll(
            "-Xjvm-default=all"
        )
    }
}

sourceSets {
    main {
        resources.srcDir(generatedResourcesDir)
    }
}

ksp {
    @OptIn(KspExperimental::class)
    useKsp2.set(true)
}

dependencies {
    "ksp"("dev.zacsweers.autoservice:auto-service-ksp:1.2.0")
    implementation("com.google.auto.service:auto-service-annotations:1.1.0")
    implementation(kotlin("reflect"))
}
