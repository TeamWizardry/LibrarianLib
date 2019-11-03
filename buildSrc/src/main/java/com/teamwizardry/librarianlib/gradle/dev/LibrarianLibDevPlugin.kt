package com.teamwizardry.librarianlib.gradle.dev

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.InvalidPluginException

/**
 * The plugin used by root projects that should include liblib modules.
 *
 * This plugin can only be applied to the root project.
 */
class LibrarianLibDevPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if(target.rootProject != target)
            throw InvalidPluginException("LibrarianLibDevPlugin can only be applied to the root project")
        val extension = target.extensions.create("LibrarianLibDev", LibrarianLibDev::class.java, target)
        extension.instance = LibrarianLibDevPluginInstance(target, extension)
    }

    companion object {
        internal fun getInstance(project: Project): LibrarianLibDevPluginInstance? {
            return (project.extensions.findByName("LibrarianLibDev") as LibrarianLibDev?)?.instance
        }

        fun findModuleProject(project: Project, moduleName: String): Project? {
            return project.findProject(":$moduleName")
        }

        fun getModuleProject(project: Project, moduleName: String): Project {
            return project.project(":$moduleName")
        }
    }
}
