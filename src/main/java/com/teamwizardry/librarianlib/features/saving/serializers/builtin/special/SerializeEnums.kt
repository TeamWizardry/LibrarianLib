package com.teamwizardry.librarianlib.features.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerPriority
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTPrimitive
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagShort
import net.minecraft.nbt.NBTTagString

/**
 * Created by TheCodeWarrior
 */
object SerializeEnums {
    init {
        SerializerRegistry.register("java:generator.enum", Serializer({ type -> type.clazz.isEnum }, SerializerPriority.GENERAL))

        SerializerRegistry["java:generator.enum"]?.register(Targets.NBT, { type ->

            @Suppress("UNCHECKED_CAST")
            val constants = type.clazz.enumConstants as Array<Enum<*>>
            val constantsMap = constants.associateBy { it.name }
            val constSize = constants.size

            Targets.NBT.impl<Enum<*>>({ nbt, _, syncing ->
                if (syncing || nbt is NBTPrimitive) {
                    nbt.safeCast(NBTPrimitive::class.java).let {
                        if (constSize <= 256) {
                            constants[it.byte.toInt()]
                        } else {
                            constants[it.short.toInt()]
                        }
                    }
                } else {
                    val name = nbt.safeCast(NBTTagString::class.java).string
                    constantsMap[name] ?: throw IllegalArgumentException("No such enum element $name for class ${type.clazz.canonicalName}")
                }
            }, { value, syncing ->
                if (syncing) {
                    if (constSize <= 256) {
                        NBTTagByte(value.ordinal.toByte())
                    } else {
                        NBTTagShort(value.ordinal.toShort())
                    }
                } else {
                    NBTTagString(value.name)
                }
            })
        })

        SerializerRegistry["java:generator.enum"]?.register(Targets.BYTES, { type ->

            @Suppress("UNCHECKED_CAST")
            val constants = type.clazz.enumConstants as Array<Enum<*>>
            val constSize = constants.size

            Targets.BYTES.impl<Enum<*>>({ buf, _, _ ->
                if (constSize <= 256) {
                    constants[buf.readByte().toInt()]
                } else {
                    constants[buf.readShort().toInt()]
                }
            }, { buf, value, _ ->
                if (constSize <= 256) {
                    buf.writeByte(value.ordinal)
                } else {
                    buf.writeShort(value.ordinal)
                }
            })
        })
    }
}
