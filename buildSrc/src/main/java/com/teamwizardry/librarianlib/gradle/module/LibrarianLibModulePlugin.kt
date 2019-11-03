package com.teamwizardry.librarianlib.gradle.module

import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.InvalidPluginException

/**
 * The plugin that configures LibrarianLib module subprojects. Use [LibrarianLibDevPlugin] for root projects that
 * should include liblib modules.
 *
 * This plugin requires that the root project has [LibrarianLibDevPlugin] applied, and can't be applied to the root
 * project.
 */
class LibrarianLibModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if(target.rootProject == target)
            throw InvalidPluginException("LibrarianLibModulePlugin can not be applied to the root project")
        val root = LibrarianLibDevPlugin.getInstance(target.rootProject)
            ?: throw InvalidPluginException("LibrarianLibModulePlugin requires that the root project have " +
                "LibrarianLibDevPlugin applied")
        val extension = target.extensions.create("LibrarianLibModule", LibrarianLibModule::class.java, target)
        extension.instance = LibrarianLibModulePluginInstance(target, extension, root)
    }

    companion object {
        internal fun getInstance(project: Project): LibrarianLibModulePluginInstance? {
            return (project.extensions.findByName("LibrarianLibModule") as LibrarianLibModule?)?.instance
        }
    }
}
