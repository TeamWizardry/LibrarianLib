@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import java.io.StringWriter
import java.util.*

open class CopyFreemarker : DefaultTask() {
    private val ctx = DslContext(project)

    @InputDirectory
    val template: Property<File> = ctx.property()

    @OutputDirectory
    val outputDirectory: Property<File> = ctx.property()

    @Input
    val model: MapProperty<String, Any> = ctx.mapProperty()

    fun model(build: HashModelBuilder.() -> Unit) {
        model.set(project.provider {
            val builder = HashModelBuilder()
            builder.build()
            builder.model
        })
    }

    private val cfg = Configuration(Configuration.VERSION_2_3_31).apply {
        defaultEncoding = "UTF-8"
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        logTemplateExceptions = false
        wrapUncheckedExceptions = true
        fallbackOnNullLoopVariable = false
    }

    @TaskAction
    private fun runTask() {
        val inputRoot = template.get()
        val outputRoot = outputDirectory.get()
        cfg.setDirectoryForTemplateLoading(inputRoot)

        val files = listFiles()
        val outputs = remapPaths(files)

        files.zip(outputs).forEach { (input, output) ->
            val realInput = inputRoot.resolve(input)
            val realOutput = outputRoot.resolve(output)

            if(realInput.isDirectory) {
                realOutput.mkdirs()
            } else {
                val template = cfg.getTemplate(input.toString())
                realOutput.parentFile.mkdirs()
                realOutput.writer().use { writer ->
                    template.process(model.get(), writer)
                }
            }
        }
    }

    private fun listFiles(): List<File> {
        val root = template.get()
        return project.fileTree(root).map { it.relativeTo(root) }
    }

    private fun remapPaths(files: List<File>): List<File> {
        val joined = files.joinToString("\uE000") // join using a private use area character
        val template = Template(unique(), joined, cfg)
        val writer = StringWriter()
        template.process(model.get(), writer)
        return writer.toString().split('\uE000').map { File(it) }
    }

    private fun unique(): String = UUID.randomUUID().toString()

    class HashModelBuilder() {
        val model: MutableMap<String, Any> = mutableMapOf()

        fun add(key: String, value: Any) {
            model[key] = value
        }

        fun hash(build: HashModelBuilder.() -> Unit): HashModelBuilder {
            val builder = HashModelBuilder()
            builder.build()
            return builder
        }

        fun list(build: ListModelBuilder.() -> Unit): ListModelBuilder {
            val builder = ListModelBuilder()
            builder.build()
            return builder
        }

        operator fun String.remAssign(value: String): Unit = add(this, value)
        operator fun String.remAssign(value: Number): Unit = add(this, value)
        operator fun String.remAssign(value: Boolean): Unit = add(this, value)
        operator fun String.remAssign(value: Date): Unit = add(this, value)
        operator fun String.remAssign(value: HashModelBuilder): Unit = add(this, value.model)
        operator fun String.remAssign(value: ListModelBuilder): Unit = add(this, value.model)
    }

    class ListModelBuilder() {
        val model: MutableList<Any> = mutableListOf()

        fun add(value: Any) {
            model.add(value)
        }

        fun hash(build: HashModelBuilder.() -> Unit): HashModelBuilder {
            val builder = HashModelBuilder()
            builder.build()
            return builder
        }

        fun list(build: ListModelBuilder.() -> Unit): ListModelBuilder {
            val builder = ListModelBuilder()
            builder.build()
            return builder
        }

        operator fun String.unaryPlus(): Unit = add(this)
        operator fun Number.unaryPlus(): Unit = add(this)
        operator fun Boolean.unaryPlus(): Unit = add(this)
        operator fun Date.unaryPlus(): Unit = add(this)
        operator fun HashModelBuilder.unaryPlus(): Unit = add(this.model)
        operator fun ListModelBuilder.unaryPlus(): Unit = add(this.model)
    }
}
