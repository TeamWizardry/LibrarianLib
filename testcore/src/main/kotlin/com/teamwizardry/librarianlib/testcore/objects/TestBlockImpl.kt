package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.*
import java.util.Random

public open class TestBlockImpl(public val config: TestBlock): Block(config.also { configHolder = it }.properties) {
//    public var tileEntityType: TileEntityType<*>? = null
//        private set
//    public val tileEntityRenderer: ClientMetaSupplier<TileEntityRendererFactory>?
//    private var tileFactory: (() -> TileEntity)? = null

    init {
//        this.registryName = Identifier(ModLoadingContext.get().activeContainer.modId, config.id)
        if (config.directional) {
            this.defaultState = this.stateManager.defaultState.with(FACING, Direction.UP)
        }
//        @Suppress("UNCHECKED_CAST")
//        val tileConfig = config.tileConfig as TestTileConfig<TileEntity>?
//        if(tileConfig != null) {
//            tileFactory = {
//                @Suppress("UNCHECKED_CAST")
//                tileConfig.factory(tileEntityType as TileEntityType<TileEntity>)
//            }
//            val type = TileEntityType.Builder.create(tileFactory, this).build(null)
//            type.registryName = this.registryName
//            tileEntityType = type
//            tileEntityRenderer = tileConfig.renderer
//        } else {
//            tileEntityRenderer = null
//        }
    }

    public open val modelName: String
        get() = "${if (config.directional) "directional" else "normal"}/${if (config.transparent) "transparent" else "solid"}"

    override fun getDroppedStacks(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
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
        return if (blockstate.block === this && blockstate.get(FACING) == direction) this.defaultState.with(FACING, direction.opposite) else this.defaultState.with(FACING, direction)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        if (!configHolder!!.directional)
            return
        builder.add(FACING)
    }

    // ticks ===========================================================================================================
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        super.randomDisplayTick(state, world, pos, random)
    }

    @Suppress("DEPRECATION")
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        super.scheduledTick(state, world, pos, random)
    }

    @Suppress("DEPRECATION")
    override fun randomTick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, random: Random) {
        super.randomTick(state, worldIn, pos, random)
    }

    // placed/broken ===================================================================================================
    @Suppress("DEPRECATION")
    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        super.onBlockAdded(state, world, pos, oldState, notify)
    }

    @Suppress("DEPRECATION")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, newState: BlockState, world: WorldAccess, pos: BlockPos, posFrom: BlockPos): BlockState {
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (config.destroy.exists)
            config.destroy.run(world.isClient, TestBlock.DestroyContext(state, world, pos, player))
        else
            super.onBreak(world, pos, state, player)
    }

    override fun onPlaced(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (placer is PlayerEntity)
            config.place.run(worldIn.isClient, TestBlock.PlaceContext(state, worldIn, pos, placer, stack))
        else
            super.onPlaced(worldIn, pos, state, placer, stack)
    }

    // interaction =====================================================================================================
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        config.rightClick.run(world.isClient, TestBlock.RightClickContext(state, world, pos, player, hand, hit))
        if (config.rightClick.exists)
            return ActionResult.CONSUME
        else
            return super.onUse(state, world, pos, player, hand, hit)
    }


    override fun onBlockBreakStart(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity) {
        if (config.leftClick.exists)
            config.leftClick.run(world.isClient, TestBlock.LeftClickContext(state, world, pos, player))
        else
            super.onBlockBreakStart(state, world, pos, player)
    }

    // entity interaction ==============================================================================================
    @Suppress("DEPRECATION")
    override fun onEntityCollision(state: BlockState, worldIn: World, pos: BlockPos, entityIn: Entity) {
        super.onEntityCollision(state, worldIn, pos, entityIn)
    }

    override fun onSteppedOn(world: World, pos: BlockPos, entity: Entity) {
        super.onSteppedOn(world, pos, entity)
    }

    override fun onLandedUpon(world: World, pos: BlockPos, entity: Entity, distance: Float) {
        super.onLandedUpon(world, pos, entity, distance)
    }

    override fun onProjectileHit(world: World, state: BlockState, hit: BlockHitResult, projectile: ProjectileEntity) {
        super.onProjectileHit(world, state, hit, projectile)
    }

    // misc ============================================================================================================

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        notify: Boolean
    ) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
    }

    override fun isSideInvisible(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean {
        return if (adjacentBlockState.block === this) true else super.isSideInvisible(state, adjacentBlockState, side)
    }

    // todo: fabric tile entities
//    override fun hasBlockEntity(state: BlockState?): Boolean {
//        return tileEntityType != null
//    }
//
//    override fun createTileEntity(state: BlockState?, world: IBlockReader?): BlockEntity? {
//        return tileFactory!!.invoke()
//    }

    public companion object {
        public val FACING: DirectionProperty = Properties.FACING

        // needed because fillStateContainer is called before we can set the config property
        private var configHolder: TestBlock? by threadLocal()
    }
}

public fun interface TileEntityRendererFactory: ClientSideFunction {
    public fun create(dispatcher: BlockEntityRenderDispatcher): BlockEntityRenderer<*>
}
