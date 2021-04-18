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
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import java.io.StringWriter
import java.util.*

open class RestyleDokka : DefaultTask() {
    private val ctx = DslContext(project)

    @Internal
    val dokkaTask: Property<AbstractDokkaTask> = ctx.property()

    @OutputDirectory
    val outputDir: Property<File> = ctx.property()

    @get:InputDirectory
    internal val inputDirectory: File
        get() = dokkaTask.get().outputDirectory.get()

    init {
        @Suppress("LeakingThis")
        dependsOn(dokkaTask)
    }

//    private val freemarker = Configuration(Configuration.VERSION_2_3_31).apply {
//        defaultEncoding = "UTF-8"
//        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
//        logTemplateExceptions = false
//        wrapUncheckedExceptions = true
//        fallbackOnNullLoopVariable = false
//        setClassForTemplateLoading(MergeDokkaModules::class.java, "com.teamwizardry.gradle.dokka.templates")
//    }

    @TaskAction
    private fun runTask() {
        val outputRoot = outputDir.get()
        outputRoot.deleteRecursively()

        project.copy {
            from(inputDirectory)
            into(outputRoot)
        }

        processFile("styles/style.css") {
            readResource("style.css")
        }
    }

    /**
     * Read a resource in the `com.teamwizardry.gradle.task.dokka` package.
     */
    private fun readResource(name: String): String {
        return RestyleDokka::class.java.getResourceAsStream("dokka/$name")?.use {
            it.bufferedReader().readText()
        } ?: throw IllegalArgumentException("Resource `$name` doesn't exist")
    }

    /**
     * Apply some processing to the text contents of each file in the given collection.
     */
    private fun processFiles(files: FileCollection, operation: (File, String) -> String) {
        files.forEach { file ->
            if(file.isFile) {
                file.writeText(operation(file, file.readText()))
            }
        }
    }

    /**
     * Apply some processing to the text contents of the given file.
     */
    private fun processFile(file: File, operation: (String) -> String) {
        file.writeText(operation(file.readText()))
    }

    /**
     * Process files matching any of the given patterns under the output root.
     *
     * The patterns are for the full file paths, so use `**` to match files in any directory.
     */
    private fun processFiles(vararg patterns: String, operation: (File, String) -> String) {
        processFiles(project.fileTree(outputDir.get()) { include(*patterns) }, operation)
    }

    /**
     * Process the file with the given path under the output root.
     */
    private fun processFile(path: String, operation: (String) -> String) {
        processFile(outputDir.get().resolve(path), operation)
    }

}
