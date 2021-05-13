package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.core.util.append
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockItem
import com.teamwizardry.librarianlib.testcore.objects.TestObjectDslMarker
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import net.devtech.arrp.json.blockstate.JBlockModel
import net.devtech.arrp.json.blockstate.JState
import net.devtech.arrp.json.blockstate.JVariant
import net.devtech.arrp.json.models.JModel
import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

@TestObjectDslMarker
public class TestBlock(manager: TestModContentManager, id: Identifier): TestConfig(manager, id) {
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

    internal val instance: TestBlockImpl by lazy {
        TestBlockImpl(this)
    }
    internal val itemInstance: TestBlockItem by lazy {
        TestBlockItem(instance, Item.Settings().group(manager.itemGroup))
    }

    override fun registerCommon(resources: TestModResourceManager) {
        Registry.register(Registry.BLOCK, id, instance)
        Registry.register(Registry.ITEM, id, itemInstance)

        resources.lang.block(id, name)
        description?.also {
            resources.lang.block(id.append(".tooltip"), it)
        }
    }

    override fun registerClient(resources: TestModResourceManager) {
        val model = Identifier("liblib-testcore:block/test_block/${instance.modelName}")
        val state = JState.state()
        if(directional) {
            state.add(
                JVariant()
                    .put("facing", "up", JBlockModel(model))
                    .put("facing", "down", JBlockModel(model).x(180))
                    .put("facing", "east", JBlockModel(model).y(90).x(90))
                    .put("facing", "south", JBlockModel(model).y(180).x(90))
                    .put("facing", "west", JBlockModel(model).y(270).x(90))
                    .put("facing", "north", JBlockModel(model).y(0).x(90))
            )
        } else {
            state.add(
                JVariant()
                    .put("", JBlockModel(model))
            )
        }
        resources.arrp.addBlockState(state, Identifier(id.namespace, "blockstates/${id.path}"))

        resources.arrp.addModel(
            JModel.model().parent("$model"),
            Identifier(id.namespace, "item/${id.path}")
        )
    }

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
