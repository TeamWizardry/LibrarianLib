package com.teamwizardry.librarianlib.features.base.block.tile.module

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleFluid(handler: SerializableFluidTank) : ModuleCapability<ModuleFluid.SerializableFluidTank>(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, handler) {
    constructor(capacity: Int) : this(SerializableFluidTank(capacity))
    constructor(stack: FluidStack, capacity: Int) : this(SerializableFluidTank(stack, capacity))
    constructor(fluid: Fluid, amount: Int, capacity: Int) : this(SerializableFluidTank(fluid, amount, capacity))

    override fun onClicked(tile: TileMod, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (side !in allowedSides) return false
        val stack = player.getHeldItem(hand)
        FluidUtil.interactWithFluidHandler(player, hand, player.world, tile.pos, side)
        return FluidUtil.getFluidHandler(stack) != null
    }

    open class SerializableFluidTank : FluidTank, INBTSerializable<NBTTagCompound> {
        constructor(capacity: Int) : super(capacity)
        constructor(stack: FluidStack, capacity: Int) : super(stack, capacity)
        constructor(fluid: Fluid, amount: Int, capacity: Int) : super(fluid, amount, capacity)

        override fun deserializeNBT(nbt: NBTTagCompound) { readFromNBT(nbt) }
        override fun serializeNBT(): NBTTagCompound = writeToNBT(NBTTagCompound())
    }

    override fun hasComparatorOutput() = true
    override fun getComparatorOutput(tile: TileMod) = (handler.fluid?.amount?.toFloat() ?: 0f) / handler.capacity
}
