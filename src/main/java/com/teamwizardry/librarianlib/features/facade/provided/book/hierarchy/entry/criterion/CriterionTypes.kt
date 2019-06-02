package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game.CriterionAdvancement
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game.CriterionCode
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game.CriterionEntry
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.logic.CriterionAnd
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.logic.CriterionChoose
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.logic.CriterionNot
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.logic.CriterionOr
import net.minecraft.util.ResourceLocation
import java.util.*

object CriterionTypes {

    private val criterionTypes = mutableMapOf<String, (JsonObject) -> ICriterion>()

    init {
        registerCriterion("and", ::CriterionAnd)
        registerCriterion("or", ::CriterionOr)
        registerCriterion("not", ::CriterionNot)
        registerCriterion("choose", ::CriterionChoose)
        registerCriterion("xor", ::CriterionChoose)
        registerCriterion("entry", ::CriterionEntry)
        registerCriterion("advancement", ::CriterionAdvancement)
        registerCriterion("manual", { CriterionCode() })
    }

    fun registerCriterion(name: String, provider: (JsonObject) -> ICriterion) {
        registerCriterion(ResourceLocation(name), provider)
    }

    fun registerCriterion(name: ResourceLocation, provider: (JsonObject) -> ICriterion) {
        val key = name.toString()
        if (!criterionTypes.containsKey(key))
            criterionTypes[key] = provider
    }

    fun getCriterion(type: String): ((JsonObject) -> ICriterion)? {
        return getCriterion(ResourceLocation(type.toLowerCase(Locale.ROOT)))
    }

    fun getCriterion(type: ResourceLocation): ((JsonObject) -> ICriterion)? {
        return criterionTypes[type.toString()]
    }
}
