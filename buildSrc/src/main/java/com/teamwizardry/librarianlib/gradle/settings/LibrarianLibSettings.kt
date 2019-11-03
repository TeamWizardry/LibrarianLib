package com.teamwizardry.librarianlib.gradle.settings

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.io.File

open class LibrarianLibSettings(internal val settings: Settings) {

    // ==================================================== DSL ===================================================== //

    /**
     * Find and add modules found in the specified directory.
     *
     * Each subdirectory is tested based on the following criteria: the directory contains a `build.gradle.kts` file,
     * and that file contains the string `"apply<LibrarianLibModulePlugin>()"`, with optional intervening whitespace.
     * If the subdirectory passes the test, it is added as a module, with the subdirectory name being used as the
     * module name. Warnings are printed for any build scripts that are found that don't contain LibrarianLib modules.
     */
    fun findModules(directory: File) {
        (directory.list() ?: throw IllegalArgumentException("The supplied path '${directory}' is not a directory"))
            .map { directory.resolve(it) }

            .filter { file ->
                file.isDirectory && file.resolve("build.gradle.kts").exists().also {
                    if(!it && file.resolve("build.gradle").exists())
                        logger.warn("Ignoring non-kotlin build script '${file.name}/build.gradle' in module search " +
                            "directory '$directory'.")
                }
            }

            .filter { file ->
                file.resolve("build.gradle.kts").readText().contains(moduleBuildScriptRegex).also {
                    if(!it)
                        logger.warn("Ignoring non-module build script '${file.name}/build.gradle.kts' in module search " +
                            "directory '$directory'.")
                }
            }

            .forEach {
                addModule(it)
            }
    }

    fun addModule(file: File) {
        val buildscript = file.resolve("build.gradle.kts")
        
        require(buildscript.exists()) {
            "No build.gradle.kts found in module directory '$file'" 
        }
        require(buildscript.readText().contains(moduleBuildScriptRegex)) {
            "Build script '$buildscript' is not a LibrarianLib module. Make sure to apply the LibrarianLib module " +
                "plugin using this syntax: 'apply<LibrarianLibModulePlugin>()'" 
        }

        val name = file.name
        settings.include(name)
        settings.project(":$name").projectDir = file
    }


    // ================================================== SUPPORT =================================================== //

    companion object {
        private val moduleBuildScriptRegex = Regex(listOf("apply", "<", "LibrarianLibModulePlugin", ">", "\\(", "\\)").joinToString("\\s*"))

        private val logger = Logging.getLogger(LibrarianLibSettings::class.java)
    }
}