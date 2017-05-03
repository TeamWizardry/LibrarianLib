package com.teamwizardry.librarianlib.core.client

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.BlockModelRenderer
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * @author WireSegal
 * Created at 9:48 AM on 5/3/17.
 */
object RenderHookHandler {
    private val itemHooks = mutableListOf<ItemHook>(GlowingHandler::glow)
    private val blockHooks = mutableListOf<BlockHook>(GlowingHandler::glow)


    @JvmStatic
    fun registerItemHook(hook: ItemHook) {
        itemHooks.add(hook)
    }

    @JvmStatic
    fun registerBlockHook(hook: BlockHook) {
        blockHooks.add(hook)
    }

    @JvmStatic
    fun runItemHook(itemStack: ItemStack, bakedModel: IBakedModel) {
        itemHooks.forEach { it(itemStack, bakedModel) }
    }

    @JvmStatic
    fun runBlockHook(blockModelRenderer: BlockModelRenderer, world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos, vertexBuffer: VertexBuffer) {
        blockHooks.forEach { it(blockModelRenderer, world, model, state, pos, vertexBuffer) }
    }


}

typealias ItemHook = (ItemStack, IBakedModel) -> Unit
typealias BlockHook = (BlockModelRenderer, IBlockAccess, IBakedModel, IBlockState, BlockPos, VertexBuffer) -> Unit
