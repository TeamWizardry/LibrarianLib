package com.teamwizardry.librarianlib.features.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.*

/**
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeEnumFactory : SerializerFactory("Enum") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if(type.clazz.isEnum) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    @Suppress("UNCHECKED_CAST")
    override fun create(type: FieldType): Serializer<*> {
        return SerializeEnum(type, (type.clazz.enumConstants as Array<Enum<*>>))
    }

    class SerializeEnum(type: FieldType, val constants: Array<Enum<*>>) : Serializer<Enum<*>>(type) {

        val constantsMap = constants.associateBy { it.name }
        val constSize = constants.size

        override fun readNBT(nbt: NBTBase, existing: Enum<*>?, syncing: Boolean): Enum<*> {
            if (syncing || nbt is NBTPrimitive) {
                nbt.safeCast(NBTPrimitive::class.java).let {
                    if (constSize <= 256) {
                        return constants[it.byte.toInt()]
                    } else {
                        return constants[it.short.toInt()]
                    }
                }
            } else {
                val name = nbt.safeCast(NBTTagString::class.java).string
                return constantsMap[name] ?: throw IllegalArgumentException("No such enum element $name for class ${type.clazz.canonicalName}")
            }
        }

        override fun writeNBT(value: Enum<*>, syncing: Boolean): NBTBase {
            if (syncing) {
                if (constSize <= 256) {
                    return NBTTagByte(value.ordinal.toByte())
                } else {
                    return NBTTagShort(value.ordinal.toShort())
                }
            } else {
                return NBTTagString(value.name)
            }
        }

        override fun readBytes(buf: ByteBuf, existing: Enum<*>?, syncing: Boolean): Enum<*> {
            if (constSize <= 256) {
                return constants[buf.readByte().toInt()]
            } else {
                return constants[buf.readShort().toInt()]
            }
        }

        override fun writeBytes(buf: ByteBuf, value: Enum<*>, syncing: Boolean) {
            if (constSize <= 256) {
                buf.writeByte(value.ordinal)
            } else {
                buf.writeShort(value.ordinal)
            }
        }
    }
}
