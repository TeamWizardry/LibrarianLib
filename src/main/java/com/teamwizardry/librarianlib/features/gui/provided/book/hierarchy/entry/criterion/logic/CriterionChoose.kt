package com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.criterion.logic

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.criterion.ICriterion
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.storage.loot.RandomValueRange

/**
 * @author WireSegal
 * Created at 9:51 PM on 2/21/18.
 */
class CriterionChoose(json: JsonObject) : ICriterion {

    private val range: RandomValueRange = rangeDecoder.fromJson(json.get("range") ?: JsonPrimitive(1), RandomValueRange::class.java)
    private val criteria = json.getAsJsonArray("values").mapNotNull { ICriterion.fromJson(it) }

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean) =
            range.isInRange(criteria.count { it.isUnlocked(player, grantedInCode) })

    companion object {
        private val rangeDecoder = GsonBuilder()
                .registerTypeAdapter(RandomValueRange::class.java, RandomValueRange.Serializer())
                .create()
    }

}
