package com.teamwizardry.gradle.module

import com.teamwizardry.gradle.LibLibExtension
import com.teamwizardry.gradle.ModuleConfig
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project

open class LibLibModuleExtension(project: Project) {
    val root: LibLibExtension = project.rootProject.extensions.getByType(LibLibExtension::class.java)
    val modules: NamedDomainObjectCollection<ModuleConfig> = root.modules
}