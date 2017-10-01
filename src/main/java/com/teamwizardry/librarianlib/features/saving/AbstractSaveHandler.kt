package com.teamwizardry.librarianlib.features.saving

import com.google.common.reflect.TypeToken
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
    val classCache = mutableMapOf<Class<*>, Serializer<Any>>()
            .withRealDefault { SerializerRegistry.getOrCreate(FieldType.create(it)) }
    val tokenCache = mutableMapOf<TypeToken<*>, Serializer<Any>>()
            .withRealDefault { SerializerRegistry.getOrCreate(FieldType.create(it)) }

    // NBT =============================================================================================================

    @JvmStatic
    fun writeAutoNBT(instance: Any, sync: Boolean): NBTBase {
        return classCache[instance.javaClass].write(instance, sync)
    }

    @JvmStatic
    fun writeAutoNBTByToken(token: TypeToken<*>, instance: Any, sync: Boolean): NBTBase {
        return tokenCache[token].write(instance, sync)
    }

    @JvmStatic
    fun readAutoNBT(instance: Any, tag: NBTBase, sync: Boolean): Any {
        return classCache[instance.javaClass].read(tag, instance, sync)
    }

    @JvmStatic
    fun readAutoNBTByClass(clazz: Class<*>, tag: NBTBase, sync: Boolean): Any {
        return classCache[clazz].read(tag, null, sync)
    }

    @JvmStatic
    fun readAutoNBTByToken(token: TypeToken<*>, tag: NBTBase, sync: Boolean): Any {
        return tokenCache[token].read(tag, null, sync)
    }

    // Bytes ===========================================================================================================

    @JvmStatic
    fun writeAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean) {
        classCache[instance.javaClass].write(buf, instance, sync)
    }

    @JvmStatic
    fun writeAutoBytesByToken(token: TypeToken<*>, instance: Any, buf: ByteBuf, sync: Boolean) {
        tokenCache[token].write(buf, instance, sync)
    }

    @JvmStatic
    fun readAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean): Any {
        return classCache[instance.javaClass].read(buf, instance, sync)
    }

    @JvmStatic
    fun readAutoBytesByClass(clazz: Class<*>, buf: ByteBuf, sync: Boolean): Any {
        return classCache[clazz].read(buf, null, sync)
    }

    @JvmStatic
    fun readAutoBytesByToken(token: TypeToken<*>, buf: ByteBuf, sync: Boolean): Any {
        return tokenCache[token].read(buf, null, sync)
    }

    // Other ===========================================================================================================

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
