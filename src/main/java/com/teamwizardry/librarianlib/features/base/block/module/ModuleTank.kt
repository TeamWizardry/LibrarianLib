package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.util.*

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleTank(val handler: FluidTank) : ITileModule {
    constructor(capacity: Int) : this(FluidTank(capacity))
    constructor(stack: FluidStack, capacity: Int) : this(FluidTank(stack, capacity))
    constructor(fluid: Fluid, amount: Int, capacity: Int) : this(FluidTank(fluid, amount, capacity))

    fun disallowSides(vararg sides: EnumFacing?): ModuleTank {
        allowedSides.removeAll { it in sides }
        return this
    }

    private val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    override fun readFromNBT(compound: NBTTagCompound) { handler.readFromNBT(compound) }
    override fun writeToNBT(sync: Boolean): NBTTagCompound = handler.writeToNBT(NBTTagCompound())

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing in allowedSides) handler as T else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing in allowedSides
    }
}
