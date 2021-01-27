package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.core.util.sided.ClientFunction
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.DirectionalBlock
import net.minecraft.client.renderer.tileentity.TileEntityRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.state.DirectionProperty
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ActionResultType
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.Mirror
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fml.ModLoadingContext
import java.util.Random

public open class TestBlock(public val config: TestBlockConfig): Block(config.also { configHolder = it }.properties) {
    public var tileEntityType: TileEntityType<*>? = null
        private set
    public val tileEntityRenderer: ClientFunction<in TileEntityRendererDispatcher, out TileEntityRenderer<*>>?
    private var tileFactory: (() -> TileEntity)? = null

    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
        if (config.directional) {
            this.defaultState = this.stateContainer.baseState.with(FACING, Direction.UP)
        }
        @Suppress("UNCHECKED_CAST")
        val tileConfig = config.tileConfig as TestTileConfig<TileEntity>?
        if(tileConfig != null) {
            tileFactory = {
                @Suppress("UNCHECKED_CAST")
                tileConfig.factory(tileEntityType as TileEntityType<TileEntity>)
            }
            val type = TileEntityType.Builder.create(tileFactory, this).build(null)
            type.registryName = this.registryName
            tileEntityType = type
            tileEntityRenderer = tileConfig.renderer
        } else {
            tileEntityRenderer = null
        }
    }

    public open val modelName: String
        get() = "${if (config.directional) "directional" else "normal"}/${if (config.transparent) "transparent" else "solid"}"

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        if (!config.directional)
            return state
        return state.with(DirectionalBlock.FACING, rot.rotate(state.get(FACING)))
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        if (!config.directional)
            return state
        return state.with(DirectionalBlock.FACING, mirrorIn.mirror(state.get(FACING)))
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        if (!config.directional)
            return super.getStateForPlacement(context)

        val direction = context.face
        val blockstate = context.world.getBlockState(context.pos.offset(direction.opposite))
        return if (blockstate.block === this && blockstate.get(FACING) == direction) this.defaultState.with(FACING, direction.opposite) else this.defaultState.with(FACING, direction)
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        if (!configHolder!!.directional)
            return
        builder.add(FACING)
    }

    // ticks ===========================================================================================================
    override fun animateTick(stateIn: BlockState, worldIn: World, pos: BlockPos, rand: Random) {
        super.animateTick(stateIn, worldIn, pos, rand)
    }

    @Suppress("DEPRECATION")
    override fun tick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, rand: Random) {
        super.tick(state, worldIn, pos, rand)
    }

    @Suppress("DEPRECATION")
    override fun randomTick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, random: Random) {
        super.randomTick(state, worldIn, pos, random)
    }

    // placed/broken ===================================================================================================
    @Suppress("DEPRECATION")
    override fun onBlockAdded(p_220082_1_: BlockState, worldIn: World, pos: BlockPos, p_220082_4_: BlockState, p_220082_5_: Boolean) {
        super.onBlockAdded(p_220082_1_, worldIn, pos, p_220082_4_, p_220082_5_)
    }

    @Suppress("DEPRECATION")
    override fun updatePostPlacement(stateIn: BlockState, facing: Direction, facingState: BlockState, worldIn: IWorld, currentPos: BlockPos, facingPos: BlockPos): BlockState {
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos)
    }

    override fun onLanded(worldIn: IBlockReader, entityIn: Entity) {
        super.onLanded(worldIn, entityIn)
    }

    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (config.destroy.exists)
            config.destroy.run(worldIn.isRemote, TestBlockConfig.DestroyContext(state, worldIn, pos, player))
        else
            super.onBlockHarvested(worldIn, pos, state, player)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (placer is PlayerEntity)
            config.place.run(worldIn.isRemote, TestBlockConfig.PlaceContext(state, worldIn, pos, placer, stack))
        else
            super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    // interaction =====================================================================================================
    @Suppress("DEPRECATION")
    override fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): ActionResultType {
        config.rightClick.run(worldIn.isRemote(), TestBlockConfig.RightClickContext(state, worldIn, pos, player, handIn, hit))
        if (config.rightClick.exists)
            return ActionResultType.CONSUME
        else
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit)
    }

    @Suppress("DEPRECATION")
    override fun onBlockClicked(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity) {
        if (config.leftClick.exists)
            config.leftClick.run(worldIn.isRemote(), TestBlockConfig.LeftClickContext(state, worldIn, pos, player))
        else
            super.onBlockClicked(state, worldIn, pos, player)
    }

    // entity interaction ==============================================================================================
    @Suppress("DEPRECATION")
    override fun onEntityCollision(state: BlockState, worldIn: World, pos: BlockPos, entityIn: Entity) {
        super.onEntityCollision(state, worldIn, pos, entityIn)
    }

    override fun onEntityWalk(worldIn: World, pos: BlockPos, entityIn: Entity) {
        super.onEntityWalk(worldIn, pos, entityIn)
    }

    override fun onFallenUpon(worldIn: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance)
    }

    override fun onProjectileCollision(worldIn: World, state: BlockState, hit: BlockRayTraceResult, projectile: ProjectileEntity) {
        super.onProjectileCollision(worldIn, state, hit, projectile)
    }

    // misc ============================================================================================================

    override fun onNeighborChange(state: BlockState?, world: IWorldReader?, pos: BlockPos?, neighbor: BlockPos?) {
        super.onNeighborChange(state, world, pos, neighbor)
    }

    override fun isSideInvisible(state: BlockState, adjacentBlockState: BlockState, side: Direction): Boolean {
        return if (adjacentBlockState.block === this) true else super.isSideInvisible(state, adjacentBlockState, side)
    }

    override fun hasTileEntity(state: BlockState?): Boolean {
        return tileEntityType != null
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return tileFactory!!.invoke()
    }

    public companion object {
        public val FACING: DirectionProperty = BlockStateProperties.FACING

        // needed because fillStateContainer is called before we can set the config property
        private var configHolder: TestBlockConfig? by threadLocal()
    }
}