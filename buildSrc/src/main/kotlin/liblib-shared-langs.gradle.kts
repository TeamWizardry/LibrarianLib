import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import com.google.devtools.ksp.KspExperimental

plugins {
    id("java")
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.release = 21
}

kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)

        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters = true
        optIn.addAll(
            "kotlin.ExperimentalStdlibApi",
            "kotlin.ExperimentalUnsignedTypes",
            "kotlin.contracts.ExperimentalContracts",
            "com.teamwizardry.librarianlib.LibLibInternal",
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
    "ksp"(getLibrary("build_autoServiceKsp"))
    implementation(getLibrary("build_autoServiceAnnotations"))
    implementation(kotlin("reflect"))
}
