@file:Suppress("UnstableApiUsage")

package com.teamwizardry.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import java.io.File

import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.util.Replacer
import java.util.regex.MatchResult

open class ReplaceTextInPlace : DefaultTask() {
    private val ctx = DslContext(project)

    private val specs = mutableListOf<ReplaceSpec>()

    fun replaceIn(vararg files: Any, config: ReplaceSpec.() -> Unit) {
        specs.add(ReplaceSpec(project.files(*files)).apply(config))
    }

    @TaskAction
    private fun runTask() {
        for(spec in specs) {
            spec.run()
        }
    }

    inner class ReplaceSpec internal constructor(private val files: FileCollection) {
        private val replacer = Replacer()
        private var currentFile: File? = null

        fun add(oldString: String, newString: String) {
            replacer.add(oldString, newString)
        }

        fun add(oldRegex: Regex, newString: String) {
            replacer.add(oldRegex, newString)
        }

        fun add(oldRegex: Regex, newString: (File, MatchResult) -> String) {
            replacer.add(oldRegex) { newString(currentFile!!, it) }
        }

        fun run() {
            for(file in files) {
                currentFile = file
                file.writeText(replacer.apply(file.readText()))
            }
        }
    }
}
