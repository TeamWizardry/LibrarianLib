package com.teamwizardry.librarianlib.features.base.block.tile.module

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate
import kotlin.math.min

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleMultiFluid(handler: SerializableFluidConcatenator) :
        ModuleCapability<SerializableFluidConcatenator>(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, handler) {
    constructor(vararg capacities: Int) : this(SerializableFluidConcatenator(*capacities))
    constructor(vararg fluidCapacities: FluidStack) : this(SerializableFluidConcatenator(*fluidCapacities))


    override fun onClicked(tile: TileMod, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (side !in allowedSides) return false
        val stack = player.getHeldItem(hand)
        FluidUtil.interactWithFluidHandler(player, hand, player.world, tile.pos, side)
        return FluidUtil.getFluidHandler(stack) != null
    }

    override fun hasComparatorOutput() = true
    override fun getComparatorOutput(tile: TileMod) = handler.amount.toFloat() / handler.capacity
}

open class SerializableFluidConcatenator(vararg val tanks: FluidTank) : FluidHandlerConcatenate(*tanks), INBTSerializable<NBTTagCompound> {
    constructor(collection: Collection<FluidTank>) : this(*collection.toTypedArray())
    constructor(vararg capacities: Int) : this(capacities.map { FluidTank(it) })
    constructor(vararg fluidCapacities: FluidStack) : this(fluidCapacities.map { ChokedFluidTank(it.fluid, it.amount) })

    private val specialTanks = tanks.filterIsInstance<ChokedFluidTank>()
    private val normalTanks = tanks.filterNot { it in specialTanks }

    val amount get() = tanks.sumBy { it.fluidAmount }
    val capacity get() = tanks.sumBy { it.capacity }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (normalTanks.isNotEmpty()) {
            val list = nbt.getTagList("UnkeyedFluidTanks", Constants.NBT.TAG_COMPOUND)
            for (i in 0 until min(list.tagCount(), normalTanks.size))
                normalTanks[i].readFromNBT(list.getCompoundTagAt(i))
        }
        for (tank in specialTanks)
            tank.readFromNBT(nbt.getCompoundTag(tank.fluidType.name))
    }

    override fun serializeNBT(): NBTTagCompound {
        val out = NBTTagCompound()
        if (normalTanks.isNotEmpty()) {
            val list = NBTTagList()
            for (tank in normalTanks)
                list.appendTag(tank.writeToNBT(NBTTagCompound()))
            out.setTag("UnkeyedFluidTanks", list)
        }

        for (tank in specialTanks)
            out.setTag(tank.fluidType.name, tank.writeToNBT(NBTTagCompound()))

        return out
    }
}

open class ChokedFluidTank(val fluidType: Fluid, capacity: Int) : FluidTank(capacity) {
    override fun canDrainFluidType(fluid: FluidStack?): Boolean {
        return super.canDrainFluidType(fluid) && fluid != null && fluid.fluid.name == fluidType.name
    }

    override fun canFillFluidType(fluid: FluidStack?): Boolean {
        return super.canFillFluidType(fluid) && fluid != null && fluid.fluid.name == fluidType.name
    }
}
