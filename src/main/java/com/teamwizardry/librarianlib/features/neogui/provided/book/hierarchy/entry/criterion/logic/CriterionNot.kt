package com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.entry.criterion.logic

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.neogui.provided.book.hierarchy.entry.criterion.ICriterion
import net.minecraft.entity.player.EntityPlayer

/**
 * @author WireSegal
 * Created at 9:45 PM on 2/21/18.
 */
class CriterionNot(choose: JsonObject) : ICriterion {

    private val criterion: ICriterion?

    init {
        var superCriterion = choose.get("criterion")
        if (superCriterion.isJsonArray) {
            val obj = JsonObject()
            obj.addProperty("type", "or")
            obj.add("values", superCriterion)

            superCriterion = obj
        }

        criterion = ICriterion.fromJson(superCriterion)
    }

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean) =
            criterion == null || !criterion.isUnlocked(player, grantedInCode)
}
