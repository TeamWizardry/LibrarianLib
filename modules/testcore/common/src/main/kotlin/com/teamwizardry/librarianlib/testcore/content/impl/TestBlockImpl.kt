package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.testcore.content.TestBlockConfig
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.*

public open class TestBlockImpl(public val config: TestBlockConfig): Block(config.also { configHolder = it }.properties) {

    init {
        if (config.directional) {
            this.defaultState = this.stateManager.defaultState.with(FACING, Direction.UP)
        }
    }

    public open val modelName: String
        get() = "${if (config.directional) "directional" else "normal"}/${if (config.transparent) "transparent" else "solid"}"

    override fun getDroppedStacks(state: BlockState, builder: LootContextParameterSet.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        if (!config.directional)
            return state
        return state.with(FacingBlock.FACING, rotation.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        if (!config.directional)
            return state
        return state.with(FacingBlock.FACING, mirror.apply(state.get(FACING)))
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        if (!config.directional)
            return super.getPlacementState(ctx)

        val direction = ctx.side
        val blockstate = ctx.world.getBlockState(ctx.blockPos.offset(direction.opposite))
        return if (blockstate.block === this && blockstate.get(FACING) == direction) this.defaultState.with(FACING, direction.opposite) else this.defaultState.with(
            FACING, direction)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        if (!configHolder!!.directional)
            return
        builder.add(FACING)
    }

    // placed/broken ===================================================================================================

    override fun afterBreak(
        world: World,
        player: PlayerEntity,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack?
    ) {
        if (config.destroy.exists)
            config.destroy.run(world.isClient, TestBlockConfig.DestroyContext(state, world, pos, player))
        else
            super.afterBreak(world, player, pos, state, blockEntity, tool)
    }

    override fun onPlaced(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (placer is PlayerEntity)
            config.place.run(worldIn.isClient, TestBlockConfig.PlaceContext(state, worldIn, pos, placer, stack))
        else
            super.onPlaced(worldIn, pos, state, placer, stack)
    }

    override fun onUseWithItem(
        stack: ItemStack?,
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ItemActionResult {
        config.rightClick.run(world.isClient, TestBlockConfig.RightClickContext(state, world, pos, player, hand, hit, stack))
        if (config.rightClick.exists)
            return ItemActionResult.CONSUME
        else
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit)
    }

    override fun onBlockBreakStart(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity) {
        if (config.leftClick.exists)
            config.leftClick.run(world.isClient, TestBlockConfig.LeftClickContext(state, world, pos, player))
        else
            super.onBlockBreakStart(state, world, pos, player)
    }

    override fun isSideInvisible(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean {
        return if (adjacentBlockState.block === this) true else super.isSideInvisible(state, adjacentBlockState, side)
    }

    public companion object {
        public val FACING: EnumProperty<Direction> = Properties.FACING

        // needed because fillStateContainer is called before we can set the config property
        private var configHolder: TestBlockConfig? by threadLocal()
    }
}
