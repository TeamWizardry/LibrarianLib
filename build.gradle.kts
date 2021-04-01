apply<LibLibRootPlugin>()

configure<LibLibRootExtension> {
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

    modules {
        create("core") {
        }
    }
}

/*
apply<LibLibRootPlugin>()

liblib {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://www.cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }
        maven {
            name = "kotlinforforge"
            url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        }
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
        maven {
            // location of the maven that hosts JEI files
            name = "Progwml6 maven"
            url = uri("https://dvs1.progwml6.com/files/maven/")
        }
        maven {
            // location of a maven mirror for JEI files, as a fallback
            name = "ModMaven"
            url = uri("https://modmaven.k-4u.nl")
        }
    }
    modules {
        core {
            project = project(":core")
            modid = "ll-core"
            testModid = "ll-core-test"
        }
    }
}
 */