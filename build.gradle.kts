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
        create("albedo")
    }
}
