package com.teamwizardry.librarianlib.core.client

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.BlockModelRenderer
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 9:48 AM on 5/3/17.
 */
@SideOnly(Side.CLIENT)
object RenderHookHandler {
    private val itemHooks = mutableListOf<ItemHook>(GlowingHandler::glow)
    private val blockHooks = mutableListOf<BlockHook>(GlowingHandler::glow)
    private val fluidHooks = mutableListOf<FluidHook>()


    @JvmStatic
    fun registerItemHook(hook: ItemHook) {
        itemHooks.add(hook)
    }

    @JvmStatic
    fun registerBlockHook(hook: BlockHook) {
        blockHooks.add(hook)
    }

    @JvmStatic
    fun registerFluidHook(hook: FluidHook) {
        fluidHooks.add(hook)
    }

    @JvmStatic
    fun runItemHook(itemStack: ItemStack, bakedModel: IBakedModel) {
        itemHooks.forEach { it(itemStack, bakedModel) }
    }

    @JvmStatic
    fun runBlockHook(blockModelRenderer: BlockModelRenderer, world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos, vertexBuffer: BufferBuilder) {
        blockHooks.forEach { it(blockModelRenderer, world, model, state, pos, vertexBuffer) }
    }

    @JvmStatic
    fun runFluidHook(blockModelRenderer: BlockModelRenderer, world: IBlockAccess, state: IBlockState, pos: BlockPos, vertexBuffer: BufferBuilder) {
        fluidHooks.forEach { it(blockModelRenderer, world, state, pos, vertexBuffer) }
    }


}

typealias ItemHook = (ItemStack, IBakedModel) -> Unit
typealias BlockHook = (BlockModelRenderer, IBlockAccess, IBakedModel, IBlockState, BlockPos, BufferBuilder) -> Unit
typealias FluidHook = (BlockModelRenderer, IBlockAccess, IBlockState, BlockPos, BufferBuilder) -> Unit
