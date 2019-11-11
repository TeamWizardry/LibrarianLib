@file:Suppress("PropertyName")

import net.minecraftforge.gradle.userdev.tasks.GenerateSRG
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.concurrent.thread
import kotlin.reflect.KProperty

plugins {
    idea
    eclipse
    `maven-publish`
    java
    kotlin("jvm")
    id("net.minecraftforge.gradle")
}

apply<LibrarianLibDevPlugin>()

val mc_version: String by gradleProperties
val forge_version: String by gradleProperties
val mc_mappings: String by gradleProperties

val branch: String = gradleProperties["branch"] ?: "git rev-parse --abbrev-ref HEAD".execute(rootDir.absolutePath).lines().last()
logger.info("On branch $branch")

val mod_version_suffix = if(mc_version.contains(branch)) "" else "-${branch.split('/').last()}"

val mod_version: String by gradleProperties
val mod_name: String by gradleProperties
val mod_group: String by gradleProperties

// ===================================================== Common ===================================================== //

allprojects {
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "net.minecraftforge.gradle")


    group = mod_group
    version = mod_version + mod_version_suffix
    if(project == rootProject)
        base.archivesBaseName = "$mod_name-$mc_version"
    else
        base.archivesBaseName = "librarianlib-${project.name}"

    minecraft {
        mappings = "$mc_mappings-$mc_version"
    }

    repositories {
        maven(url = "http://maven.shadowfacts.net/")
        maven(url = "https://jitpack.io")
    }

    val contained by configurations.creating

    configurations.compile.extendsFrom(contained)

    dependencies {
        minecraft("net.minecraftforge:forge:$mc_version-$forge_version")
        if(project == rootProject)
            contained(kotlin("stdlib-jdk8"))
        else
            compile(kotlin("stdlib-jdk8"))
    }

//    if(project !in setOf(project(":testbase"), project(":core"), project(":virtualresources"), rootProject)) {
//        dependencies {
//            testCompileOnly(project(":testbase"))
//        }
//    }

    fun AbstractCopyTask.appendTomlDependencies() {
        val dependencies = configurations.compileOnly.allDependencies.filterIsInstance<ProjectDependency>()
        filesMatching("META-INF/mods.toml") {
            filter { line ->
                if (line == "## module_dependencies ##")
                    dependencies.joinToString("\n") { dep ->
                        """
                        [[dependencies.librarianlib-${project.name}]]
                            modId="librarianlib-${dep.dependencyProject.name}"
                            mandatory=true
                            versionRange="*"
                        """.trimIndent()
                    }
                else
                    line
            }
        }
    }

    tasks.getByName<Jar>("jar") {
        doFirst {
            appendTomlDependencies()
        }
    }

    tasks.create<Jar>("mavenJar") {
        val jarInJar = containedDeps()
        doFirst {
            from(java.sourceSets["main"].output)

            appendTomlDependencies()

            jarInJar {
                add(configurations["contained"])
            }
        }

        classifier = "maven"
    }

    reobf.create("mavenJar") {
        dependsOn(tasks.getByName("createMcpToSrg"))
        mappings = tasks.getByName<GenerateSRG>("createMcpToSrg").output
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceSets {
            sourceSets["test"].compileClasspath += sourceSets["main"].compileClasspath
            sourceSets["test"].runtimeClasspath += sourceSets["main"].runtimeClasspath
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xjvm-default=enable",
                "-Xuse-experimental=kotlin.Experimental"
            )
        }
        destinationDir = File(destinationDir.absolutePath.replace("kotlin/([^/]+)$".toRegex(), "java/$1"))
    }
}

