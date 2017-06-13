package com.teamwizardry.librarianlib.features.base.block

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * @author WireSegal
 * Created at 9:59 AM on 6/13/17.
 */
interface ITileModule : ICapabilityProvider {
    fun readFromNBT(compound: NBTTagCompound)
    fun writeToNBT(compound: NBTTagCompound, sync: Boolean)

    fun onLoad(tile: TileMod) = Unit
    fun onBreak(tile: TileMod) = Unit
    fun onUpdate(tile: TileMod) = Unit

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?) = null
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) = false
}
