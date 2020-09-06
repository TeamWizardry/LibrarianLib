package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.event.world.BlockEvent

/**
 * The DSL for configuring an item
 */
@TestObjectDslMarker
public class TestItemConfig(public val id: String, public val name: String, group: ItemGroup): TestConfig() {
    public constructor(id: String, name: String, group: ItemGroup, block: TestItemConfig.() -> Unit): this(id, name, group) {
        this.block()
    }

    /**
     * The maximum stack size. Defaults to 1
     */
    public var stackSize: Int = 1

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
     * The properties of the test item. Do not mutate this after this configuration has been passed to the [TestItem]
     * constructor.
     */
    public val properties: Item.Properties = Item.Properties()
        .group(group)
        .maxStackSize(stackSize)

    /**
     * Execute the passed block with this object as the receiver. Useful for using this object as a DSL
     */
    public inline operator fun invoke(crossinline block: TestItemConfig.() -> Unit): TestItemConfig {
        this.block()
        return this
    }

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

    public data class RightClickContext(val world: World, val player: PlayerEntity, val hand: Hand): PlayerTestContext(player) {
        val stack: ItemStack = player.getHeldItem(hand)
    }

    public data class RightClickBlockContext(private val context: ItemUseContext): PlayerTestContext(context.player!!) {
        val stack: ItemStack = context.item

        val world: World = context.world
        val player: PlayerEntity = context.player!!
        val hand: Hand = context.hand
        val side: Direction = context.face
        val block: BlockPos = context.pos
        val hitVec: Vec3d = context.hitVec
    }

    public data class RightClickHoldContext(val stack: ItemStack, val player: PlayerEntity, val count: Int): PlayerTestContext(player)
    public data class RightClickReleaseContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val timeLeft: Int): PlayerTestContext(player)
    public data class LeftClickBlockContext(val stack: ItemStack, val pos: BlockPos, val player: PlayerEntity): PlayerTestContext(player)
    public data class LeftClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val entity: Entity): PlayerTestContext(player)
    public data class RightClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val target: LivingEntity, val hand: Hand): PlayerTestContext(player)
    public data class InventoryTickContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val itemSlot: Int, val isSelected: Boolean): PlayerTestContext(player)
}
