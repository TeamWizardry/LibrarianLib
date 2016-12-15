package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerImpl
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerTarget
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeMisc
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeVectors
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitiveArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitives
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

    }

    class NBTTarget : SerializerTarget<(nbt: NBTBase, existing: Any?) -> Any, (value: Any) -> NBTBase>("NBT") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (NBTBase, T?) -> T, write: (T) -> NBTBase): SerializerImpl<(NBTBase, T?) -> T, (T) -> NBTBase> {
            return SerializerImpl(read, write)
        }
    }

    class ByteTarget : SerializerTarget<(buf: ByteBuf, existing: Any?) -> Any, (buf: ByteBuf, value: Any) -> Unit>("ByteBuf") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (buf: ByteBuf, existing: T?) -> T, write: (buf: ByteBuf, value: T) -> Unit): SerializerImpl<(buf: ByteBuf, existing: T?) -> T, (buf: ByteBuf, value: T) -> Unit> {
            return SerializerImpl(read, write)
        }
    }

    class JsonTarget : SerializerTarget<(json: JsonElement, existing: Any?) -> Any, (value: Any) -> JsonElement>("JSON") {
        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (json: JsonElement, existing: T?) -> T, write: (value: T) -> JsonElement): SerializerImpl<(json: JsonElement, existing: T?) -> T, (value: T) -> JsonElement> {
            return SerializerImpl(read, write)
        }
    }
}

