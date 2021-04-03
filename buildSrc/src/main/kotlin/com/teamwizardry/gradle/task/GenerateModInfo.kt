@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Provider

/**
 * Generates a mods.toml and pack.mcmeta
 */
open class GenerateModInfo : DefaultTask() {
    private val ctx = DslContext(project)

    @Input
    val modid: Property<String> = ctx.property()

    /**
     * The root sources directory (e.g. `$buildDir/generated/main/resources`)
     */
    @Input
    val outputRoot: Property<File> = ctx.property()

    private val outputModsInfoFile: File
        get() = outputRoot.get().resolve("META-INF/mods.toml")
    private val outputPackMcmeta: File
        get() = outputRoot.get().resolve("pack.mcmeta")

    @get:OutputFiles
    protected val outputFiles: FileCollection
        get() = project.files(outputModsInfoFile, outputPackMcmeta)

    @TaskAction
    private fun runTask() {
        outputModsInfoFile.parentFile.mkdirs()
        outputModsInfoFile.writeText(generateModsToml(modid.get()))
        outputPackMcmeta.writeText(generatePackMcmeta(modid.get()))
    }

    private fun generateModsToml(modid: String): String {
        //language=TOML
        return """
            |modLoader="kotlinforforge"
            |loaderVersion="[1,)"
            |license="LGPL-3.0"
            |[[mods]]
            |modId="$modid"
            |version="0.0.0"
            |displayName="$modid"
            |description=""
            |[[dependencies.$modid]]
            |    modId="librarianlib"
            |    versionRange="(0.0.0,]"
            |    mandatory=true
            |    ordering="BEFORE"
        """.trimMargin()
    }

    private fun generatePackMcmeta(modid: String): String {
        //language=JSON
        return """
            |{
            |    "pack": {
            |        "description": "$modid resources",
            |        "pack_format": 4
            |    }
            |}
        """.trimMargin()
    }
}