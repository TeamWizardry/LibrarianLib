package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.forEachIndexed
import com.teamwizardry.librarianlib.features.kotlin.readBooleanArray
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.kotlin.writeBooleanArray
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics.readTagFromBuffer
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics.writeTagToBuffer
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.special.SerializeObjectFactory
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.INBTSerializable

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeINBTSerializableFactory : SerializerFactory("INBTSerializable") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        if(SerializeObjectFactory.canApply(type) != SerializerFactoryMatch.NONE)
            return SerializerFactoryMatch.NONE
        return this.canApplySubclass(type, INBTSerializable::class.java)
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeINBTSerializable
    }

    object SerializeINBTSerializable: Serializer<INBTSerializable<NBTBase>>(FieldType.create(INBTSerializable::class.java)) {
        override fun getDefault(): INBTSerializable<NBTBase> {
            throw UnsupportedOperationException("INBTSerializable doesn't have a default value")
        }

        override fun readNBT(nbt: NBTBase, existing: INBTSerializable<NBTBase>?, syncing: Boolean): INBTSerializable<NBTBase> {
            if(existing == null) throw NullPointerException("INBTSerializable can only deserialize with an existing value")
            existing.deserializeNBT(nbt)
            return existing
        }

        override fun writeNBT(value: INBTSerializable<NBTBase>, syncing: Boolean): NBTBase {
            return value.serializeNBT()
        }

        override fun readBytes(buf: ByteBuf, existing: INBTSerializable<NBTBase>?, syncing: Boolean): INBTSerializable<NBTBase> {
            if(existing == null) throw NullPointerException("INBTSerializable can only deserialize with an existing value")
            val tag = readTagFromBuffer(buf.readByte(), buf)
            existing.deserializeNBT(tag)
            return existing
        }

        override fun writeBytes(buf: ByteBuf, value: INBTSerializable<NBTBase>, syncing: Boolean) {
            val tag = value.serializeNBT()
            buf.writeByte(tag.id.toInt())
            writeTagToBuffer(tag, buf)
        }
    }
}
