package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.DirectionalBlock
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.Direction
import net.minecraft.util.Mirror
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Rotation
import net.minecraft.world.storage.loot.LootContext
import net.minecraftforge.fml.ModLoadingContext

open class TestBlock(val config: TestBlockConfig): Block(config.also { configHolder = it }.properties) {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
        if(config.directional) {
            this.defaultState = this.stateContainer.baseState.with(FACING, Direction.UP)
        }
    }

    open val modelName: String
        get() = "${if(config.directional) "directional" else "normal"}/${if(config.transparent) "transparent" else "solid"}"

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        if(!config.directional)
            return state
        return state.with(DirectionalBlock.FACING, rot.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        if(!config.directional)
            return state
        return state.with(DirectionalBlock.FACING, mirrorIn.mirror(state.get(FACING)))
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        if(!config.directional)
            return super.getStateForPlacement(context)

        val direction = context.face
        val blockstate = context.world.getBlockState(context.pos.offset(direction.opposite))
        return if (blockstate.block === this && blockstate.get(FACING) == direction) this.defaultState.with(FACING, direction.opposite) else this.defaultState.with(FACING, direction)
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        if(!configHolder!!.directional)
            return
        builder.add(FACING)
    }

    companion object {
        val FACING = BlockStateProperties.FACING

        // needed because fillStateContainer is called before we can set the config property
        private var configHolder: TestBlockConfig? by threadLocal()
    }
}