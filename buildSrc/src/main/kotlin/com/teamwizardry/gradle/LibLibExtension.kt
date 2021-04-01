package com.teamwizardry.gradle

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.kotlin.dsl.domainObjectContainer
import com.teamwizardry.gradle.util.DslContext

open class LibLibExtension(private val ctx: DslContext) {
    val modules: NamedDomainObjectContainer<ModuleConfig> =
        ctx.domainObjectContainer { ModuleConfig(it, ctx) }
    private val repos = mutableListOf<RepositoryHandler.() -> Unit>()

    fun repositories(repos: RepositoryHandler.() -> Unit) {
        this.repos.add(repos)
    }

    internal fun applyRepositories(handler: RepositoryHandler) {
        this.repos.forEach { repo ->
            handler.repo()
        }
    }
}

