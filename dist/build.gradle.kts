@file:Suppress("PublicApiImplicitType", "UnstableApiUsage")

import net.fabricmc.loom.configuration.JarManifestConfiguration
import java.util.jar.Manifest

plugins {
    `java-library`
    `publish-conventions`
}

configurations {
    create("include") {
        canBe(consumed = false, resolved = true)
        isTransitive = false

        attributes.attribute(
            LibLibAttributes.Target.attribute,
            LibLibAttributes.Target.public
        )
    }
}

dependencies {
    commonConfig.modules.forEach {
        "include"(it.project)
    }
}

val generated: File = file("$buildDir/generated/resources")
val generateFabricMod = tasks.register<GenerateFabricModJson>("generateFabricMod") {
    outputRoot.set(generated)
}

version = commonConfig.version

configureFabricModJson {
    id.set("librarianlib")
    version.set(commonConfig.version)

    name.set(project.property("mod.modmenu.liblib_name") as String)
    description.set(project.property("mod.modmenu.liblib_description") as String)
    icon.set("ll/icon.png")
    iconFile.set(rootDir.resolve("logo/icon.png"))

    depends("fabricloader", project.property("mod.dependencies.fabricloader") as String)
    depends("minecraft", project.property("mod.dependencies.minecraft") as String)
    depends("fabric-language-kotlin", project.property("mod.dependencies.flk") as String)

    modMenu.hidden.set(true)

    jars.set(project.provider { configurations["include"].resolve().map { it.name } })
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateFabricMod)
}

val jar = tasks.named<Jar>("jar") {
    archiveBaseName.set("librarianlib")
    from(generated)
    from(configurations["include"]) {
        into("META-INF/jars")
    }

    manifest {
        val manifest = Manifest()
        JarManifestConfiguration(rootProject).configure(manifest)
        // loom hard-codes `toM = "intermediary"` in the RemapJarTask
        manifest.mainAttributes.putValue("Fabric-Mapping-Namespace", "intermediary")
        attributes(manifest.mainAttributes.mapKeys { (key, _) -> "$key" })
    }
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveBaseName.set("librarianlib")
    archiveClassifier.set("sources")
    from(file("no_sources.txt"))
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveBaseName.set("librarianlib")
    archiveClassifier.set("javadoc")
    from(file("no_javadoc.txt"))
}

artifacts {
    add("publishedApi", jar)
    add("publishedRuntime", jar)
    add("publishedSources", sourcesJar)
    add("publishedJavadoc", javadocJar)
}

publishing.publications.named<MavenPublication>("maven") {
    artifactId = "librarianlib"
}
