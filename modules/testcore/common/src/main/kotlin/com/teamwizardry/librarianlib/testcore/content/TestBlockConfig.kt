package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockItem
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockWithEntityImpl
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@TestConfigDslMarker
public class TestBlockConfig(moduleConfig: TestModuleConfig, id: Identifier): TestConfig(moduleConfig, id) {
    public val properties: AbstractBlock.Settings
        get() {
            val props = AbstractBlock.Settings.create()
                .mapColor(MapColor.PINK)
                .pistonBehavior(PistonBehavior.NORMAL)
            if (transparent) {
                props
                    .nonOpaque()
                    .solidBlock { state, world, pos -> false }
            }
            return props
        }

    /**
     * Whether the model should be transparent
     */
    public var transparent: Boolean = false

    /**
     * Whether the block should have a facing property
     */
    public var directional: Boolean = false

    public val rightClick: SidedAction<RightClickContext> = SidedAction()
    public val leftClick: SidedAction<LeftClickContext> = SidedAction()
    public val destroy: SidedAction<DestroyContext> = SidedAction()
    public val place: SidedAction<PlaceContext> = SidedAction()

    public var blockEntityFactory: ((BlockEntityType<BlockEntity>, BlockPos, BlockState) -> BlockEntity)? = null
        private set
    public var blockEntityTickFunction: ((BlockEntity) -> Unit)? = null
        private set

    public fun <T: BlockEntity> blockEntity(factory: (BlockEntityType<T>, BlockPos, BlockState) -> T) {
        if(blockEntityFactory != null) throw IllegalStateException("Can't replace an existing block entity factory")
        @Suppress("UNCHECKED_CAST")
        this.blockEntityFactory = factory as (BlockEntityType<BlockEntity>, BlockPos, BlockState) -> BlockEntity
    }
    public fun <T: BlockEntity> blockEntityTickFunction(ticker: (T) -> Unit) {
        if(blockEntityTickFunction != null) throw IllegalStateException("Can't replace an existing block entity ticker")
        @Suppress("UNCHECKED_CAST")
        this.blockEntityTickFunction = ticker as (BlockEntity) -> Unit
    }

    internal val blockInstance: TestBlockImpl by lazy {
        if(blockEntityFactory != null)
            TestBlockWithEntityImpl(this)
        else
            TestBlockImpl(this)
    }

    internal val itemInstance: TestBlockItem by lazy {
        TestBlockItem(blockInstance, Item.Settings().maxCount(1).`arch$tab`(moduleConfig.itemGroup.instance))
    }

    internal val blockEntityType: BlockEntityType<BlockEntity>? by lazy {
        blockEntityFactory?.let { factory ->
            BlockEntityType.Builder
                .create({ pos, state -> factory(this.blockEntityType!!, pos, state) }, blockInstance)
                .build(null)
        }
    }

    public data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockHitResult,
        val stack: ItemStack?
    ): PlayerTestContext(player)

    public data class LeftClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player)

    public data class DestroyContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player)

    public data class PlaceContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val stack: ItemStack
    ): PlayerTestContext(player)
}
