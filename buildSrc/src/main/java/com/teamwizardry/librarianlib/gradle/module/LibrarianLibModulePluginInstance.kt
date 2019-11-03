package com.teamwizardry.librarianlib.gradle.module

import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPlugin
import com.teamwizardry.librarianlib.gradle.dev.LibrarianLibDevPluginInstance
import org.gradle.api.Project

@Suppress("JoinDeclarationAndAssignment")
internal class LibrarianLibModulePluginInstance(val project: Project, val config: LibrarianLibModule, val root: LibrarianLibDevPluginInstance) {

    init {
        project.beforeEvaluate { finishSetup() }
    }

    init {

    }

    fun finishSetup() {
        config.dependencies.forEach {
            project.dependencies.add("compileOnly", LibrarianLibDevPlugin.getModuleProject(project, it))
        }
    }
}