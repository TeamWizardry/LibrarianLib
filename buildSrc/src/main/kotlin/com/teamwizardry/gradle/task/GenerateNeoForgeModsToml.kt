@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty

open class GenerateNeoForgeModsToml : DefaultTask() {
    private val ctx = DslContext(project)

    /**
     * The root resources directory (e.g. `$buildDir/generated/main/resources`)
     */
    @Input val outputRoot: Property<File> = ctx.property()

    @Input val modLoader: Property<String> = ctx.property { "javafml" }
    @Input val loaderVersion: Property<String> = ctx.property { "2" }
    @Input val license: Property<String> = ctx.property()
    @Optional @Input val issueTrackerURL: Property<String> = ctx.property()
    @Nested val mods: ListProperty<ModInfo> = ctx.listProperty { listOf() }
    @Input val mixins: ListProperty<String> = ctx.listProperty { listOf() }
    @Input val accessTransformers: ListProperty<String> = ctx.listProperty { listOf() }

    fun mod(block: ModInfo.() -> Unit) {
        mods.add(ModInfo().apply(block))
    }

    fun mixin(path: String) {
        mixins.add(path)
    }

    fun accessTransformer(path: String) {
        accessTransformers.add(path)
    }

    inner class ModInfo {
        @Input val modId: Property<String> = ctx.property()
        @Input val version: Property<String> = ctx.property()
        @Input val displayName: Property<String> = ctx.property()
        @Input val description: Property<String> = ctx.property()
        @Optional @Input val updateJSONURL: Property<String> = ctx.property()
        @Optional @Input val displayURL: Property<String> = ctx.property()
        @Optional @Input val logoFileName: Property<String> = ctx.property { "${modId.get()}-logo.png" }
        @Optional @InputFile val logoFile: Property<File> = ctx.property()
        @Optional @Input val credits: Property<String> = ctx.property()
        @Optional @Input val authors: Property<String> = ctx.property()
        @Nested val dependencies: ListProperty<Dependency> = ctx.listProperty { listOf() }

        fun dependency(
            modId: String,
            versionRange: String,
            type: DependencyType = DependencyType.REQUIRED,
            block: Dependency.() -> Unit = {}
        ) {
            dependencies.add(Dependency().also {
                it.modId.set(modId)
                it.type.set(type)
                it.versionRange.set(versionRange)
            }.apply(block))
        }

        inner class Dependency {
            @Input val modId: Property<String> = ctx.property()
            @Input val type: Property<DependencyType> = ctx.property()
            @Input val versionRange: Property<String> = ctx.property()
            @Optional @Input val ordering: Property<DependencyOrdering> = ctx.property(DependencyOrdering.NONE)
            @Optional @Input val side: Property<DependencySide> = ctx.property(DependencySide.BOTH)
        }
    }

    enum class DependencyType(val tomlValue: String) {
        REQUIRED("required"),
        OPTIONAL("optional"),
        INCOMPATIBLE("incompatible"),
        DISCOURAGED("discouraged"),
    }

    enum class DependencyOrdering(val tomlValue: String) {
        BEFORE("BEFORE"),
        NONE("NONE"),
        AFTER("AFTER"),
    }


    enum class DependencySide(val tomlValue: String) {
        BOTH("BOTH"),
        CLIENT("CLIENT"),
        SERVER("SERVER"),
    }

    @get:OutputFiles
    protected val outputFile: FileCollection
        get() {
            val root = outputRoot.get()
            val files = ctx.project.files()
            files.from(root.resolve("META-INF/neoforge.mods.toml"))
            for (mod in mods.get()) {
                if (mod.logoFile.isPresent) {
                    files.from(root.resolve(mod.logoFileName.get()))
                }
            }
            return files
        }

    @TaskAction
    fun runTask() {
        val root = outputRoot.get()
        root.resolve("META-INF").mkdirs()
        root.resolve("META-INF/neoforge.mods.toml").writeText(makeToml())
        for (mod in mods.get()) {
            mod.logoFile.orNull?.also { logo ->
                val dest = root.resolve(mod.logoFileName.get())
                dest.parentFile.mkdirs()
                logo.copyTo(dest, overwrite = true)
            }
        }
    }

    private fun makeToml(): String = buildString {
        tomlValue("modLoader", modLoader.get())
        tomlValue("loaderVersion", loaderVersion.get())
        tomlValue("license", license.get())
        tomlValue("issueTrackerURL", issueTrackerURL.orNull)

        for (mod in mods.get()) {
            val modId = mod.modId.get()
            appendLine("[[mods]]")
            tomlValue("modId", modId)
            tomlValue("version", mod.version.get())
            tomlValue("displayName", mod.displayName.get())
            tomlValue("description", mod.description.get())
            tomlValue("updateJSONURL", mod.updateJSONURL.orNull)
            tomlValue("displayURL", mod.displayURL.orNull)
            if (mod.logoFile.isPresent) tomlValue("logoFile", mod.logoFileName.get())
            tomlValue("credits", mod.credits.orNull)
            tomlValue("authors", mod.authors.orNull)

            for (dependency in mod.dependencies.get()) {
                appendLine("[[dependencies.${modId}]]")
                tomlValue("modId", dependency.modId.get())
                tomlValue("type", dependency.type.get().tomlValue)
                tomlValue("versionRange", dependency.versionRange.get())
                tomlValue("ordering", dependency.ordering.get().tomlValue)
                tomlValue("side", dependency.side.get().tomlValue)
            }
        }

        for (mixin in mixins.get()) {
            appendLine("[[mixins]]")
            tomlValue("config", mixin)
        }

        for (accessTransformer in accessTransformers.get()) {
            appendLine("[[accessTransformers]]")
            tomlValue("file", accessTransformer)
        }
    }

    private fun StringBuilder.tomlValue(key: String, value: String?) {
        if (value == null) return
        if (value.contains("\n")) {
            appendLine("$key=\"\"\"$value\"\"\"")
        } else {
            appendLine("$key=\"${value.replace("\"", "\\\"")}\"")
        }
    }
}
