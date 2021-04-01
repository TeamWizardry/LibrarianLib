package com.teamwizardry.gradle.module

import com.teamwizardry.gradle.LibLibExtension
import org.gradle.api.Project

open class LibLibModuleExtension(project: Project) {
    val root: LibLibExtension =
        project.rootProject.extensions.getByType(LibLibExtension::class.java)
}