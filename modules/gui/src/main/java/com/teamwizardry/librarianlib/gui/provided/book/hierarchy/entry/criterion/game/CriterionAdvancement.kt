package com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.criterion.game

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.criterion.ICriterion
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementProgress
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.ClientAdvancementManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
class CriterionAdvancement(json: JsonObject) : ICriterion {

    private val advancement = ResourceLocation(json.getAsJsonPrimitive("name").asString)

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean): Boolean {
        if (player is EntityPlayerMP) {
            val adv = player.serverWorld.advancementManager.getAdvancement(advancement)
            return adv != null && player.advancements.getProgress(adv).isDone
        }
        if (player is EntityPlayerSP) {
            val bool = ClientRunnable.produce(ClientRunnable.ClientSupplier {
                val manager = player.connection.advancementManager
                val adv = manager.advancementList.getAdvancement(advancement) ?: return@ClientSupplier false
                val progress = progress(manager)[adv]
                progress != null && progress.isDone
            })
            return bool == true
        }
        return false
    }

    companion object {

        @SideOnly(Side.CLIENT)
        private var mh: ((ClientAdvancementManager) -> Any?)? = null

        init {
            ClientRunnable.run { mh = MethodHandleHelper.wrapperForGetter(ClientAdvancementManager::class.java, "field_192803_d, advancementToProgress") }
        }

        @Suppress("UNCHECKED_CAST")
        @SideOnly(Side.CLIENT)
        private fun progress(manager: ClientAdvancementManager): Map<Advancement, AdvancementProgress> {
            return mh!!.invoke(manager) as Map<Advancement, AdvancementProgress>
        }
    }
}
