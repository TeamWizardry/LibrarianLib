package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.block.material.PushReaction
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.World

@TestObjectDslMarker
public class TestBlockConfig(public val id: String, public val name: String): TestConfig() {
    public constructor(id: String, name: String, config: TestBlockConfig.() -> Unit): this(id, name) {
        this.config()
    }

    public val properties: Block.Properties = Block.Properties.create(testMaterial)

    init {
        properties.notSolid()
    }

    /**
     * Whether the model should be transparent
     */
    public var transparent: Boolean = false

    /**
     * Whether the block should have a facing property
     */
    public var directional: Boolean = false

    /**
     * @see Block.tickRate
     */
    public var tickRate: Int = 10

    public val rightClick: SidedAction<RightClickContext> = SidedAction()
    public val leftClick: SidedAction<LeftClickContext> = SidedAction()
    public val destroy: SidedAction<DestroyContext> = SidedAction()
    public val place: SidedAction<PlaceContext> = SidedAction()

    public data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockRayTraceResult
    ): PlayerTestContext(player) {
        val stack: ItemStack = player.getHeldItem(hand)
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
            true, // requiresNoToolIn
            false, // canBurnIn
            false, // replaceableIn
            PushReaction.NORMAL // mobilityFlag
        )
    }
}
