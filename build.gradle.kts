import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.GroovyObject
import net.minecraftforge.gradle.user.TaskSingleReobf
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import kotlin.concurrent.thread

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("net.minecraftforge.gradle.forge")
    id("com.jfrog.bintray")
    id("com.jfrog.artifactory")
    id("org.jetbrains.dokka")
}

val branch = prop("branch") ?: "git rev-parse --abbrev-ref HEAD".execute(rootDir.absolutePath).lines().last()
logger.info("On branch $branch")

version = "${branch.replace('/', '-')}-".takeUnless { prop("mc_version")?.contains(branch) == true }.orEmpty() + prop("mod_version") + "." + prop("build_number")
description = "A library for the TeamWizardry mods "
base.archivesBaseName = prop("mod_name") + "-" + prop("mc_version")

minecraft {
    version = "${prop("mc_version")}-${prop("forge_version")}"
    mappings = prop("mc_mappings")
    runDir = "run"
    coreMod = prop("core_plugin")

    replace("GRADLE:VERSION", prop("mod_version"))
    replace("GRADLE:BUILD", prop("build_number"))
    replaceIn("LibrarianLib.kt")
}

sourceSets["main"].allSource.srcDir("src/example/java")
sourceSets["main"].allSource.srcDir("src/api/java")
sourceSets["main"].resources.srcDir("src/example/resources")

val shade by configurations.creating // TODO: investigate contained deps

configurations.compile.extendsFrom(shade)
configurations.testCompile.extendsFrom(shade)

repositories {
    jcenter()
    maven {
        name = "Bluexin repo"
        url = uri("https://maven.bluexin.be/repository/snapshots/")
    }
    maven {
        name = "Jitpack.io"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api("net.shadowfacts:Forgelin:1.8.0")
    runtime("net.shadowfacts:Forgelin:1.8.0")

    shade("org.magicwerk:brownies-collections:0.9.13")
//    implementation("org.magicwerk:brownies-collections:0.9.13")

    shade("com.ibm.icu:icu4j:63.1")
    shade("org.msgpack:msgpack-core:0.8.16")
    shade("com.github.thecodewarrior:bitfont:-SNAPSHOT")
//    implementation("com.ibm.icu:icu4j:63.1")
//    implementation("org.msgpack:msgpack-core:0.8.16")
//    implementation("com.github.thecodewarrior:bitfont:-SNAPSHOT")
}

kotlin.experimental.coroutines = Coroutines.ENABLE

/**
 * Doing this will ensure we get the sources with replaced values
 * as defined in `minecraft` block in our sources jar.
 */
val sourceJar = tasks.replace("sourceJar", Jar::class).apply {
    from(
            tasks["sourceMainJava"],
            tasks["sourceMainKotlin"],
            tasks["sourceTestJava"],
            tasks["sourceTestKotlin"]
    )
    include("**/*.kt", "**/*.java", "**/*.scala")
    classifier = "sources"
    includeEmptyDirs = false
}

tasks {
    getByName<Jar>("jar") {
        for (dep in shade) {
            from(zipTree(dep)) {
                exclude("META-INF", "META-INF/**")
            }
        }
        exclude("*/**/librarianlibtest/**", "*/**/librarianlib.test/**")
        classifier = "fat"

        manifest {
            attributes("FMLCorePluginContainsFMLMod" to true)
        }
    }

    getByName<ProcessResources>("processResources") {
        val props = mapOf(
                "version" to project.version,
                "mcversion" to minecraft.version
        )

        inputs.properties(props)

        from(sourceSets["main"].resources.srcDirs) {
            include("mcmod.info")
            expand(props)
        }

        from(sourceSets["main"].resources.srcDirs) {
            exclude("mcmod.info")
        }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
            freeCompilerArgs += "-Xjvm-default=enable"
            freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        }
    }
}

val dokka by tasks.getting(DokkaTask::class) {
    outputDirectory = "$buildDir/docs"
    outputFormat = "javadoc"
    jdkVersion = 8
    sourceDirs =
            tasks["sourceMainJava"].outputs.files +
            tasks["sourceMainKotlin"].outputs.files +
            tasks["sourceTestJava"].outputs.files +
            tasks["sourceTestKotlin"].outputs.files

    includes = listOf("src/dokka/kotlin-dsl.md")
    doFirst {
        file(outputDirectory).deleteRecursively()
    }
}

val javadocJar by tasks.creating(Jar::class) {
    from(dokka.outputs)
    classifier = "javadoc"
}

val deobfJar by tasks.creating(Jar::class) {
    from(sourceSets["main"].output)
}

val reobfJar : TaskSingleReobf by tasks

lateinit var publication : Publication
publishing {
    publication = publications.create("publication", MavenPublication::class) {
        from(components["java"])
        artifact(reobfJar.jar) {
            builtBy(reobfJar)
            classifier = "release"
        }
        artifact(sourceJar)
        artifact(deobfJar)
//        artifact(javadocJar)
        this.artifactId = base.archivesBaseName
    }

    repositories {
        val mavenPassword = if (hasProp("local")) null else prop("mavenPassword")
        maven {
            val remoteURL = "https://maven.bluexin.be/repository/" + (if ((version as String).contains("SNAPSHOT")) "snapshots" else "releases")
            val localURL = "file://$buildDir/repo"
            url = uri(if (mavenPassword != null) remoteURL else localURL)
            if (mavenPassword != null) {
                credentials(PasswordCredentials::class.java) {
                    username = prop("mavenUser")
                    password = mavenPassword
                }
            }
        }
    }
}

bintray {
    user = prop("bintrayUser")
    key = prop("bintrayApiKey")
    publish = true
    override = true
    setPublications(publication.name)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "teamwizardry"
        name = project.name
        userOrg = "teamwizardry"
        websiteUrl = "https://github.com/TeamWizardry/LibrarianLib"
        githubRepo = "TeamWizardry/LibrarianLib"
        vcsUrl = "https://github.com/TeamWizardry/LibrarianLib"
        issueTrackerUrl = "https://github.com/TeamWizardry/LibrarianLib/issues"
        desc = project.description
        setLabels("minecraft", "mc", "modding", "forge", "library", "wizardry")
        setLicenses("LGPL-3.0")
    })
}

artifactory {
    setContextUrl("https://oss.jfrog.org")
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            val targetRepoKey = if (project.version.toString().endsWith("-SNAPSHOT")) "oss-snapshot-local" else "oss-release-local"
            setProperty("repoKey", targetRepoKey)
            setProperty("username", prop("bintrayUser"))
            setProperty("password", prop("bintrayApiKey"))
            setProperty("maven", true)
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", publication.name)
        })
    })
}

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

fun hasProp(name: String): Boolean = extra.has(name)

fun prop(name: String): String? = extra.properties[name] as? String

fun DependencyHandler.coroutine(module: String): Any =
        "org.jetbrains.kotlinx:kotlinx-coroutines-$module:${prop("coroutinesVersion")}"
