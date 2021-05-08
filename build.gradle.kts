import java.util.*

plugins {
    `minecraft-conventions`
}

apply<CommonConfigPlugin>()

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://www.cursemaven.com") {
            content { includeGroup("curse.maven") }
        }
        maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "kotlinforforge" }
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://dvs1.progwml6.com/files/maven/") { name = "Progwml6 maven (JEI)" }
        maven("https://modmaven.k-4u.nl") { name = "ModMaven (JEI mirror)" }

        maven("https://raw.githubusercontent.com/Devan-Kerman/Devan-Repo/master/") // for ARRP
        maven("https://maven.terraformersmc.com/releases") // for ModMenu
    }
}

configure<CommonConfigExtension> {
    val snapshotVersion = System.getenv("SNAPSHOT_REF")?.let { ref ->
        if(!ref.startsWith("refs/heads/"))
            throw IllegalStateException("SNAPSHOT_REF `$ref` doesn't start with refs/heads/")
        val branch = ref.removePrefix("refs/heads/")
        branch.replace("[^.\\w-]".toRegex(), "-") + "-SNAPSHOT"
    }
    val mod_version: String by project
    version = snapshotVersion ?: mod_version

    modules {
//        create("albedo")
        create("core")
//        create("courier")
        create("etcetera")
//        create("facade")
//        create("foundation")
//        create("glitter")
//        create("lieutenant")
//        create("mirage")
        create("mosaic")
//        create("scribe")
    }

}

open class CreateModule: CopyFreemarker() {
    @Option(option = "name", description = "The name of the module in Title Case. e.g. 'Cool Thing'. " +
            "The PascalCase and lowercase names will be inferred from this")
    var humanName: Property<String> = project.objects.property()
}

// use `./gradlew createModule --name=Whatever`
tasks.register<CreateModule>("createModule") {
    template.set(project.file("modules/_template"))
    outputDirectory.set(project.file("modules"))
    model {
        "humanName" %= humanName.get()
        "PascalName" %= humanName.get().replace(" ", "")
        "lowername" %= humanName.get().replace(" ", "").toLowerCase(Locale.ROOT)
    }
    doLast {
        val lowerName = humanName.get().replace(" ", "").toLowerCase(Locale.ROOT)
        logger.warn("############################################################################")
        logger.warn("# Some manual actions are still required when adding a module!             #")
        logger.warn("# - Set the maven_description in the new module's gradle.properties        #")
        logger.warn("# - Add `includeModule(\"$lowerName\")` to the settings.gradle.kts file    #")
        logger.warn("# - Add `create(\"$lowerName\")` to the root build.gradle.kts commonConfig #")
        logger.warn("# - Add an item describing the module in the root README.md file           #")
        logger.warn("############################################################################")
    }
}

tasks.register<ReplaceTextInPlace>("updateReadmeVersions") {

    fun formatBadge(id: String, label: String, message: String, color: String, alt: String): String {
        val cleanLabel = label.replace("_", "__").replace("-", "--").replace(" ", "_")
        val cleanMessage = message.replace("_", "__").replace("-", "--").replace(" ", "_")
        return """<img id="$id" src="https://img.shields.io/badge/$cleanLabel-$cleanMessage-$color" alt="$alt"/>"""
    }

//    val mc_version: String by project
//    val forge_version: String by project
//    val mc_mappings_channel: String by project
//    val mc_mappings_version: String by project
//    val mcpVersion = "${mc_mappings_channel}_${mc_mappings_version}"
//
//    replaceIn("README.md") {
//        add("""<img id="([^"]*-badge)".*?/>""".toRegex()) { _, match ->
//            when (match.group(1)) {
//                "mc-version-badge" -> formatBadge(
//                    id = "mc-version-badge",
//                    label = "Minecraft",
//                    message = mc_version,
//                    color = "blue",
//                    alt = "Minecraft $mc_version"
//                )
//                "forge-version-badge" ->
//                    formatBadge(
//                        id = "forge-version-badge",
//                        label = "Forge",
//                        message = forge_version,
//                        color = "blue",
//                        alt = "Minecraft Forge $forge_version"
//                    )
//                "mcp-mappings-badge" ->
//                    formatBadge(
//                        id = "mcp-mappings-badge",
//                        label = "MCP",
//                        message = mcpVersion,
//                        color = "blue",
//                        alt = "MCP $mcpVersion"
//                    )
//                else -> match.group()
//            }
//        }
//    }
}
