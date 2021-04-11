import java.util.*

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
    }
}

configure<CommonConfigExtension> {
    val mod_version: String by project
    version = mod_version
    modules {
        create("albedo")
        create("core")
        create("courier")
        create("etcetera")
        create("facade")
        create("foundation")
        create("glitter")
        create("lieutenant")
        create("mirage")
        create("mosaic")
        create("scribe")
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
        logger.warn("# - Add `includeModule(\"$lowerName\")` to the settings.gradle.kts file    #")
        logger.warn("# - Add `create(\"$lowerName\")` to the root build.gradle.kts commonConfig #")
        logger.warn("# - Add an item describing the module in the root README.md file           #")
        logger.warn("############################################################################")
    }
}
