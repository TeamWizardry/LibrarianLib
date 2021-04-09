package com.teamwizardry.gradle

import org.gradle.api.NamedDomainObjectContainer;
import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.module.ShadowRule
import org.gradle.api.provider.Property

open class CommonConfigExtension(private val ctx: DslContext) {
    var version: String = "?"
    val modules: NamedDomainObjectContainer<ModuleInfo> = ctx.domainObjectContainer { ModuleInfo(it, ctx) }

    private val shadowRuleListeners = mutableListOf<(ShadowRule) -> Unit>()
    private val _shadowRules = mutableListOf<ShadowRule>()
    val shadowRules: List<ShadowRule> = _shadowRules

    /**
     * Runs the given block with all the existing shadow rules, as well as subscribing it to be called whenever a new
     * rule is added
     */
    fun shadowRules(forEach: (ShadowRule) -> Unit) {
        shadowRules.forEach(forEach)
        shadowRuleListeners.add(forEach)
    }

    fun addShadowRule(rule: ShadowRule) {
        _shadowRules.add(rule)
        shadowRuleListeners.forEach { it(rule) }
    }
}

