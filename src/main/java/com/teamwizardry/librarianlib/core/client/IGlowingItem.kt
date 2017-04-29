package com.teamwizardry.librarianlib.core.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Implement this to have a separate glowing form.
 */
interface IGlowingItem {
    @SideOnly(Side.CLIENT)
    fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel?

    object Helper {
        @SideOnly(Side.CLIENT)
        fun simpleBake(itemStack: ItemStack): IBakedModel {
            return Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(itemStack, null, null)
        }
    }
}
