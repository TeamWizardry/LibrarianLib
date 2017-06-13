package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleInventory(val handler: ItemStackHandler) : ITileModule {
    constructor() : this(ItemStackHandler())
    constructor(size: Int) : this(ItemStackHandler(size))
    constructor(stacks: NonNullList<ItemStack>) : this(ItemStackHandler(stacks))

    fun disallowSides(vararg sides: EnumFacing?) = apply { allowedSides.removeAll { it in sides } }

    private val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    override fun readFromNBT(compound: NBTTagCompound) = handler.deserializeNBT(compound)
    override fun writeToNBT(sync: Boolean): NBTTagCompound = handler.serializeNBT()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing in allowedSides) handler as T else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing in allowedSides
    }

    override fun onBreak(tile: TileMod) {
        (0 until handler.slots)
                .map { handler.getStackInSlot(it) }
                .filterNot { it.isEmpty }
                .forEach { InventoryHelper.spawnItemStack(tile.world, tile.pos.x.toDouble(), tile.pos.y.toDouble(), tile.pos.z.toDouble(), it) }
    }
}
