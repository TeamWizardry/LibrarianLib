package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.core.util.append
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.content.impl.TestItemImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestItemModel
import com.teamwizardry.librarianlib.testcore.objects.TestObjectDslMarker
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import net.devtech.arrp.json.models.JModel
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

/**
 * The DSL for configuring an item
 */
@TestObjectDslMarker
public class TestItem(manager: TestModContentManager, id: Identifier): TestConfig(manager, id) {
    /**
     * The maximum stack size. Defaults to 1
     */
    public var maxCount: Int = 1
        set(value) {
            field = value
            properties.maxCount(value)
        }

    /**
     * How long the right click and hold action should last in ticks. Setting this to a non-negative value will cause
     * override the default behavior of returning one hour when either the [rightClickHold] or [rightClickRelease]
     * actions are enabled
     */
    public var rightClickHoldDuration: Int = -1
        get() {
            if (field < 0) {
                if (rightClickHold.exists || rightClickRelease.exists)
                    return 3600 * 20
            }
            return field
        }

    /**
     * The properties of the test item. Do not mutate this after this configuration has been passed to the [TestItemImpl]
     * constructor.
     */
    public val properties: Item.Settings = Item.Settings()
        .group(manager.itemGroup)
        .maxCount(maxCount)

    /**
     * Called when this item is right clicked.
     *
     * @see Item.onItemRightClick
     * @see Item.onItemUse
     * @see Item.itemInteractionForEntity
     */
    public var rightClick: SidedAction<RightClickContext> = SidedAction()

    /**
     * Called when the item is right clicked in the air (i.e. not on a block or entity).
     * @see Item.onItemRightClick
     */
    public var rightClickAir: SidedAction<RightClickContext> = SidedAction()

    /**
     * Called when the item is right clicked on a block.
     *
     * @see Item.onItemUse
     */
    public var rightClickBlock: SidedAction<RightClickBlockContext> = SidedAction()

    /**
     * Called each tick while the player is holding down the right mouse button.
     *
     * @see Item.onUsingTick
     */
    public var rightClickHold: SidedAction<RightClickHoldContext> = SidedAction()

    /**
     * Called when the player releases the right mouse button.
     *
     * @see Item.onPlayerStoppedUsing
     */
    public var rightClickRelease: SidedAction<RightClickReleaseContext> = SidedAction()

    /**
     * Called when this item is left-clicked on a block. If this callback exists the block will not be broken.
     * @see Item.onBlockStartBreak
     * @see BlockEvent.BreakEvent
     */
    public var leftClickBlock: SidedAction<LeftClickBlockContext> = SidedAction()

    /**
     * Called when this item is left-clicked on an entity. If this callback exists the entity will not take damage.
     * @see Item.onLeftClickEntity
     */
    public var leftClickEntity: SidedAction<LeftClickEntityContext> = SidedAction()

    /**
     * Called when this item is right-clicked on an entity. If this callback exists the entity will not receive a
     * click event.
     *
     * @see Item.itemInteractionForEntity
     */
    public var rightClickEntity: SidedAction<RightClickEntityContext> = SidedAction()

    /**
     * Called each tick when the item is in the player's inventory.
     *
     * @see Item.inventoryTick
     */
    public var inventoryTick: SidedAction<InventoryTickContext> = SidedAction()

    /**
     * Called each tick when the item is in the player's hand.
     *
     * @see Item.inventoryTick
     */
    public var tickInHand: SidedAction<InventoryTickContext> = SidedAction()

    internal val instance: TestItemImpl by lazy {
        TestItemImpl(this)
    }

    public data class RightClickContext(val world: World, val player: PlayerEntity, val hand: Hand): PlayerTestContext(player) {
        val stack: ItemStack = player.getStackInHand(hand)
    }

    public data class RightClickBlockContext(private val context: ItemUsageContext): PlayerTestContext(context.player!!) {
        val stack: ItemStack = context.stack

        val world: World = context.world
        val player: PlayerEntity = context.player!!
        val hand: Hand = context.hand
        val side: Direction = context.side
        val block: BlockPos = context.blockPos
        val hitVec: Vec3d = context.hitPos
    }

    public data class RightClickHoldContext(val stack: ItemStack, val player: PlayerEntity, val timeLeft: Int): PlayerTestContext(player)
    public data class RightClickReleaseContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val timeLeft: Int): PlayerTestContext(player)
    public data class LeftClickBlockContext(val stack: ItemStack, val pos: BlockPos, val player: PlayerEntity): PlayerTestContext(player)
    public data class LeftClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val entity: Entity): PlayerTestContext(player)
    public data class RightClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val target: LivingEntity, val hand: Hand): PlayerTestContext(player)
    public data class InventoryTickContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val itemSlot: Int, val isSelected: Boolean): PlayerTestContext(player)

    override fun registerCommon(resources: TestModResourceManager) {
        Registry.register(Registry.ITEM, id, instance)
        resources.lang.item(id, name)
        description?.also {
            resources.lang.item(id.append(".tooltip"), it)
        }
    }

    override fun registerClient(resources: TestModResourceManager) {
        val testModel = TestItemModel(id)
        resources.arrp.addModel(
            testModel.model,
            Identifier(id.namespace, "item/${id.path}")
        )
        ColorProviderRegistry.ITEM.register(testModel.colorProvider, instance)
    }


}
