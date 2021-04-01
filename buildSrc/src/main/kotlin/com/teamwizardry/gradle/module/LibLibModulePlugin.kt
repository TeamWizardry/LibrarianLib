package com.teamwizardry.gradle.module

import org.gradle.api.Plugin
import org.gradle.api.Project

class LibLibModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("liblib", LibLibModuleExtension::class.java, target)
        extension.root.applyRepositories(target.repositories)
    }
}