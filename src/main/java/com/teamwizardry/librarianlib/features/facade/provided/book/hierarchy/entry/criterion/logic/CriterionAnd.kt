package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.logic

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.ICriterion
import net.minecraft.entity.player.EntityPlayer

/**
 * @author WireSegal
 * Created at 9:45 PM on 2/21/18.
 */
class CriterionAnd(json: JsonObject) : ICriterion {

    private val criteria = json.getAsJsonArray("values").mapNotNull { ICriterion.fromJson(it) }

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean) =
            criteria.all { it.isUnlocked(player, grantedInCode) }
}
