package com.teamwizardry.librarianlib.gradle.module

import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPlugin
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.ResolveException

open class LibrarianLibModule(private val project: Project) {
    internal lateinit var instance: LibrarianLibModulePluginInstance
    /**
     * The name of the liblib module. e.g. `"core"`, `"utilities"`, `"particles"`.
     *
     * Defaults to the project name.
     */
    var name: String = project.name

    /**
     * The [names][name] of the modules this module depends on.
     *
     * Defaults to `"core"` and `"testbase"`.
     */
    var dependencies: List<String> = listOf()
        set(value) {
            val missing = value.mapNotNull {
                try {
                    LibrarianLibDevPlugin.getModuleProject(project, it)
                } catch(e: UnknownProjectException) {
                    return@mapNotNull e
                }
                return@mapNotNull null
            }
            if(missing.isNotEmpty()) {
                throw ResolveException("LibrarianLib module '${project.name}'", missing)
            }
            field = value
        }

    /**
     * Whether this module should depend on the two core modules, "core" and "testbase".
     * Typically only false if they would result in circular dependencies.
     *
     * Defaults to true.
     */
    var includeCoreDependencies: Boolean = true

    // ==================================================== DSL ===================================================== //



    // ================================================== SUPPORT =================================================== //

}