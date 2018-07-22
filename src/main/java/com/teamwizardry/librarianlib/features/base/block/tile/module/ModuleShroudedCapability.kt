package com.teamwizardry.librarianlib.features.base.block.tile.module

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable

/**
 * @author WireSegal
 * Created at 9:27 AM on 7/22/18.
 */
open class ModuleShroudedCapability<CAP : INBTSerializable<NBTTagCompound>>(capability: Capability<in CAP>, central: CAP, private val shroud: CAP.(EnumFacing) -> Any) : ModuleCapability<CAP>(capability, central) {

    protected open fun map(facing: EnumFacing) = handler.shroud(facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == this.capability && facing in allowedSides) {
            if (facing == null)
                super.getCapability(capability, facing)
            else
                map(facing) as T
        } else
            return null
    }
}

open class ModuleMappedCapability<CAP: INBTSerializable<NBTTagCompound>>(capability: Capability<in CAP>, central: CAP, shroud: CAP.(EnumFacing) -> Any) : ModuleShroudedCapability<CAP>(capability, central, shroud) {
    private val shrouds = mutableMapOf<EnumFacing, Any>()

    override fun map(facing: EnumFacing) = shrouds.getOrPut(facing) { super.map(facing) }
}
