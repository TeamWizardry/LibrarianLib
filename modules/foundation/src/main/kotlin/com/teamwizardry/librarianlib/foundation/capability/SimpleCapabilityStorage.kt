package com.teamwizardry.librarianlib.foundation.capability

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable
import java.lang.IllegalArgumentException

/**
 * A simple capability storage class that just delegates to INBTSerializable. If the capability instance isn't
 * INBTSerializable, this throws an exception
 */
public class SimpleCapabilityStorage<T: Any>: Capability.IStorage<T> {
    override fun writeNBT(capability: Capability<T>, instance: T, side: Direction?): INBT {
        if(instance is INBTSerializable<*>)
            return instance.serializeNBT()
        throw IllegalArgumentException("Capability instance of type ${instance.javaClass.simpleName} doesn't " +
                "implement INBTSerializable, so it can't use the default capability storage.")
    }

    override fun readNBT(capability: Capability<T>, instance: T, side: Direction?, nbt: INBT) {
        @Suppress("UNCHECKED_CAST")
        if(instance is INBTSerializable<*>)
            (instance as INBTSerializable<INBT>).deserializeNBT(nbt)
        throw IllegalArgumentException("Capability instance of type ${instance.javaClass.simpleName} doesn't " +
                "implement INBTSerializable, so it can't use the default capability storage.")
    }
}