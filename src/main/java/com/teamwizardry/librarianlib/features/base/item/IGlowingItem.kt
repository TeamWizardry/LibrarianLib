package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.core.client.ModelWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * Implement this to have a separate glowing form.
 */
interface IGlowingItem {
    @SideOnly(Side.CLIENT)
    fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel?

    @SideOnly(Side.CLIENT)
    fun packedGlowCoords(itemStack: ItemStack, model: IBakedModel): Int = 0xf000f0

    @SideOnly(Side.CLIENT)
    fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel): Boolean = false

    @SideOnly(Side.CLIENT)
    object Helper {
        @JvmStatic
        fun simpleBake(itemStack: ItemStack): IBakedModel {
            return Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(itemStack, null, null)
        }

        private class ModelEntry(val model: IBakedModel, val allowUntinted: Boolean, val allowedIndices: IntArray) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other?.javaClass != javaClass) return false

                other as ModelEntry

                if (model != other.model) return false
                if (allowUntinted != other.allowUntinted) return false
                if (!Arrays.equals(allowedIndices, other.allowedIndices)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = model.hashCode()
                result = 31 * result + allowUntinted.hashCode()
                result = 31 * result + Arrays.hashCode(allowedIndices)
                return result
            }
        }

        private val wrappedModels = mutableMapOf<ModelEntry, IBakedModel>()

        @JvmStatic
        fun wrapperBake(model: IBakedModel, allowUntinted: Boolean, vararg allowedTintIndices: Int): IBakedModel {
            val modelEntry = ModelEntry(model, allowUntinted, allowedTintIndices)

            if (allowedTintIndices.isEmpty()) return wrappedModels.getOrPut(modelEntry) {
                ModelWrapper(model, allowUntinted) { true }
            }

            return wrappedModels.getOrPut(modelEntry) {
                ModelWrapper(model, allowUntinted) { it in allowedTintIndices }
            }

        }
    }
}

