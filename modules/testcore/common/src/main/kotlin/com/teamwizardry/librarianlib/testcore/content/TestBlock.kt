package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockItem
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockWithEntityImpl
import com.teamwizardry.librarianlib.testcore.objects.TestObjectDslMarker
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.data.client.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.state.property.Properties
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import pers.solid.brrp.v1.model.ModelJsonBuilder

@TestObjectDslMarker
public class TestBlock(manager: TestModContentManager, id: Identifier): TestConfig(manager, id) {
    public val properties: AbstractBlock.Settings = AbstractBlock.Settings.create()
        .mapColor(MapColor.PINK)
        .pistonBehavior(PistonBehavior.NORMAL)
        .nonOpaque()

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
        TestBlockItem(blockInstance, Item.Settings())
    }
    internal val blockEntityType: BlockEntityType<BlockEntity>? by lazy {
        blockEntityFactory?.let { factory ->
            FabricBlockEntityTypeBuilder.create(
                { pos, state -> factory(this.blockEntityType!!, pos, state) },
                blockInstance
            ).build()
        }
    }

    override fun registerCommon(resources: TestModResourceManager) {
        blockEntityType?.also {
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id, blockEntityType)
        }
        Registry.register(Registries.BLOCK, id, blockInstance)
        Registry.register(Registries.ITEM, id, itemInstance)
        ItemGroupEvents.modifyEntriesEvent(manager.itemGroupKey).register { it.add(itemInstance) }

        resources.lang.add(blockInstance, name)
        description?.also {
            resources.lang.add(id.toTranslationKey("block", "tooltip"), it)
        }
    }

    override fun registerClient(resources: TestModResourceManager) {
        val model = Identifier.of("liblib-testcore:block/test_block/${blockInstance.modelName}")


        val state = VariantsBlockStateSupplier.create(blockInstance, BlockStateVariant.create().put(VariantSettings.MODEL, model))
        if(directional) {
            state.coordinate(
                BlockStateVariantMap.create(Properties.FACING)
                    .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                    .register(Direction.UP, BlockStateVariant.create())
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(
                        Direction.SOUTH,
                        BlockStateVariant.create()
                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R180)
                    )
                    .register(
                        Direction.WEST,
                        BlockStateVariant.create()
                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
                    .register(
                        Direction.EAST,
                        BlockStateVariant.create()
                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
                    )
            )
        }
        resources.runtimeResourcePack.addBlockState(Identifier.of(id.namespace, "blockstates/${id.path}"), state)

        resources.runtimeResourcePack.addModel(
            Identifier.of(id.namespace, "item/${id.path}"),
            ModelJsonBuilder.create("$model")
        )
    }

    public data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockHitResult,
        val stack: ItemStack?
    ): PlayerTestContext(player) {
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
}
