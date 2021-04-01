import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.UserDevExtension

plugins {
    `minecraft-conventions`
}
apply<LibLibModulePlugin>()


//configurations {
//    mod
//    clientMod.extendsFrom(mod)
//    serverMod.extendsFrom(mod)
//    dataMod.extendsFrom(mod)
//}

val kotlinforforge_version: String by project

dependencies {
    // exclude Minecraft's default ICU4J so facade's version can override it. At runtime LibLib's will be relocated.
    configurations.getByName("minecraft").exclude("com.ibm.icu", "icu4j-core-mojang")

    implementation("thedarkcolour:kotlinforforge:$kotlinforforge_version")
}

val liblib: LibLibModuleExtension = the()

configure<UserDevExtension> {
    val commonConfig: RunConfig.() -> Unit = {
        // ForgeGradle would generate `runtime_main`, which is wrong on two counts. Firstly it needs to be `.main` and
        // not `_main` (which is a _known issue_ https://github.com/MinecraftForge/ForgeGradle/issues/425), and secondly
        // it doesn"t take into account the fact that this project may not be the root project.
        // https://github.com/MinecraftForge/ForgeGradle/blob/bd92a0d384b987be361ed3f7df28b1980f7fae1e/src/common/java/net/minecraftforge/gradle/common/util/RunConfig.java#L240
        ideaModule("librarianlib.runtime.main")
        singleInstance(true)
        taskName(name)

        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")

        mods {
            liblib.root.modules.forEach { module ->
                // todo: ForgeGradle needs the source set references directly. Lazy evaluation is stupid, amirite?
                create(module.modid.get()) {
                    sources(module.project.get().sourceSets.main.get())
                }
//                create(module.testModid.get()) {
//                    sources(module.project.get().sourceSets.test.get())
//                }
            }
        }
    }

    // todo: ForgeGradle doesn't generate run configs properly unless I eagerly configure these using `create`
    runs.create("client") {
        workingDirectory(project.file("run/client"))
        this.commonConfig()
    }
    runs.create("server") {
        workingDirectory(project.file("run/server"))
        this.commonConfig()
    }
    runs.create("data") {
        workingDirectory(project.file("run/data"))
        this.commonConfig()
        jvmArg("-XstartOnFirstThread")
        val args = mutableListOf<Any?>(
            "--all",
            "--output",
            project.file("run/datagen"),
            "--mod", liblib.root.modules.joinToString(",") { "${it.modid},${it.testModid}" }
        )
        liblib.root.modules.forEach { module ->
            args.add("--existing")
            args.add(module.project.get().sourceSets.main.get().output.resourcesDir)
            args.add("--existing")
            args.add(module.project.get().sourceSets.test.get().output.resourcesDir)
        }

        args()
    }
}

// todo: ForgeGradle doesn't set up build dependencies, so we have to manually wire them up
tasks.named("classes") {
    dependsOn(liblib.root.modules.map { it.project.get().tasks.named("classes") })
    dependsOn(liblib.root.modules.map { it.project.get().tasks.named("testClasses") })
}
tasks.named("processResources") {
    dependsOn(liblib.root.modules.map { it.project.get().tasks.named("processResources") })
    dependsOn(liblib.root.modules.map { it.project.get().tasks.named("processTestResources") })
}
