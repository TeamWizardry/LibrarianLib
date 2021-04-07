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
import java.lang.IllegalStateException

/**
 * Logs a warning if the mixin plugin is applied but no mixin configs were found, or throws an error if mixin configs
 * were found and the mixin plugin is not applied.
 */
open class ValidateMixinApplication : DefaultTask() {
    private val ctx = DslContext(project)

    @Internal
    val mixinRoots: ConfigurableFileCollection = project.files()

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

    @get:Input
    protected val mixinPluginApplied: Property<Boolean> = ctx.property() {
        project.plugins.hasPlugin("org.spongepowered.mixin")
    }

    @get:InputFiles
    protected val mixinFiles: FileCollection = mixinRoots.asFileTree.matching {
        includes.add("**/*.mixins.json")
    }

    @TaskAction
    private fun runTask() {
        val hasMixins = !mixinFiles.isEmpty

        if(!hasMixins && mixinPluginApplied.get()) {
            logger.warn("Project ${project.name} has no mixin configs, yet the mixin plugin was applied. This will slow down the build.")
        } else if(hasMixins && !mixinPluginApplied.get()) {
            throw IllegalStateException("Project ${project.name} has mixin configs, yet the mixin plugin was not applied. Mixins will not function.")
        }
    }
}