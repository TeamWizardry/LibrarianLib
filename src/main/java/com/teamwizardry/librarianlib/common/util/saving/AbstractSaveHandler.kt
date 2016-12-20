package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerImpl
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.withRealDefault
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase

/**
 * Created by TheCodeWarrior
 */
object AbstractSaveHandler {
    var isSyncing: Boolean = false

    val nbtCache = mutableMapOf<Class<*>, SerializerImpl<(nbt: NBTBase, existing: Any?, syncing: Boolean) -> Any, (value: Any, syncing: Boolean) -> NBTBase>>()
            .withRealDefault { SerializerRegistry.impl(Targets.NBT, FieldType.create(it)) }
    val byteCache = mutableMapOf<Class<*>, SerializerImpl<(buf: ByteBuf, existing: Any?, syncing: Boolean) -> Any, (buf: ByteBuf, value: Any, syncing: Boolean) -> Unit>>()
            .withRealDefault { SerializerRegistry.impl(Targets.BYTES, FieldType.create(it)) }

    @JvmStatic
    fun writeAutoNBT(instance: Any, sync: Boolean): NBTBase {
        return nbtCache[instance.javaClass].write(instance, sync)
    }

    @JvmStatic
    fun readAutoNBT(instance: Any, tag: NBTBase, sync: Boolean) : Any {
        return nbtCache[instance.javaClass].read(tag, instance, sync)
    }

    @JvmStatic
    fun writeAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean) {
        byteCache[instance.javaClass].write(buf, instance, sync)
    }

    @JvmStatic
    fun readAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean) : Any {
        return byteCache[instance.javaClass].read(buf, instance, sync)
    }

    @JvmStatic
    fun cacheFields(clazz: Class<*>) {
        SavingFieldCache.getClassFields(clazz)
    }
}
