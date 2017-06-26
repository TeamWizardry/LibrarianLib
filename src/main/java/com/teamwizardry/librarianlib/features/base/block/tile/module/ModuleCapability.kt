package com.teamwizardry.librarianlib.features.base.block.tile.module

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
open class ModuleCapability<CAP : INBTSerializable<NBTTagCompound>>(val capability: Capability<in CAP>, val handler: CAP) : ITileModule {

    fun disallowSides(vararg sides: EnumFacing?) = apply { allowedSides.removeAll { it in sides } }
    fun setSides(vararg sides: EnumFacing?) = apply { allowedSides.clear(); allowedSides.addAll(sides) }

    protected val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    override fun readFromNBT(compound: NBTTagCompound) = handler.deserializeNBT(compound)
    override fun writeToNBT(sync: Boolean): NBTTagCompound = handler.serializeNBT()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == capability && facing in allowedSides) handler as T else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == capability && facing in allowedSides
    }
}
