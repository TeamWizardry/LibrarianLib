@file:Suppress("PublicApiImplicitType")

import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.UserDevExtension

plugins {
    `minecraft-conventions`
}

//configurations {
//    mod
//    clientMod.extendsFrom(mod)
//    serverMod.extendsFrom(mod)
//    dataMod.extendsFrom(mod)
//}

val kotlinforforge_version: String by project

val commonConfig = rootProject.the<CommonConfigExtension>()
val liblibModules = commonConfig.modules

dependencies.attributesSchema {
    attribute(LibLibAttributes.Target.attribute) {
        compatibilityRules.add(LibLibAttributes.Rules.optional())
    }
}

configurations {
    runtimeClasspath {
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.devClasspath)
    }
    testRuntimeClasspath {
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.devClasspath)
    }
}

dependencies {
    // exclude Minecraft's default ICU4J so facade's version can override it. At runtime LibLib's will be relocated.
    configurations.getByName("minecraft").exclude("com.ibm.icu", "icu4j-core-mojang")

    implementation("thedarkcolour:kotlinforforge:$kotlinforforge_version")

    runtimeOnly(project(":testcore"))
    liblibModules.forEach { module ->
        runtimeOnly(module.project)
    }
}


configure<UserDevExtension> {
    val commonConfig: RunConfig.(configName: String) -> Unit = { configName ->
        // ForgeGradle would generate `runtime_main`, which is wrong on two counts. Firstly it needs to be `.main` and
        // not `_main` (which is a _known issue_ https://github.com/MinecraftForge/ForgeGradle/issues/425), and secondly
        // it doesn"t take into account the fact that this project may not be the root project.
        // https://github.com/MinecraftForge/ForgeGradle/blob/bd92a0d384b987be361ed3f7df28b1980f7fae1e/src/common/java/net/minecraftforge/gradle/common/util/RunConfig.java#L240
        ideaModule("librarianlib.zzz.runtime.main")
        singleInstance(true)
        taskName(configName)

        property("forge.logging.markers", "")
        property("forge.logging.console.level", "debug")

        mods {
            create("librarianlib") {
                sources(project(":zzz:librarianlib").sourceSets.main.get())
                liblibModules.forEach { sources(it.mainSources.get()) }
            }
            create("testcore") {
                sources(project(":testcore").sourceSets.main.get())
            }
            create("testcore-test") {
                sources(project(":testcore").sourceSets.test.get())
            }
            liblibModules.forEach { module ->
                create(module.testModid) {
                    sources(module.testSources.get())
                }
            }
        }
    }

    // todo: ForgeGradle doesn't generate run configs properly unless I eagerly configure these using `create`
    runs.create("client") {
        workingDirectory(project.file("run/client"))
        this.commonConfig("client")
    }
    runs.create("server") {
        workingDirectory(project.file("run/server"))
        this.commonConfig("server")
    }
    runs.create("data") {
        workingDirectory(project.file("run/data"))
        this.commonConfig("data")
        jvmArg("-XstartOnFirstThread")
        val args = mutableListOf<Any?>(
            "--all",
            "--output",
            project.file("run/datagen"),
            "--mod", liblibModules.joinToString(",") { it.testModid }
        )
        liblibModules.forEach { module ->
            args.add("--existing")
            args.add(module.mainSources.get().output.resourcesDir)
            args.add("--existing")
            args.add(module.testSources.get().output.resourcesDir)
        }

        args(args)
    }
}

tasks.named("classes") {
    dependsOn(project(":zzz:librarianlib").tasks.named("classes"))
    dependsOn(project(":testcore").tasks.named("classes"))
    dependsOn(project(":testcore").tasks.named("testClasses"))
    dependsOn(liblibModules.map { it.project.tasks.named("classes") })
    dependsOn(liblibModules.map { it.project.tasks.named("testClasses") })
}
tasks.named("processResources") {
    dependsOn(project(":zzz:librarianlib").tasks.named("processResources"))
    dependsOn(project(":testcore").tasks.named("processResources"))
    dependsOn(project(":testcore").tasks.named("processTestResources"))
    dependsOn(liblibModules.map { it.project.tasks.named("processResources") })
    dependsOn(liblibModules.map { it.project.tasks.named("processTestResources") })
    dependsOn(":updateReadmeVersions")
}
