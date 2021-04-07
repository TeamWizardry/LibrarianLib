package com.teamwizardry.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.teamwizardry.gradle.util.DslContext

class CommonConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("commonConfig", CommonConfigExtension::class.java, DslContext(target))
    }
}