package com.teamwizardry.librarianlib.common.base.capability

import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

/**
 * Created by Elad on 1/21/2017.
 */
@SaveInPlace
abstract class CapabilityMod(val name: ResourceLocation) {

    val capability: Capability<*> by lazy { capabilities[javaClass]?.invoke() ?: throw InvalidPropertiesFormatException("Invalid class!") }

    fun attach(event: AttachCapabilitiesEvent<*>) {
        event.addCapability(name, CapabilityModSerializer())
    }

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun onDeath(playerCloneEvent: PlayerEvent.Clone) {
            if (playerCloneEvent.isWasDeath)
                for (cap in capabilities.values)
                    (playerCloneEvent.entityPlayer.getCapability(cap(), null) as CapabilityMod)
                            .readFromNBT((playerCloneEvent.original.getCapability(cap(), null) as CapabilityMod).writeToNBT(NBTTagCompound()))
        }

        fun <T : CapabilityMod> register(capClass: Class<T>, capObj: () -> Capability<T>) {
            CapabilityManager.INSTANCE.register(capClass, object : Capability.IStorage<T> {
                override fun writeNBT(capability: Capability<T>, instance: T, side: EnumFacing?): NBTBase {
                    return instance.writeToNBT(NBTTagCompound())
                }

                override fun readNBT(capability: Capability<T>, instance: T, side: EnumFacing?, nbt: NBTBase?) {
                    instance.readFromNBT(nbt as NBTTagCompound)
                }

            }, capClass)
            registeredClasses.add(capClass)
            capabilities.put(capClass, capObj)
        }
    }

    open fun writeToCustomNbt(nbtTagCompound: NBTTagCompound) {
    }

    open fun readFromCustomNbt(nbtTagCompound: NBTTagCompound) {
    }

    fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, false))
        writeToCustomNbt(compound)
        return compound
    }

    fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getTag("auto"), false)
        readFromCustomNbt(compound)
    }

    protected open inner class CapabilityModSerializer : ICapabilitySerializable<NBTTagCompound> {
        override fun serializeNBT(): NBTTagCompound {
            return writeToNBT(NBTTagCompound())
        }

        override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
            return if (capability == this@CapabilityMod.capability) this as T else null
        }

        override fun deserializeNBT(nbt: NBTTagCompound) {
            readFromNBT(nbt)
        }

        override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
            return capability == this@CapabilityMod.capability
        }

    }
}

val registeredClasses: HashSet<Class<*>> = hashSetOf()
internal val capabilities: HashMap<Class<*>, () -> Capability<*>> = hashMapOf()