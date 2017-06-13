package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleFluid(val handler: FluidTank) : ITileModule {
    constructor(capacity: Int) : this(FluidTank(capacity))
    constructor(stack: FluidStack, capacity: Int) : this(FluidTank(stack, capacity))
    constructor(fluid: Fluid, amount: Int, capacity: Int) : this(FluidTank(fluid, amount, capacity))

    fun disallowSides(vararg sides: EnumFacing?) = apply { allowedSides.removeAll { it in sides } }

    override fun onClicked(tile: TileMod, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (side !in allowedSides) return false
        val stack = player.getHeldItem(hand)
        val result = FluidUtil.interactWithFluidHandler(stack, handler, player)
        if (result.isSuccess)
            player.setHeldItem(hand, result.result)
        return FluidUtil.getFluidHandler(stack) != null
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
