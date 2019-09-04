package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.ModLoadingContext

/**
 * The DSL for configuring an item
 */
@TestObjectDslMarker
class TestItemConfig(val id: String, val name: String): TestConfig() {
    constructor(id: String, name: String, block: TestItemConfig.() -> Unit): this(id, name) {
        this.block()
    }

    /**
     * Additional text to show in the item tooltip
     */
    var description: String? = null

    /**
     * The maximum stack size. Defaults to 1
     */
    var stackSize: Int = 1
    /**
     * How long the right click and hold action should last in ticks. Setting this to a non-negative value will cause
     * override the default behavior of returning one hour when either the [rightClickHold] or [rightClickRelease]
     * actions are enabled
     */
    var rightClickHoldDuration: Int = -1
        get() {
            if(field < 0) {
                if (rightClickHold.exists || rightClickRelease.exists)
                    return 3600 * 20
            }
            return field
        }

    /**
     * The properties of the test item. Do not mutate this after this configuration has been passed to the [TestItem]
     * constructor.
     */
    val properties: Item.Properties = Item.Properties()
        .group(LibrarianLibModule.current<TestMod>().itemGroup)
        .maxStackSize(stackSize)

    /**
     * Execute the passed block with this object as the receiver. Useful for using this object as a DSL
     */
    inline operator fun invoke(crossinline block: TestItemConfig.() -> Unit): TestItemConfig {
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
    var rightClick = Action<RightClickContext>()
    /**
     * Called when the item is right clicked in the air (i.e. not on a block or entity).
     * @see Item.onItemRightClick
     */
    var rightClickAir = Action<RightClickContext>()
    /**
     * Called when the item is right clicked on a block.
     *
     * @see Item.onItemUse
     */
    var rightClickBlock = Action<RightClickBlockContext>()

    /**
     * Called each tick while the player is holding down the right mouse button.
     *
     * @see Item.onUsingTick
     */
    var rightClickHold = Action<RightClickHoldContext>()
    /**
     * Called when the player releases the right mouse button.
     *
     * @see Item.onPlayerStoppedUsing
     */
    var rightClickRelease = Action<RightClickReleaseContext>()

    /**
     * Called when this item is left-clicked on a block. If this callback exists the block will not be broken.
     * @see Item.onBlockStartBreak
     * @see BlockEvent.BreakEvent
     */
    var leftClickBlock = Action<LeftClickBlockContext>()
    /**
     * Called when this item is left-clicked on an entity. If this callback exists the entity will not take damage.
     * @see Item.onLeftClickEntity
     */
    var leftClickEntity = Action<LeftClickEntityContext>()
    /**
     * Called when this item is right-clicked on an entity. If this callback exists the entity will not receive a
     * click event.
     *
     * @see Item.itemInteractionForEntity
     */
    var rightClickEntity = Action<RightClickEntityContext>()

    /**
     * Called each tick when the item is in the player's inventory.
     *
     * @see Item.inventoryTick
     */
    var inventoryTick = Action<InventoryTickContext>()
    /**
     * Called each tick when the item is in the player's hand.
     *
     * @see Item.inventoryTick
     */
    var tickInHand = Action<InventoryTickContext>()

    data class RightClickContext(val world: World, val player: PlayerEntity, val hand: Hand): PlayerTestItemContext(player) {
        val stack: ItemStack = player.getHeldItem(hand)
    }
    data class RightClickBlockContext(private val context: ItemUseContext): PlayerTestItemContext(context.player!!) {
        val stack: ItemStack = context.item

        val world: World = context.world
        val player: PlayerEntity = context.player!!
        val hand: Hand = context.hand
        val side: Direction = context.face
        val block: BlockPos = context.pos
        val hitVec: Vec3d = context.hitVec
    }
    data class RightClickHoldContext(val stack: ItemStack, val player: PlayerEntity, val count: Int): PlayerTestItemContext(player)
    data class RightClickReleaseContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val timeLeft: Int): PlayerTestItemContext(player)
    data class LeftClickBlockContext(val stack: ItemStack, val pos: BlockPos, val player: PlayerEntity): PlayerTestItemContext(player)
    data class LeftClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val entity: Entity): PlayerTestItemContext(player)
    data class RightClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val target: LivingEntity, val hand: Hand): PlayerTestItemContext(player)
    data class InventoryTickContext(val stack: ItemStack, val world: World, val player: PlayerEntity, val itemSlot: Int, val isSelected: Boolean): PlayerTestItemContext(player)
}
