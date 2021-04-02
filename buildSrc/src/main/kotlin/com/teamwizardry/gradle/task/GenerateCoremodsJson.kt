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
 * Generates a `coremods.json` file based on a set of mixins. If there are no coremods this won't generate a
 * `coremods.json` file.
 *
 * This task treats any file in the form `*.asm.js` as a coremod file. Coremods will be named in the resulting json
 * file according to their path relative to the root they were found in, trimming off any leading `META-INF`. e.g.
 * `META-INF/ll/core/wow.asm.js` will be called `"ll.core.wow"`
 */
open class GenerateCoremodsJson : DefaultTask() {
    private val ctx = DslContext(project)

    @Internal
    val coremodRoots: ConfigurableFileCollection = project.files()

    /**
     * The root resources directory (e.g. `$buildDir/generated/main/resources`)
     */
    @Input
    val outputRoot: Property<File> = ctx.property()

    /**
     * Scans the given source set's resources for mixins
     */
    fun from(sourceSet: SourceSet) {
        coremodRoots.from(sourceSet.resources.sourceDirectories)
    }

    /**
     * Scans the given source set's resources for mixins
     */
    fun from(sourceSet: Provider<SourceSet>) {
        coremodRoots.from(sourceSet.map { it.resources.sourceDirectories })
    }

    @get:InputFiles
    protected val coremodFiles: FileCollection = coremodRoots.asFileTree.matching {
        includes.add("**/*.asm.js")
    }

    @get:OutputFile
    protected val outputFile: File
        get() = outputRoot.get().resolve("META-INF/coremods.json")

    @TaskAction
    private fun runTask() {
        val coremods = coremodRoots.flatMap { collectCoremods(it).entries }
            .sortedBy { it.key }.associate { it.key to it.value }
        val output = outputFile
        if(coremods.isEmpty()) {
            if(output.exists())
                output.delete()
        } else {
            val fileText = generateCoremodsJson(coremods)
            output.parentFile.mkdirs()
            output.writeText(fileText)
        }
    }

    private fun collectCoremods(root: File): Map<String, String> {
        return project.fileTree(root) {
            includes.add("**/*.asm.js")
        }.associate { file ->
            val relative = file.relativeTo(root).invariantSeparatorsPath
            val name = relative.removeSuffix(".asm.js").removePrefix("META-INF/").replace('/', '.')
            name to relative
        }
    }

    private fun generateCoremodsJson(coremods: Map<String, String>): String {
        return "{\n" + coremods.entries.joinToString(",\n") { "    \"${it.key}\": \"${it.value}\"" } + "\n}"
    }
}