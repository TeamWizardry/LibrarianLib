package com.teamwizardry.librarianlib.gradle.module

import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDev
import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPlugin
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.ResolveException
import org.gradle.api.logging.Logging
import java.util.Collections

open class LibrarianLibModule(private val project: Project, private val root: LibrarianLibDev) {
    private var _dependencies = mutableListOf<String>()
    private var _testDependencies = mutableListOf<String>()
    
    /**
     * The name of the liblib module. e.g. `"core"`, `"utilities"`, `"particles"`.
     *
     * Defaults to the project name.
     */
    var name: String = project.name

    /**
     * The [names][name] of the modules this module depends on. The `"core"` module is implied unless
     * [includeCoreDependencies] is set to false.
     */
    val dependencies: List<String> = Collections.unmodifiableList(_dependencies)

    /**
     * The [names][name] of additional modules this module depends on during testing. The `"testbase"` module is
     * implied unless [includeCoreDependencies] is set to false.
     */
    val testDependencies: List<String> = Collections.unmodifiableList(_testDependencies)

    /**
     * Whether this module should depend on the two core modules, "core" and "testbase".
     * Typically only false if they would result in circular dependencies.
     *
     * Defaults to true.
     */
    var includeCoreDependencies: Boolean = true

    // ==================================================== DSL ===================================================== //

    fun require(vararg modules: String) {
        val newModules = modules.filter { it !in dependencies }
        verifyModules(newModules)
        _testDependencies.removeAll(newModules)
        _dependencies.addAll(newModules)
    }

    fun testRequire(vararg modules: String) {
        val newModules = modules.filter { it !in testDependencies && it !in dependencies }
        verifyModules(newModules)
        _testDependencies.addAll(newModules)
    }

    // ================================================== SUPPORT =================================================== //

    private var isConfigured = false
    
    internal fun configure(config: LibrarianLibModule.() -> Unit) {
        if(isConfigured) {
            throw IllegalStateException("Module '$name' has already been configured. " +
                "There can only be one `module {}` block in each project.")
        }

        logger.lifecycle("Configuring LibrarianLib module '$name'")
        this.config()
        logger.lifecycle("Finishing configuration of '$name'")
        
        addDependencies()
        isConfigured = true
    }

    private fun addDependencies() {
        val dependencies = dependencies.toMutableSet()
        if(includeCoreDependencies)
            dependencies.add("core")
        logger.lifecycle("Adding dependencies on modules ${dependencies.joinToString(", ") { "'$it'" }}")
        dependencies.forEach {
            project.dependencies.add("compileOnly", LibrarianLibDevPlugin.getModuleProject(project, it))
        }

        val testDependencies = dependencies.toMutableSet()
        if(includeCoreDependencies)
            testDependencies.add("testbase")
        logger.lifecycle("Adding test dependencies on modules ${testDependencies.joinToString(", ") { "'$it'" }}")
        testDependencies.forEach {
            project.dependencies.add("testCompileOnly", LibrarianLibDevPlugin.getModuleProject(project, it))
        }
    }

    private fun verifyModules(modules: List<String>) {
        val missing = modules.mapNotNull {
            try {
                LibrarianLibDevPlugin.getModuleProject(project, it)
            } catch(e: UnknownProjectException) {
                return@mapNotNull e
            }
            null
        }
        if(missing.isNotEmpty())
            throw ResolveException("LibrarianLib module '$name'", missing)
    }

    companion object {
        val logger = Logging.getLogger(LibrarianLibModule::class.java)
    }
}