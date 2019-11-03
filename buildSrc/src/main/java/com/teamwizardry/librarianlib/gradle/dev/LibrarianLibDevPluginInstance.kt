package com.teamwizardry.librarianlib.gradle.dev

import org.gradle.api.Project

@Suppress("JoinDeclarationAndAssignment")
internal class LibrarianLibDevPluginInstance(val project: Project, val config: LibrarianLibDev) {

    init {
        project.beforeEvaluate { finishSetup() }
    }

    init {

    }

    fun finishSetup() {
    }
}