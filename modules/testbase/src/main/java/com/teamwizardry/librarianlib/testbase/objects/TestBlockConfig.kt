package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.ClientRunnable
import com.teamwizardry.librarianlib.core.util.SidedConsumer
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
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.ModLoadingContext

@TestObjectDslMarker
class TestBlockConfig(val id: String, val name: String): TestConfig() {
    constructor(id: String, name: String, config: TestBlockConfig.() -> Unit): this(id, name) {
        this.config()
    }

    val properties: Block.Properties = Block.Properties.create(testMaterial)

    /**
     * Whether the model should be transparent
     */
    var transparent: Boolean = false
    /**
     * Whether the block should have a facing property
     */
    var directional: Boolean = false

    /**
     * @see Block.tickRate
     */
    var tickRate: Int = 10

    val rightClick = Action<RightClickContext>()
    val leftClick = Action<LeftClickContext>()
    val destroy = Action<DestroyContext>()
    val place = Action<PlaceContext>()

    data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockRayTraceResult
    ): PlayerTestItemContext(player) {
        val stack: ItemStack = player.getHeldItem(hand)
    }

    data class LeftClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestItemContext(player) {
    }

    data class DestroyContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestItemContext(player) {
    }

    data class PlaceContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val stack: ItemStack
    ): PlayerTestItemContext(player) {
    }

    companion object {
        val testMaterial: Material = Material(
            MaterialColor.PINK, // materialMapColorIn
            false, // liquid
            true, // solid
            true, // doesBlockMovement
            false, // opaque
            true, // requiresNoToolIn
            false, // canBurnIn
            false, // replaceableIn
            PushReaction.NORMAL // mobilityFlag
        )
    }
}
