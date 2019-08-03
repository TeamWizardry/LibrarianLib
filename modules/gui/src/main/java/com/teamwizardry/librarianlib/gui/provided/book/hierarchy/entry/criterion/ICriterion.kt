package com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.criterion

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.entity.player.EntityPlayer

/**
 * @author WireSegal
 * Created at 9:36 PM on 2/21/18.
 */
interface ICriterion {

    fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean): Boolean

    companion object {
        fun fromJson(element: JsonElement): ICriterion? {
            try {
                var obj: JsonObject? = null
                var provider: ((JsonObject) -> ICriterion)? = null
                when {
                    element.isJsonPrimitive -> {
                        provider = CriterionTypes.getCriterion("entry")
                        obj = JsonObject()
                        obj.addProperty("type", "entry")
                        obj.add("name", element)
                    }
                    element.isJsonArray -> {
                        provider = CriterionTypes.getCriterion("and")
                        obj = JsonObject()
                        obj.addProperty("type", "and")
                        obj.add("values", element)
                    }
                    element.isJsonObject -> {
                        obj = element.asJsonObject
                        provider = CriterionTypes.getCriterion(obj.getAsJsonPrimitive("type").asString)
                    }
                }

                return obj?.let {
                    provider?.invoke(it)
                }
            } catch (error: Exception) {
                error.printStackTrace()
                return null
            }

        }
    }
}
