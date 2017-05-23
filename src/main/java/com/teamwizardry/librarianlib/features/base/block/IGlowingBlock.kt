package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelWrapper
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockModelRenderer
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * Implement this to have a separate glowing form.
 */
interface IGlowingBlock {
    @SideOnly(Side.CLIENT)
    fun transformToGlow(world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos): IBakedModel?

    @SideOnly(Side.CLIENT)
    fun packedGlowCoords(world: IBlockAccess, state: IBlockState, pos: BlockPos): Int = 0xf000f0

    @SideOnly(Side.CLIENT)
    object Helper {
        @JvmStatic
        fun simpleBake(state: IBlockState): IBakedModel
                = Minecraft.getMinecraft().blockRendererDispatcher.getModelForState(state)

        @JvmStatic
        fun wrapperBake(model: IBakedModel, allowUntinted: Boolean, vararg allowedTintIndices: Int)
                = IGlowingItem.Helper.wrapperBake(model, allowUntinted, *allowedTintIndices)
    }
}

