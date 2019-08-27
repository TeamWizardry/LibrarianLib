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
class TestItemConfig(val id: String, val name: String) {
    val modid: String = ModLoadingContext.get().activeContainer.modId

    constructor(id: String, name: String, block: TestItemConfig.() -> Unit): this(id, name) {
        this.block()
    }

    var stackSize: Int = 1
    var rightClickHoldDuration: Int = 0

    val properties: Item.Properties = Item.Properties()
        .group(LibrarianLibModule.current<TestMod>().itemGroup)
        .maxStackSize(stackSize)

    val server = Actions(this)
    val client = Actions(this)
    val common = Actions(this)

    inline operator fun invoke(crossinline block: TestItemConfig.() -> Unit): TestItemConfig {
        this.block()
        return this
    }

    /**
     * A set of actions to be performed on a particular side
     */
    @TestObjectDslMarker
    class Actions internal constructor(private val config: TestItemConfig) {
        inline operator fun invoke(crossinline block: Actions.() -> Unit): Actions {
            this.block()
            return this
        }

        internal var rightClick: (RightClickContext.() -> Unit)? = null
        internal var rightClickAir: (RightClickContext.() -> Unit)? = null
        internal var rightClickBlock: (RightClickBlockContext.() -> Unit)? = null

        internal var rightClickHold: (RightClickHoldContext.() -> Unit)? = null
        internal var rightClickRelease: (RightClickReleaseContext.() -> Unit)? = null

        internal var leftClickBlock: (LeftClickBlockContext.() -> Unit)? = null
        internal var leftClickEntity: (LeftClickEntityContext.() -> Unit)? = null
        internal var rightClickEntity: (RightClickEntityContext.() -> Unit)? = null

        internal var inventoryTick: (InventoryTickContext.() -> Unit)? = null
        internal var tickInHand: (InventoryTickContext.() -> Unit)? = null

        /**
         * Called when this item is right clicked.
         *
         * @see Item.onItemRightClick
         * @see Item.onItemUse
         * @see Item.itemInteractionForEntity
         */
        fun rightClick(action: RightClickContext.() -> Unit) {
            this.rightClick = action
        }
        /**
         * Called when the item is right clicked in the air (i.e. not on a block or entity).
         * @see Item.onItemRightClick
         */
        fun rightClickAir(action: RightClickContext.() -> Unit) {
            this.rightClickAir = action
        }
        /**
         * Called when the item is right clicked on a block.
         *
         * @see Item.onItemUse
         */
        fun rightClickBlock(action: RightClickBlockContext.() -> Unit) {
            this.rightClickBlock = action
        }

        /**
         * Called each tick while the player is holding down the right mouse button.
         *
         * @see Item.onUsingTick
         */
        fun rightClickHold(action: RightClickHoldContext.() -> Unit) {
            if(config.rightClickHoldDuration == 0)
                config.rightClickHoldDuration = 3600 * 20
            this.rightClickHold = action
        }
        /**
         * Called when the player releases the right mouse button.
         *
         * @see Item.onPlayerStoppedUsing
         */
        fun rightClickRelease(action: RightClickReleaseContext.() -> Unit) {
            if(config.rightClickHoldDuration == 0)
                config.rightClickHoldDuration = 3600 * 20
            this.rightClickRelease = action
        }

        /**
         * Called when this item is left-clicked on a block. If this callback exists the block will not be broken.
         * @see Item.onBlockStartBreak
         * @see BlockEvent.BreakEvent
         */
        fun leftClickBlock(action: LeftClickBlockContext.() -> Unit) {
            this.leftClickBlock = action
        }
        /**
         * Called when this item is left-clicked on an entity. If this callback exists the entity will not take damage.
         * @see Item.onLeftClickEntity
         */
        fun leftClickEntity(action: LeftClickEntityContext.() -> Unit) {
            this.leftClickEntity = action
        }
        /**
         * Called when this item is right-clicked on an entity. If this callback exists the entity will not receive a
         * click event.
         *
         * @see Item.itemInteractionForEntity
         */
        fun rightClickEntity(action: RightClickEntityContext.() -> Unit) {
            this.rightClickEntity = action
        }

        /**
         * Called each tick when the item is in the player's inventory.
         *
         * @see Item.inventoryTick
         */
        fun inventoryTick(action: InventoryTickContext.() -> Unit) {
            this.inventoryTick = action
        }
        /**
         * Called each tick when the item is in the player's hand.
         *
         * @see Item.inventoryTick
         */
        fun tickInHand(action: InventoryTickContext.() -> Unit) {
            this.tickInHand = action
        }

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
}
