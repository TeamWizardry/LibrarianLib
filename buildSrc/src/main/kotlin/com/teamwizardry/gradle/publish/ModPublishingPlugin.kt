package com.teamwizardry.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.component.SoftwareComponentFactory
import javax.inject.Inject

class ModPublishingPlugin @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory
) : Plugin<Project> {

    override fun apply(target: Project) {
        val ext = target.extensions.create("modPublishing", ModPublishingExtension::class.java, DslContext(target))
        val module = softwareComponentFactory.adhoc("mod")
        target.components.add(module)
    }
}