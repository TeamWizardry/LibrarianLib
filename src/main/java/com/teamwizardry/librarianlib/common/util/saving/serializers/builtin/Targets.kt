package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerImpl
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerTarget
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeMisc
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeVectors
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitiveArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitives
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special.SerializeArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special.SerializeEnums
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase

/**
 * Created by TheCodeWarrior
 */
object Targets {
    val NBT = NBTTarget()
    val BYTES = ByteTarget()
    val JSON = JsonTarget()

    init {
        SerializePrimitives
        SerializePrimitiveArrays
        SerializeMisc
        SerializeVectors
        SerializeArrays
        SerializeEnums
    }

    class NBTTarget : SerializerTarget<(nbt: NBTBase, existing: Any?, syncing: Boolean) -> Any, (value: Any, syncing: Boolean) -> NBTBase>("NBT") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (nbt: NBTBase, existing: T?, syncing: Boolean) -> T, write: (value: T, syncing: Boolean) -> NBTBase): SerializerImpl<(NBTBase, T?, Boolean) -> T, (T, Boolean) -> NBTBase> {
            return SerializerImpl(read, write)
        }
    }

    class ByteTarget : SerializerTarget<(buf: ByteBuf, existing: Any?, syncing: Boolean) -> Any, (buf: ByteBuf, value: Any, syncing: Boolean) -> Unit>("ByteBuf") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (buf: ByteBuf, existing: T?, syncing: Boolean) -> T, write: (buf: ByteBuf, value: T, syncing: Boolean) -> Unit): SerializerImpl<(buf: ByteBuf, existing: T?, syncing: Boolean) -> T, (buf: ByteBuf, value: T, syncing: Boolean) -> Unit> {
            return SerializerImpl(read, write)
        }
    }

    class JsonTarget : SerializerTarget<(json: JsonElement, existing: Any?, syncing: Boolean) -> Any, (value: Any, syncing: Boolean) -> JsonElement>("JSON") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (json: JsonElement, existing: T?, syncing: Boolean) -> T, write: (value: T, syncing: Boolean) -> JsonElement): SerializerImpl<(json: JsonElement, existing: T?, syncing: Boolean) -> T, (value: T, syncing: Boolean) -> JsonElement> {
            return SerializerImpl(read, write)
        }
    }
}

