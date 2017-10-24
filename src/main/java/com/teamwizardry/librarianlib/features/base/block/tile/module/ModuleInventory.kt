package com.teamwizardry.librarianlib.features.base.block.tile.module

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleInventory(handler: ItemStackHandler) : ModuleCapability<ItemStackHandler>(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handler) {
    constructor() : this(ItemStackHandler())
    constructor(size: Int) : this(ItemStackHandler(size))
    constructor(stacks: NonNullList<ItemStack>) : this(ItemStackHandler(stacks))

    companion object {
        fun getPowerLevel(percent: Float) = (percent * 15).toInt() + if (percent > 0.0) 1 else 0

        fun getPowerLevel(capability: IItemHandler): Float {
            var percent = 0f
            for (i in 0 until capability.slots) {
                val inSlot = capability.getStackInSlot(i)
                percent += inSlot.count.toFloat() / getMaxStackSize(i, capability, inSlot)
            }
            return percent / capability.slots
        }

        private fun getMaxStackSize(slot: Int, handler: IItemHandler, inSlot: ItemStack?): Int {
            if (inSlot == null || inSlot.isEmpty) return 64
            val stack = inSlot.copy()
            stack.count = inSlot.maxStackSize - inSlot.count
            val result = handler.insertItem(slot, stack, true)
            return inSlot.maxStackSize - result.count
        }
    }

    override fun onBreak(tile: TileMod) {
        (0 until handler.slots)
                .map { handler.getStackInSlot(it) }
                .filterNot { it.isEmpty }
                .forEach { InventoryHelper.spawnItemStack(tile.world, tile.pos.x.toDouble(), tile.pos.y.toDouble(), tile.pos.z.toDouble(), it) }
    }

    override fun hasComparatorOutput() = true
    override fun getComparatorOutput(tile: TileMod) = getPowerLevel(handler)
}
