package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.Entry
import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.ICriterion
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
class CriterionEntry(json: JsonObject) : ICriterion {

    private val entry: ResourceLocation = ResourceLocation(json.getAsJsonPrimitive("name").asString)

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean): Boolean {
        val entryObj = Entry.ENTRIES[entry]
        return entryObj != null && entryObj.isUnlocked(player)
    }
}
