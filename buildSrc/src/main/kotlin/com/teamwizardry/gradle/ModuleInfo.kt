package com.teamwizardry.gradle

import com.teamwizardry.gradle.util.LiveCollection

open class ModuleInfo(val name: String) {
    val path: String = ":$name"
    val commonPath = "$path:common"
    val fabricPath = "$path:fabric"
    val neoForgePath = "$path:neoforge"
    val testModPath = "$path:testmod"

    /**
     * The direct dependencies of this module. (this is populated by the module plugin)
     */
    val dependencies: LiveCollection<ModuleInfo> = LiveCollection(mutableSetOf())

    /**
     * The direct and transitive dependencies of this module
     */
    val allDependencies: LiveCollection<ModuleInfo> = LiveCollection(mutableSetOf())

    val mavenName: String = name
    val apiMavenName: String = name
    val fabricMavenName: String = "$name-fabric"
    val neoForgeMavenName: String = "$name-neoforge"

    val modid: String = "liblib_$name"

    init {
        dependencies { dep ->
            // add direct dependencies
            allDependencies.add(dep)
            // add all of those dependencies' direct and transitive dependencies
            dep.allDependencies {
                allDependencies.add(it)
            }
        }
    }
}
