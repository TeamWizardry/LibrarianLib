package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

@TestItemDslMarker
class TestItemConfig(val name: String) {
    val modid: String = ModLoadingContext.get().activeContainer.modId

    constructor(name: String, block: TestItemConfig.() -> Unit): this(name) {
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

    class Actions(private val config: TestItemConfig) {
        /**
         * Called in [Item.onItemRightClick] and [Item.onItemUse]
         */
        var rightClick: (RightClickContext.() -> Unit)? = null
        /**
         * Called in [Item.onItemRightClick]
         */
        var rightClickAir: (RightClickContext.() -> Unit)? = null
        data class RightClickContext(val world: World, val player: PlayerEntity, val hand: Hand)
        /**
         * Called in [Item.onItemUse]
         */
        var rightClickBlock: (RightClickBlockContext.() -> Unit)? = null
        data class RightClickBlockContext(val context: ItemUseContext) {
            val world: World = context.world
            val player: PlayerEntity = context.player!!
            val hand: Hand = context.hand
        }

        /**
         * Called in [Item.onUsingTick]
         */
        var rightClickHold: (RightClickHoldContext.() -> Unit)? = null
        data class RightClickHoldContext(val stack: ItemStack, val player: LivingEntity, val count: Int)
        /**
         * Called in [Item.onPlayerStoppedUsing]
         */
        var rightClickRelease: (RightClickReleaseContext.() -> Unit)? = null
        data class RightClickReleaseContext(val stack: ItemStack, val world: World, val entityLiving: LivingEntity, val timeLeft: Int)

        /**
         * Called in [Item.onLeftClickEntity]
         */
        var leftClickEntity: (LeftClickEntityContext.() -> Unit)? = null
        data class LeftClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val entity: Entity)
        /**
         * Called in [Item.itemInteractionForEntity]
         */
        var rightClickEntity: (RightClickEntityContext.() -> Unit)? = null
        data class RightClickEntityContext(val stack: ItemStack, val player: PlayerEntity, val target: LivingEntity, val hand: Hand)

        /**
         * Called in [Item.inventoryTick]
         */
        var inventoryTick: (InventoryTickContext.() -> Unit)? = null
        data class InventoryTickContext(val stack: ItemStack, val world: World, val entity: Entity, val itemSlot: Int, val isSelected: Boolean)


        fun rightClick(action: RightClickContext.() -> Unit) {
            this.rightClick = action
        }
        fun rightClickAir(action: RightClickContext.() -> Unit) {
            this.rightClickAir = action
        }
        fun rightClickBlock(action: RightClickBlockContext.() -> Unit) {
            this.rightClickBlock = action
        }

        fun rightClickHold(action: RightClickHoldContext.() -> Unit) {
            if(config.rightClickHoldDuration == 0)
                config.rightClickHoldDuration = 3600 * 20
            this.rightClickHold = action
        }
        fun rightClickRelease(action: RightClickReleaseContext.() -> Unit) {
            if(config.rightClickHoldDuration == 0)
                config.rightClickHoldDuration = 3600 * 20
            this.rightClickRelease = action
        }

        fun leftClickEntity(action: LeftClickEntityContext.() -> Unit) {
            this.leftClickEntity = action
        }
        fun rightClickEntity(action: RightClickEntityContext.() -> Unit) {
            this.rightClickEntity = action
        }

        fun inventoryTick(action: InventoryTickContext.() -> Unit) {
            this.inventoryTick = action
        }
    }
}

@DslMarker
annotation class TestItemDslMarker
