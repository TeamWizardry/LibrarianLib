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

/**
 * Annotate an enum element with this to have the serializer fall back to it in the case of an unknown element
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class FallbackEnumValue

@SerializerFactoryRegister
object SerializeEnumFactory : SerializerFactory("Enum") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        return if (type.clazz.isEnum || type.clazz.superclass?.isEnum == true) SerializerFactoryMatch.GENERAL else SerializerFactoryMatch.NONE
    }

    @Suppress("UNCHECKED_CAST")
    override fun create(type: FieldType): Serializer<*> {
        return SerializeEnum(type, (type.clazz.enumConstants as? Array<Enum<*>>?: ((type.clazz.superclass as Class<*>).enumConstants as Array<Enum<*>>)))
    }

    class SerializeEnum(type: FieldType, val constants: Array<Enum<*>>) : Serializer<Enum<*>>(type) {
        override fun getDefault(): Enum<*> {
            return defaultConstant
        }

        val fallbackConstant = constants.find { it::class.java.getField(it.name).isAnnotationPresent(FallbackEnumValue::class.java) }

        val defaultConstant = constants.first()
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
                return constantsMap[name] ?: getError(name)
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
            val c = if (constSize <= 256) {
                buf.readByte().toInt()
            } else {
                buf.readShort().toInt()
            }
            if (c < 0 || c >= constants.size)
                return getError(c)
            return constants[c]
        }

        override fun writeBytes(buf: ByteBuf, value: Enum<*>, syncing: Boolean) {
            if (constSize <= 256) {
                buf.writeByte(value.ordinal)
            } else {
                buf.writeShort(value.ordinal)
            }
        }

        private fun getError(name: Int): Enum<*> {
            return getError(name.toString())
        }

        private fun getError(name: String): Enum<*> {
            return fallbackConstant ?: throw IllegalArgumentException("No such enum element $name for class ${type.clazz.canonicalName}, perhaps annotate one element with @FallbackEnumValue?")
        }
    }
}
