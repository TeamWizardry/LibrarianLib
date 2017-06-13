package com.teamwizardry.librarianlib.features.base.block.module

import com.teamwizardry.librarianlib.features.kotlin.JSON.obj
import com.teamwizardry.librarianlib.features.kotlin.nbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

/**
 * @author WireSegal
 * Created at 10:41 AM on 6/13/17.
 */
class ModuleEnergy(val handler: MutableEnergyStorage) : ITileModule {
    constructor(capacity: Int) : this(MutableEnergyStorage(capacity))
    constructor(capacity: Int, maxTransfer: Int) : this(MutableEnergyStorage(capacity, maxTransfer))
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int) : this(MutableEnergyStorage(capacity, maxReceive, maxExtract))
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int, energy: Int) : this(MutableEnergyStorage(capacity, maxReceive, maxExtract, energy))

    fun disallowSides(vararg sides: EnumFacing?): ModuleEnergy {
        allowedSides.removeAll { it in sides }
        return this
    }

    private val allowedSides = mutableSetOf(*EnumFacing.VALUES, null)

    override fun readFromNBT(compound: NBTTagCompound) { handler.deserializeNBT(compound) }
    override fun writeToNBT(sync: Boolean): NBTTagCompound = handler.serializeNBT()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == CapabilityEnergy.ENERGY && facing in allowedSides) handler as T else null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == CapabilityEnergy.ENERGY && facing in allowedSides
    }
}


open class MutableEnergyStorage @JvmOverloads constructor(capacity: Int, maxIn: Int = capacity, maxOut: Int = maxIn, energy: Int = 0): EnergyStorage(capacity, maxIn, maxOut, energy), INBTSerializable<NBTTagCompound> {
    fun setEnergy(energy: Int) {
        this.energy = energy
    }

    var receiveRate: Int
        get() = maxReceive
        set(value) { maxReceive = value }

    var extractRate: Int
        get() = maxExtract
        set(value) { maxExtract = value }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        energy = nbt.getInteger("energy")
        maxExtract = nbt.getInteger("extract")
        maxReceive = nbt.getInteger("receive")
        capacity = nbt.getInteger("capacity")
    }

    override fun serializeNBT(): NBTTagCompound {
        return nbt {
            comp(
                    "energy" to energy,
                    "extract" to maxExtract,
                    "receive" to maxReceive,
                    "capacity" to capacity
            )
        } as NBTTagCompound
    }
}
