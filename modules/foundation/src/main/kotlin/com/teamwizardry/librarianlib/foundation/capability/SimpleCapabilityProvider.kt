package com.teamwizardry.librarianlib.foundation.capability

import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

/**
 * A simple capability provider that just returns and serializes an instance. For one that is direction-dependent, see
 * [SimpleDirectionalCapabilityProvider]
 */
public class SimpleCapabilityProvider<T: INBTSerializable<N>, N: INBT>(
    private val capability: Capability<T>,
    private val instance: T
): ICapabilitySerializable<N> {
    private val optInstance = LazyOptional.of { instance }

    override fun serializeNBT(): N {
        return instance.serializeNBT()
    }

    override fun deserializeNBT(nbt: N) {
        instance.deserializeNBT(nbt)
    }

    override fun <T: Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if(cap == capability)
            return optInstance.cast()
        return LazyOptional.empty()
    }
}