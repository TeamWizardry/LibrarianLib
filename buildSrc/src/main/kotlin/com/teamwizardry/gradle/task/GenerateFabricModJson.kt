@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.provider.ListProperty

open class GenerateFabricModJson : DefaultTask() {
    private val ctx = DslContext(project)

    /**
     * The root resources directory (e.g. `$buildDir/generated/main/resources`)
     */
    @Input
    val outputRoot: Property<File> = ctx.property()

    @Input
    val id: Property<String> = ctx.property()

    @Input
    val version: Property<String> = ctx.property()

    @Input
    val name: Property<String> = ctx.property() { project.displayName }

    @Input
    val description: Property<String> = ctx.property() { "" }

    @Internal
    val entrypoints: ListProperty<Entrypoint> = ctx.listProperty() { listOf() }

    @Input
    val mixins: ListProperty<String> = ctx.listProperty() { listOf() }

    @Internal
    val dependencies: ListProperty<Dependency> = ctx.listProperty() { listOf() }

    fun entrypoint(type: String, adapter: String? = null, value: String) {
        entrypoints.add(Entrypoint(type, adapter, value))
    }

    fun mixin(path: String) {
        mixins.add(path)
    }

    fun depends(id: String, version: String) {
        dependencies.add(Dependency(id, version))
    }

    @get:Input
    protected val inputEntrypoints: List<String>
        get() = entrypoints.get().map { "${it.type}\uE000${it.adapter}\uE000${it.value}" }
    @get:Input
    protected val inputDependencies: List<String>
        get() = dependencies.get().map { "${it.id}\uE000${it.version}" }
    @get:OutputFile
    protected val outputFile: File
        get() = outputRoot.get().resolve("fabric.mod.json")

    @TaskAction
    private fun runTask() {
        outputFile.writeText(makeJson())
    }

    private fun makeJson(): String {
        val entrypoints = this.entrypoints.get().groupBy { it.type }
        return """
        |{
        |  "schemaVersion": 1,
        |  "id": "${id.get()}",
        |  "version": "${version.get()}",
        |
        |  "name": "${name.get()}",
        |  "description": "${description.get()}",
        |  "authors": [
        |    "thecodewarrior"
        |  ],
        |  "contact": {
        |    "homepage": "https://github.com/TeamWizardry/LibrarianLib",
        |    "sources": "https://github.com/TeamWizardry/LibrarianLib"
        |  },
        |
        |  "license": "LGPL-3.0",
        |  "icon": "assets/modid/icon.png",
        |
        |  "environment": "*",
        |  "entrypoints": {
        |${entrypoints.entries.joinToString(",\n") { makeEntrypointTypeJson(it.key, it.value) }.prependIndent("    ")}
        |  },
        |  "mixins": [
        |${mixins.get().joinToString(",\n") { "\"$it\"" }.prependIndent("    ")}
        |  ],
        |  "depends": {
        |${dependencies.get().joinToString(",\n") { "\"${it.id}\": \"${it.version}\"" }.prependIndent("    ")}
        |  }
        |}
        """.trimMargin()
    }

    private fun makeEntrypointTypeJson(type: String, entrypoints: List<Entrypoint>): String {
        return """
        |"$type": [
        |${entrypoints.joinToString(",\n") { makeEntrypointJson(it) }.prependIndent("  ")}
        |]
        """.trimMargin()
    }

    private fun makeEntrypointJson(entrypoint: Entrypoint): String {
        return if(entrypoint.adapter == null) {
            "\"${entrypoint.value}\""
        } else {
            """
            |{
            |  "adapter": "${entrypoint.adapter}",
            |  "value": "${entrypoint.value}"
            |}
            """.trimMargin()
        }
    }

    data class Dependency(val id: String, val version: String)
    data class Entrypoint(val type: String, val adapter: String?, val value: String)
}