package com.teamwizardry.librarianlib.features.saving.serializers.builtin.generics

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics.readTagFromBuffer
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics.writeTagToBuffer
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.special.SerializeObjectFactory
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.relauncher.ReflectionHelper

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
            throw UnsupportedOperationException("INBTSerializable doesn't have a default array")
        }

        override fun readNBT(nbt: NBTBase, existing: INBTSerializable<NBTBase>?, syncing: Boolean): INBTSerializable<NBTBase> {
            if (existing == null) throw NullPointerException("INBTSerializable can only deserialize with an existing array")
            existing.deserializeNBT(nbt)
            return existing
        }

        override fun writeNBT(value: INBTSerializable<NBTBase>, syncing: Boolean): NBTBase {
            return value.serializeNBT()
        }

        override fun readBytes(buf: ByteBuf, existing: INBTSerializable<NBTBase>?, syncing: Boolean): INBTSerializable<NBTBase> {
            if (existing == null) throw NullPointerException("INBTSerializable can only deserialize with an existing array")
            val tag = readTagFromBuffer(buf.readByte(), buf)
            existing.deserializeNBT(tag)
            return existing
        }

        override fun writeBytes(buf: ByteBuf, value: INBTSerializable<NBTBase>, syncing: Boolean) {
            val tag = value.serializeNBT()
            buf.writeByte(tag.id.toInt())
            writeTagToBuffer(tag, buf)
        }

        private fun createConstructorMH(): () -> INBTSerializable<NBTBase> {
            if (type.clazz == INBTSerializable::class.java) {
                return {
                    throw UnsupportedOperationException("Cannot initialize array, INBTSerializable " +
                        "isn't a specific class") }
            } else {
                try {
                    val mh = MethodHandleHelper.wrapperForConstructor<INBTSerializable<NBTBase>>(type.clazz)
                    return { mh(arrayOf()) }
                } catch(e: ReflectionHelper.UnableToFindMethodException) {
                    return { throw UnsupportedOperationException("Could not find zero-argument constructor for " +
                            type.clazz.simpleName, e) }
                }
            }
        }
    }
}
