@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import com.github.jengelman.gradle.plugins.shadow.ShadowStats
import com.github.jengelman.gradle.plugins.shadow.relocation.RelocatePathContext
import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Jar
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import org.jetbrains.kotlin.gradle.utils.`is`

/**
 * Creates a fat directory with relocated and shaded dependencies.
 *
 * This is done separately from the jar creation, since `includeEmptyDirs` doesn't apply to outputs, only inputs. That
 * means that after relocating files the old directory structure is left behind. To fix that  means we have to output
 * the relocated sources, then we package though into a jar, ignoring the empty directories.
 */
open class ShadowSources : Copy() {
    private val ctx = DslContext(project)

    @Internal
    val relocators: ListProperty<Relocator> = ctx.listProperty() { emptyList() }

    /**
     * Add the sources from the given configuration. Note that this does not resolve transitive dependencies.
     */
    fun sourcesFrom(configuration: Configuration) {
        this.from({ collectSources(configuration) })
    }

    private fun collectSources(configuration: Configuration): FileCollection {
        val components = configuration
            .incoming.resolutionResult
            .root.dependencies
            .filterIsInstance<ResolvedDependencyResult>()
            .map { it.selected.id }

        val result = project.dependencies.createArtifactResolutionQuery()
            .forComponents(components)
            .withArtifacts(JvmLibrary::class.java, SourcesArtifact::class.java)
            .execute()

        return project.files(
            result.resolvedComponents
                .flatMap { it.getArtifacts(SourcesArtifact::class.java) }
                .filterIsInstance<ResolvedArtifactResult>()
                .map {
                    if(it.file.isDirectory) {
                        project.fileTree(it.file)
                    } else {
                        project.zipTree(it.file)
                    }
                }
        )
    }

    private val shadowStats = ShadowStats()

    init {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        @Suppress("LeakingThis")
        eachFile {
            relocateFile(this)
        }
    }

    private fun relocateFile(copyDetails: FileCopyDetails) {
        if(copyDetails.isDirectory) {
            // exclude directories. Any that are actually needed by the output files will be automatically created,
            // but if we don't do this relocated sources will leave behind their empty directory structure
            copyDetails.exclude()
            return
        }
        relocators.get().forEach { relocator ->
            val pathContext = RelocatePathContext(copyDetails.sourcePath, shadowStats)
            if(relocator.canRelocatePath(pathContext)) {
                copyDetails.path = relocator.relocatePath(pathContext)
            }

            copyDetails.filter { line ->
                relocator.applyToSourceContent(line)
            }
        }
    }
}