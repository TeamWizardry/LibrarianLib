package com.teamwizardry.librarianlib.features.base.block.tile.module

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.EmptyHandler
import net.minecraftforge.items.wrapper.RangedWrapper

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleSidedInventory(handler: ItemStackHandler, private val map: MutableMap<EnumFacing, IItemHandlerModifiable> = mutableMapOf()) :
        ModuleMappedCapability<ItemStackHandler>(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handler, { map.getOrPut(it, ::EmptyHandler) }) {
    constructor() : this(ItemStackHandler())
    constructor(size: Int) : this(ItemStackHandler(size))
    constructor(stacks: NonNullList<ItemStack>) : this(ItemStackHandler(stacks))

    fun configure(vararg sides: EnumFacing, slots: IntRange) =
            configure(*sides, predicate = slots::contains)
    fun configure(vararg sides: EnumFacing, predicate: (Int) -> Boolean) =
            configure(*sides, handler = PredicatedWrapper(handler, predicate))
    fun configure(vararg sides: EnumFacing, min: Int, maxInclusive: Int) =
            configure(*sides, handler = RangedWrapper(handler, min, maxInclusive))
    fun configure(vararg sides: EnumFacing, handler: IItemHandlerModifiable) =
            apply { sides.forEach { map[it] = handler } }

    override fun onBreak(tile: TileMod) {
        (0 until handler.slots)
                .map { handler.getStackInSlot(it) }
                .filterNot { it.isEmpty }
                .forEach { InventoryHelper.spawnItemStack(tile.world, tile.pos.x.toDouble(), tile.pos.y.toDouble(), tile.pos.z.toDouble(), it) }
    }

    override fun hasComparatorOutput() = true
    override fun getComparatorOutput(tile: TileMod) = ItemHandlerHelper.calcRedstoneFromInventory(handler) / 15f
}

private class PredicatedWrapper(val master: IItemHandlerModifiable, predicate: (Int) -> Boolean) : IItemHandlerModifiable {

    private val slots = (0 until master.slots).filter(predicate)

    override fun getSlots() = slots.size

    override fun getStackInSlot(slot: Int): ItemStack =
            if (checkSlot(slot)) master.getStackInSlot(slots[slot]) else ItemStack.EMPTY

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
            if (checkSlot(slot)) master.insertItem(slots[slot], stack, simulate) else stack

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
            if (checkSlot(slot)) master.extractItem(slots[slot], amount, simulate) else ItemStack.EMPTY

    override fun setStackInSlot(slot: Int, stack: ItemStack) =
            if (checkSlot(slot)) master.setStackInSlot(slots[slot], stack) else Unit

    override fun getSlotLimit(slot: Int): Int =
            if (checkSlot(slot)) master.getSlotLimit(slots[slot]) else 0

    private fun checkSlot(localSlot: Int): Boolean =
            localSlot < slots.size && slots[localSlot] < master.slots
}
