package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.common.util.saving.FieldType
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerException
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerImpl
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerTarget
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeMisc
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.basics.SerializeVectors
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics.SerializeLists
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics.SerializeMaps
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.generics.SerializeSets
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitiveArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitives
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special.SerializeArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special.SerializeEnums
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special.SerializeObject
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.trove.map.SerializeTroveMaps
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
        SerializeObject
        SerializeLists
        SerializeMaps
        SerializeSets

        // trove
        SerializeTroveMaps
    }

    class NBTTarget : SerializerTarget<(nbt: NBTBase, existing: Any?, syncing: Boolean) -> Any, (value: Any, syncing: Boolean) -> NBTBase>("NBT") {
        override fun wrap(impl: SerializerImpl<(NBTBase, Any?, Boolean) -> Any, (Any, Boolean) -> NBTBase>, type: FieldType): SerializerImpl<(NBTBase, Any?, Boolean) -> Any, (Any, Boolean) -> NBTBase> {
            return SerializerImpl(
                    { a, b, c ->
                        try {
                            impl.read(a, b, c)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error deserializing $type", e)
                        }
                    },
                    { a, b ->
                        try {
                            impl.write(a, b)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error serializing $type", e)
                        }
                    }
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (nbt: NBTBase, existing: T?, syncing: Boolean) -> T, write: (value: T, syncing: Boolean) -> NBTBase): SerializerImpl<(NBTBase, T?, Boolean) -> T, (T, Boolean) -> NBTBase> {
            return SerializerImpl(read, write)
        }
    }

    class ByteTarget : SerializerTarget<(buf: ByteBuf, existing: Any?, syncing: Boolean) -> Any, (buf: ByteBuf, value: Any, syncing: Boolean) -> Unit>("ByteBuf") {
        override fun wrap(impl: SerializerImpl<(ByteBuf, Any?, Boolean) -> Any, (ByteBuf, Any, Boolean) -> Unit>, type: FieldType): SerializerImpl<(ByteBuf, Any?, Boolean) -> Any, (ByteBuf, Any, Boolean) -> Unit> {
            return SerializerImpl(
                    { a, b, c ->
                        try {
                            impl.read(a, b, c)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error deserializing $type", e)
                        }
                    },
                    { a, b, c ->
                        try {
                            impl.write(a, b, c)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error serializing $type", e)
                        }
                    }
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (buf: ByteBuf, existing: T?, syncing: Boolean) -> T, write: (buf: ByteBuf, value: T, syncing: Boolean) -> Unit): SerializerImpl<(buf: ByteBuf, existing: T?, syncing: Boolean) -> T, (buf: ByteBuf, value: T, syncing: Boolean) -> Unit> {
            return SerializerImpl(read, write)
        }
    }

    class JsonTarget : SerializerTarget<(json: JsonElement, existing: Any?, syncing: Boolean) -> Any, (value: Any, syncing: Boolean) -> JsonElement>("JSON") {
        override fun wrap(impl: SerializerImpl<(JsonElement, Any?, Boolean) -> Any, (Any, Boolean) -> JsonElement>, type: FieldType): SerializerImpl<(JsonElement, Any?, Boolean) -> Any, (Any, Boolean) -> JsonElement> {
            return SerializerImpl(
                    { a, b, c ->
                        try {
                            impl.read(a, b, c)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error deserializing $type", e)
                        }
                    },
                    { a, b ->
                        try {
                            impl.write(a, b)
                        } catch(e: RuntimeException) {
                            throw SerializerException("Error serializing $type", e)
                        }
                    }
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> impl(read: (json: JsonElement, existing: T?, syncing: Boolean) -> T, write: (value: T, syncing: Boolean) -> JsonElement): SerializerImpl<(json: JsonElement, existing: T?, syncing: Boolean) -> T, (value: T, syncing: Boolean) -> JsonElement> {
            return SerializerImpl(read, write)
        }
    }
}

