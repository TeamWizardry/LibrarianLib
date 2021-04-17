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

open class MergeDokkaModules : SourceTask() {
    private val ctx = DslContext(project)

    @OutputDirectory
    val outputDir: Property<File> = ctx.property()

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
            from(source)
            into(outputRoot)
        }

        fixLinks(outputRoot)
    }

    private fun fixLinks(root: File) {
        project.fileTree(root).forEach { file ->
            if(file.isFile && file.name.endsWith(".html")) {
                val relativeRoot = root.relativeTo(file.parentFile)
                var text = file.readText()
                text = text.replace("https://placeholder.docs", relativeRoot.invariantSeparatorsPath)
                file.writeText(text)
            }
        }
    }
}
