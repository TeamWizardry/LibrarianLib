package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.readBooleanArray
import com.teamwizardry.librarianlib.common.util.writeBooleanArray
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by TheCodeWarrior
 */
object AbstractSaveHandler {
    @JvmStatic
    @JvmOverloads
    fun writeAutoNBT(instance: Any, cmp: NBTTagCompound, sync: Boolean = false) {
        SavingFieldCache.getClassFields(instance.javaClass).forEach {
            if (!sync || it.value.syncToClient) {
                val handler = NBTSerializationHandlers.getWriterUnchecked(it.value.clazz)
                if (handler != null) {
                    val value = it.value.getter(instance)
                    if (value != null)
                        cmp.setTag(it.key, handler(value))
                }
            }
        }
    }

    @JvmStatic
    fun readAutoNBT(instance: Any, cmp: NBTTagCompound) {
        SavingFieldCache.getClassFields(instance.javaClass).forEach {
            if (!cmp.hasKey(it.key))
                it.value.setter(instance, null)
            else {
                val handler = NBTSerializationHandlers.getReaderUnchecked(it.value.clazz)
                if (handler != null)
                    it.value.setter(instance, handler(cmp.getTag(it.key)))
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun writeAutoBytes(instance: Any, buf: ByteBuf, sync: Boolean = false) {
        val cache = SavingFieldCache.getClassFields(instance.javaClass).filter {!sync || it.value.syncToClient}
        val nullSig = BooleanArray(cache.size)
        var i = 0
        cache.forEach {
            nullSig[i] = false
            val handler = ByteBufSerializationHandlers.getWriterUnchecked(it.value.clazz)
            if (handler != null) {
                val field = it.value.getter(instance)
                if (field == null)
                    nullSig[i] = true
            }
            i++
        }
        buf.writeBooleanArray(nullSig)
        cache.forEach {
            val handler = ByteBufSerializationHandlers.getWriterUnchecked(it.value.clazz)
            if (handler != null) {
                val field = it.value.getter(instance)
                if (field == null)
                else {
                    handler(buf, field)
                }
            }
        }
    }

    @JvmStatic
    fun readAutoBytes(instance: Any, buf: ByteBuf) {
        val cache = SavingFieldCache.getClassFields(instance.javaClass)
        val nullSig = buf.readBooleanArray()
        var i = 0
        cache.forEach {
            if (nullSig[i])
                it.value.setter(instance, null)
            else {
                val handler = ByteBufSerializationHandlers.getReaderUnchecked(it.value.clazz)
                if (handler != null)
                    it.value.setter(instance, handler(buf))
            }
            i++
        }
    }

    @JvmStatic
    fun cacheFields(clazz: Class<*>) {
        SavingFieldCache.getClassFields(clazz)
    }
}