// ====================================================== Root ====================================================== //
dependencies {
    println("root dependencies")
    subprojects.forEach {
        println("root depends on: ${it.name}")
        compileOnly(it.java.sourceSets["main"].output)
        compileOnly(it.java.sourceSets["test"].output)
    }
    val runtimeClasspath = project.files()
    runtimeClasspath.from(project.java.sourceSets["main"].runtimeClasspath)
    subprojects.forEach {
        runtimeClasspath.from(it.configurations["contained"])
        runtimeClasspath.from(it.java.sourceSets["main"].runtimeClasspath)
        runtimeClasspath.from(it.java.sourceSets["test"].runtimeClasspath)
    }
    project.java.sourceSets["main"].runtimeClasspath = runtimeClasspath
}

tasks.create<Jar>("fatJar") {
    subprojects.forEach {
        dependsOn(it.tasks.getByName("assemble"))
    }
    val jarInJar = containedDeps()

    from(java.sourceSets["main"].output)

    doFirst {
        jarInJar {
            add(configurations["contained"])
            subprojects.forEach {
                if(it == project(":testbase"))
                    return@forEach
                add(it.configurations["contained"])
                add("${it.group}:${it.name}:${it.version}", it.tasks.getByName<Jar>("jar").archivePath)
            }
        }
    }

    classifier = "fat"
}

reobf.create("fatJar") {
    dependsOn(tasks.getByName("createMcpToSrg"))
    mappings = tasks.getByName<GenerateSRG>("createMcpToSrg").output
}

minecraft {
    runs {
        "client" {
            workingDirectory(project.file("run"))
            singleInstance(true)

            // Recommended logging data for a userdev environment
//            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

            // Recommended logging level for the console
            property("forge.logging.console.level", "debug")

            mods {
                allprojects.forEach {
                    val name = "librarianlib" + if(it == rootProject) "" else "-${it.name}"
                    name {
                        val ss = it.java.sourceSets["main"]
                        classes(ss.output.classesDirs)
                        resource(ss.resources.sourceDirectories)
                    }
                    "$name-testmod" {
                        val ss = it.java.sourceSets["test"]
                        classes(ss.output.classesDirs)
                        resource(ss.resources.sourceDirectories)
                    }
                }
            }
        }

        "server" {
            workingDirectory(project.file("run"))
            singleInstance(true)

            // Recommended logging data for a userdev environment
//            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

            // Recommended logging level for the console
            property("forge.logging.console.level", "debug")

            mods {
                "librarianlib" {
                    source(java.sourceSets["main"])

                }
                "librarianlib-testmod" {
                    source(java.sourceSets["test"])
                }
                subprojects.forEach {
                    "librarianlib-${it.name}" {
                        source(it.java.sourceSets["main"])
                    }
                    "librarianlib-${it.name}-testmod" {
                        source(it.java.sourceSets["test"])
                    }
                }
            }
        }
    }
}

tasks.register("createModule", CreateModuleTask::class.java)

// ==================================================== Utilities =================================================== //

fun String.execute(wd: String? = null, ignoreExitCode: Boolean = false): String =
    split(" ").execute(wd, ignoreExitCode)

fun List<String>.execute(wd: String? = null, ignoreExitCode: Boolean = false): String {
    val process = ProcessBuilder(this)
        .also { pb -> wd?.let { pb.directory(File(it)) } }
        .start()
    var result = ""
    val errReader = thread { process.errorStream.bufferedReader().forEachLine { logger.error(it) } }
    val outReader = thread {
        process.inputStream.bufferedReader().forEachLine { line ->
            logger.debug(line)
            result += line
        }
    }
    process.waitFor()
    outReader.join()
    errReader.join()
    if (process.exitValue() != 0 && !ignoreExitCode) error("Non-zero exit status for `$this`")
    return result
}

val Project.gradleProperties: ProjectPropertiesDelegate
    get() = ProjectPropertiesDelegate(this)

@Suppress("UNCHECKED_CAST")
class ProjectPropertiesDelegate(private val project: Project) {
    operator fun <T> getValue(receiver: Any?, property: KProperty<*>): T = this[property.name]
    operator fun <T> get(name: String): T = project.extra.properties[name] as T
    operator fun contains(name: String): Boolean = project.extra.properties[name] != null
}
