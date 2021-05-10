@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty

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

    @Optional
    @Input
    val icon: Property<String> = ctx.property()

    @Optional
    @InputFile
    val iconFile: Property<File> = ctx.property()

    @Input
    val description: Property<String> = ctx.property() { "" }

    @Internal
    val entrypoints: ListProperty<Entrypoint> = ctx.listProperty() { listOf() }

    @Input
    val mixins: ListProperty<String> = ctx.listProperty() { listOf() }

    @Internal
    val dependencies: ListProperty<Dependency> = ctx.listProperty() { listOf() }

    @Nested
    val modMenu: ModMenuData = ModMenuData()

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

    @get:OutputFiles
    protected val outputFile: FileCollection
        get() {
            val root = outputRoot.get()
            val files = ctx.project.files()
            files.from(root.resolve("fabric.mod.json"))
            if(iconFile.orNull != null)
                icon.orNull?.also {
                    root.resolve(it)
                }
            return files
        }

    @TaskAction
    private fun runTask() {
        val root = outputRoot.get()
        root.mkdirs()
        root.resolve("fabric.mod.json").writeText(makeJson())
        val icon = this.icon.orNull
        val iconFile = this.iconFile.orNull
        if(icon != null && iconFile != null) {
            root.resolve(icon).parentFile.mkdirs()
            iconFile.copyTo(root.resolve(icon), overwrite = true)
        }
    }

    private fun makeJson(): String {
        val entrypoints = this.entrypoints.get().groupBy { it.type }
        /*
        |  "icon": {
        |${iconScales.joinToString(",\n") { "\"$it\": \"assets/${id.get()}/logo_$it.png\"" }.prependIndent("    ")}
        |  },
        */
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
        |  "icon": "${icon.getOrElse("")}",
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
        |  },
        |  "custom": {
        |${makeModMenuJson().prependIndent("    ")}
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
        return if (entrypoint.adapter == null) {
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

    private fun makeModMenuJson(): String {

        val links = modMenu.links.get()
        val linkBlock = if(links.isEmpty())
            null
        else
            """
            |"links": {
            |${links.entries.joinToString(",\n") { "\"${it.key}\": \"${it.value}\"" }.prependIndent("  ")}
            |}
            """.trimMargin()

        val badges = modMenu.badges.get()
        val badgeBlock = if (badges.isEmpty())
            null
        else
            """
            |"badges": [${badges.joinToString(", ") { "\"$it\"" }}]
            """.trimMargin()

        val parent = modMenu.parent.get()
        val fakeParent = modMenu.fakeParent
        val parentBlock = if(fakeParent.id.get() != "") {
            """
            |"parent": {
            |  "id": "${fakeParent.id.get()}",
            |  "name": "${fakeParent.name.get()}",
            |  "description": "${fakeParent.description.get()}",
            |  "icon": "${icon.getOrElse("")}",
            |  "badges": [${fakeParent.badges.get().joinToString(", ") { "\"$it\"" }}]
            |}
            """.trimMargin()
        } else if(parent != "") {
            "\"parent\": \"$parent\""
        } else {
            null
        }

        return """
        |"modmenu": {
        |${listOfNotNull(linkBlock, badgeBlock, parentBlock).joinToString(",\n").prependIndent("  ")}
        |}
        """.trimMargin()

    }

    data class Dependency(val id: String, val version: String)
    data class Entrypoint(val type: String, val adapter: String?, val value: String)

    inner class ModMenuData {
        @Input
        val links: MapProperty<String, String> = ctx.mapProperty() { mapOf() }

        @Input
        val badges: ListProperty<String> = ctx.listProperty() { listOf() }

        @Input
        val parent: Property<String> = ctx.property() { "" }

        @Nested
        val fakeParent: FakeParentData = FakeParentData()

        fun parent(id: String) {
            parent.set(id)
            fakeParent.id.set("")
        }

        fun parent(id: String, name: String, description: String, badges: List<String>) {
            parent.set("")
            fakeParent.id.set(id)
            fakeParent.name.set(name)
            fakeParent.description.set(description)
            fakeParent.badges.set(badges)
        }
    }

    inner class FakeParentData {
        @Input
        val id: Property<String> = ctx.property() { "" }

        @Input
        val name: Property<String> = ctx.property() { "" }

        @Input
        val description: Property<String> = ctx.property() { "" }

        @Input
        val badges: ListProperty<String> = ctx.listProperty() { listOf() }
    }

    companion object {
        val iconScales: List<Int> = listOf(64, 128, 256, 512)
    }
}