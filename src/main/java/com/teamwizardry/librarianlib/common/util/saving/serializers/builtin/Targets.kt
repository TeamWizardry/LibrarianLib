package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin

import com.google.gson.JsonElement
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerTarget
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitiveArrays
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives.SerializePrimitives
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase

/**
 * Created by TheCodeWarrior
 */
object Targets {
    val NBT = SerializerTarget<(nbt: NBTBase, existing: Any?) -> Any, (value: Any) -> NBTBase>()
    val BYTES = SerializerTarget<(buf: ByteBuf, existing: Any?) -> Any, (buf: ByteBuf, value: Any) -> Unit>()
    val JSON = SerializerTarget<(json: JsonElement, existing: Any?) -> Any, (value: Any) -> JsonElement>()

    init {
        SerializePrimitives
        SerializePrimitiveArrays
    }
}

