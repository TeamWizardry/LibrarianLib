package com.teamwizardry.librarianlib.features.saving

import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by TheCodeWarrior
 */
object AbstractSaveHandler {
    val nbtCache = mutableMapOf<Class<*>, Serializer<Any>>()
            .withRealDefault { SerializerRegistry.getOrCreate(FieldType.create(it)) }
    val byteCache = mutableMapOf<Class<*>, Serializer<Any>>()
            .withRealDefault { SerializerRegistry.getOrCreate(FieldType.create(it)) }

    @JvmStatic
    fun writeAutoNBT(instance: Any, sync: Boolean): NBTBase {
        return nbtCache[instance.javaClass].write(instance, sync)
    }

    @JvmStatic
    fun readAutoNBT(instance: Any, tag: NBTBase, sync: Boolean): Any {
        return nbtCache[instance.javaClass].read(tag, instance, sync)
    }

    @JvmStatic
    fun writeAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean) {
        byteCache[instance.javaClass].write(buf, instance, sync)
    }

    @JvmStatic
    fun readAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean): Any {
        return byteCache[instance.javaClass].read(buf, instance, sync)
    }

    @JvmStatic
    fun hasCapability(instance: Any, cap: Capability<*>, side: EnumFacing?): Boolean {
        return SavingFieldCache.getClassFields(FieldType.create(instance.javaClass)).any { it.value.hasCapability(cap, side) }
    }

    @JvmStatic
    fun <T : Any> getCapability(instance: Any, cap: Capability<T>, side: EnumFacing?): T? {
        for ((_, value) in SavingFieldCache.getClassFields(FieldType.create(instance.javaClass))) {
            val inst = value.getCapability(instance, cap, side)
            if (inst != null) return inst
        }
        return null
    }

    @JvmStatic
    fun cacheFields(clazz: Class<*>) {
        SavingFieldCache.getClassFields(FieldType.create(clazz))
    }
}
