package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.base.block.TileMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * @author WireSegal
 * Created at 9:59 AM on 6/13/17.
 */
interface ITileModule : ICapabilityProvider {
    fun readFromNBT(compound: NBTTagCompound)
    fun writeToNBT(sync: Boolean): NBTTagCompound

    fun onLoad(tile: TileMod) = Unit
    fun onBreak(tile: TileMod) = Unit
    fun onUpdate(tile: TileMod) = Unit
    fun onClicked(tile: TileMod, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = false

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? = null
    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) = false
}
