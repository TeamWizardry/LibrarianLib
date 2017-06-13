package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.common.capabilities.Capability
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
open class ModuleCapability<CAP : INBTSerializable<NBTTagCompound>>(val capability: Capability<in CAP>, val handler: CAP) : ITileModule {

    fun disallowSides(vararg sides: EnumFacing?) = apply { allowedSides.removeAll { it in sides } }

    protected val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    override fun readFromNBT(compound: NBTTagCompound) { handler.deserializeNBT(compound) }
    override fun writeToNBT(sync: Boolean): NBTTagCompound = handler.serializeNBT()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == capability && facing in allowedSides) handler as T else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == capability && facing in allowedSides
    }
}
