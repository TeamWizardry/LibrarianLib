package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.special

import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTPrimitive
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagShort

/**
 * Created by TheCodeWarrior
 */
object SerializeEnums {
    init {
        SerializerRegistry.register("java:generator.enum", Serializer({ type -> type.clazz.isEnum }))

        SerializerRegistry["java:generator.enum"]?.register(Targets.NBT, { type ->

            val constants = type.clazz.enumConstants as Array<Enum<*>>
            val constSize = constants.size

            Targets.NBT.impl<Enum<*>>({ nbt, existing ->
                nbt.safeCast(NBTPrimitive::class.java).let {
                    if (constSize <= 256) {
                        constants[it.byte.toInt()]
                    } else {
                        constants[it.short.toInt()]
                    }
                }
            }, { value ->
                if (constSize <= 256) {
                    NBTTagByte(value.ordinal.toByte())
                } else {
                    NBTTagShort(value.ordinal.toShort())
                }
            })
        })

        SerializerRegistry["java:generator.enum"]?.register(Targets.BYTES, { type ->

            val constants = type.clazz.enumConstants as Array<Enum<*>>
            val constSize = constants.size

            Targets.BYTES.impl<Enum<*>>({ buf, existing ->
                if (constSize <= 256) {
                    constants[buf.readByte().toInt()]
                } else {
                    constants[buf.readShort().toInt()]
                }
            }, { buf, value ->
                if (constSize <= 256) {
                    buf.writeByte(value.ordinal)
                } else {
                    buf.writeShort(value.ordinal)
                }
            })
        })
    }
}
