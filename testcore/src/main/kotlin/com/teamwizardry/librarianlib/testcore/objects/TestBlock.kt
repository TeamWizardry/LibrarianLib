package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.testcore.TestModManager
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@TestObjectDslMarker
public class TestBlock(manager: TestModManager, id: Identifier): TestConfig(manager, id) {
    public val properties: AbstractBlock.Settings = AbstractBlock.Settings.of(testMaterial)

    init {
        properties.nonOpaque()
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

//    internal var tileConfig: TestTileConfig<*>? = null
//
//    public fun <T: BlockEntity> tile(factory: (BlockEntityType<T>) -> T): TestTileConfig<T> {
//        val config = TestTileConfig(factory)
//        tileConfig = config
//        return config
//    }

    public data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockHitResult
    ): PlayerTestContext(player) {
        val stack: ItemStack = player.getStackInHand(hand)
    }

    public data class LeftClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player) {
    }

    public data class DestroyContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player) {
    }

    public data class PlaceContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val stack: ItemStack
    ): PlayerTestContext(player) {
    }

    private companion object {
        val testMaterial: Material = Material(
            MaterialColor.PINK, // materialMapColorIn
            false, // liquid
            false, // solid
            true, // doesBlockMovement
            false, // opaque
            false, // canBurnIn
            false, // replaceableIn
            PistonBehavior.NORMAL // mobilityFlag
        )
    }
}
