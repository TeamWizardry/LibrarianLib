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
 * Generates a `MixinConnector.java` file based on a set of mixins
 *
 * Note: this does not generate the required `MANIFEST.MF` entry. You will have to provide that yourself. The entry
 * should look like this:
 *
 * ```
 * MixinConnector: gen.whatever.MixinConnector
 * ```
 */
open class GenerateMixinConnector : DefaultTask() {
    private val ctx = DslContext(project)

    @Internal
    val mixinRoots: ConfigurableFileCollection = project.files()

    @Input
    val mixinName: Property<String> = ctx.property()

    /**
     * The root sources directory (e.g. `$buildDir/generated/main/java`)
     */
    @Input
    val outputRoot: Property<File> = ctx.property()

    /**
     * Scans the given source set's resources for mixins
     */
    fun from(sourceSet: SourceSet) {
        mixinRoots.from(sourceSet.resources.sourceDirectories)
    }

    /**
     * Scans the given source set's resources for mixins
     */
    fun from(sourceSet: Provider<SourceSet>) {
        mixinRoots.from(sourceSet.map { it.resources.sourceDirectories })
    }

    @get:InputFiles
    protected val mixinFiles: FileCollection = mixinRoots.asFileTree.matching {
        includes.add("**/*.mixins.json")
    }

    @get:OutputFile
    protected val outputFile: File
        get() = outputRoot.get().resolve(mixinName.get().replace('.', '/') + ".java")


    @TaskAction
    private fun runTask() {
        val packageName = mixinName.get().substringBeforeLast(".")
        val className = mixinName.get().substringAfterLast(".")
        val mixinConfigs = mixinRoots.flatMap { collectMixinConfigs(it) }.sorted()
        val classText = generateMixinConnector(packageName, className, mixinConfigs)
        val output = outputFile
        output.parentFile.mkdirs()
        output.writeText(classText)
    }

    private fun collectMixinConfigs(root: File): List<String> {
        return project.fileTree(root) {
            includes.add("**/*.mixins.json")
        }.map { file ->
            file.relativeTo(root).invariantSeparatorsPath
        }
    }

    private fun generateMixinConnector(packageName: String, className: String, mixinConfigs: List<String>): String {
        //language=JAVA
        return """
           |package $packageName;
           |
           |import org.spongepowered.asm.mixin.Mixins;
           |import org.spongepowered.asm.mixin.connect.IMixinConnector;
           |
           |public class $className implements IMixinConnector {
           |    @Override
           |    public void connect() {
           |        ${mixinConfigs.joinToString(" ") { "Mixins.addConfiguration(\"$it\");" }}
           |    }
           |}
        """.trimMargin()
    }
}