package com.teamwizardry.gradle.module

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.teamwizardry.gradle.util.DslContext

/**
 * A plugin that provides a means of expressively defining the configuration of a module.
 *
 * @see ModuleExtension
 */
class LibLibModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("module", ModuleExtension::class.java, DslContext(target))
    }
}