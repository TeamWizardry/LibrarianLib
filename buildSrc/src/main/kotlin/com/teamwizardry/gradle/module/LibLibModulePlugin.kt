package com.teamwizardry.gradle.module

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.component.SoftwareComponentFactory
import javax.inject.Inject

/**
 * A plugin that provides a means of expressively defining the configuration of a module.
 *
 * @see ModuleExtension
 */
class LibLibModulePlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory
) : Plugin<Project> {

    override fun apply(target: Project) {
        val ext = target.extensions.create("module", ModuleExtension::class.java, DslContext(target))
        val module = softwareComponentFactory.adhoc("module")
        target.components.add(module)
    }
}