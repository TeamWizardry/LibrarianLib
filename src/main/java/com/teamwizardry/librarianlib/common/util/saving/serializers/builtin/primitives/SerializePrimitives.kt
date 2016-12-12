package com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.primitives

import com.teamwizardry.librarianlib.common.util.readString
import com.teamwizardry.librarianlib.common.util.safeCast
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import com.teamwizardry.librarianlib.common.util.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.common.util.saving.serializers.builtin.Targets
import com.teamwizardry.librarianlib.common.util.writeString
import net.minecraft.nbt.*

/**
 * Created by TheCodeWarrior
 */
object SerializePrimitives {
    init {
        char()
        byte()
        short()
        int()
        long()
        float()
        double()
        boolean()
        string()
    }

    private fun char() {
        SerializerRegistry.register("java:char", Serializer(Char::class.javaPrimitiveType!!, Char::class.javaObjectType))

        SerializerRegistry["java:char"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).short.toChar()
        }, { value ->
            value as Char
            NBTTagShort(value.toShort())
        })

        SerializerRegistry["java:char"]?.register(Targets.BYTES, { buf, existing ->
            buf.readChar()
        }, { buf, value ->
            value as Char
            buf.writeChar(value.toInt())
        })
    }

    private fun byte() {
        SerializerRegistry.register("java:byte", Serializer(Byte::class.javaPrimitiveType!!, Byte::class.javaObjectType))

        SerializerRegistry["java:byte"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).byte
        }, { value ->
            value as Byte
            NBTTagByte(value.toByte())
        })

        SerializerRegistry["java:byte"]?.register(Targets.BYTES, { buf, existing ->
            buf.readByte()
        }, { buf, value ->
            value as Byte
            buf.writeByte(value.toInt())
        })
    }

    private fun short() {
        SerializerRegistry.register("java:short", Serializer(Short::class.javaPrimitiveType!!, Short::class.javaObjectType))

        SerializerRegistry["java:short"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).short
        }, { value ->
            value as Short
            NBTTagShort(value.toShort())
        })

        SerializerRegistry["java:short"]?.register(Targets.BYTES, { buf, existing ->
            buf.readShort()
        }, { buf, value ->
            value as Short
            buf.writeShort(value.toInt())
        })
    }

    private fun int() {
        SerializerRegistry.register("java:int", Serializer(Int::class.javaPrimitiveType!!, Int::class.javaObjectType))

        SerializerRegistry["java:int"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).int
        }, { value ->
            value as Int
            NBTTagInt(value)
        })

        SerializerRegistry["java:int"]?.register(Targets.BYTES, { buf, existing ->
            buf.readInt()
        }, { buf, value ->
            value as Int
            buf.writeInt(value)
        })
    }

    private fun long() {
        SerializerRegistry.register("java:long", Serializer(Long::class.javaPrimitiveType!!, Long::class.javaObjectType))

        SerializerRegistry["java:long"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).long
        }, { value ->
            value as Long
            NBTTagLong(value)
        })

        SerializerRegistry["java:long"]?.register(Targets.BYTES, { buf, existing ->
            buf.readLong()
        }, { buf, value ->
            value as Long
            buf.writeLong(value)
        })
    }

    private fun float() {
        SerializerRegistry.register("java:float", Serializer(Float::class.javaPrimitiveType!!, Float::class.javaObjectType))

        SerializerRegistry["java:float"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).float
        }, { value ->
            value as Float
            NBTTagFloat(value)
        })

        SerializerRegistry["java:float"]?.register(Targets.BYTES, { buf, existing ->
            buf.readFloat()
        }, { buf, value ->
            value as Float
            buf.writeFloat(value)
        })
    }

    private fun double() {
        SerializerRegistry.register("java:double", Serializer(Double::class.javaPrimitiveType!!, Double::class.javaObjectType))

        SerializerRegistry["java:double"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).double
        }, { value ->
            value as Double
            NBTTagDouble(value)
        })

        SerializerRegistry["java:double"]?.register(Targets.BYTES, { buf, existing ->
            buf.readDouble()
        }, { buf, value ->
            value as Double
            buf.writeDouble(value)
        })
    }

    private fun boolean() {
        SerializerRegistry.register("java:boolean", Serializer(Boolean::class.javaPrimitiveType!!, Boolean::class.javaObjectType))

        SerializerRegistry["java:boolean"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).byte == 1.toByte()
        }, { value ->
            value as Boolean
            NBTTagByte(if(value) 1 else 0)
        })

        SerializerRegistry["java:boolean"]?.register(Targets.BYTES, { buf, existing ->
            buf.readBoolean()
        }, { buf, value ->
            value as Boolean
            buf.writeBoolean(value)
        })
    }

    private fun string() {
        SerializerRegistry.register("java:string", Serializer(String::class.java))

        SerializerRegistry["java:string"]?.register(Targets.NBT, { nbt, existing ->
            nbt.safeCast(NBTTagString::class.java).string
        }, { value ->
            value as String
            NBTTagString(value)
        })

        SerializerRegistry["java:string"]?.register(Targets.BYTES, { buf, existing ->
            buf.readString()
        }, { buf, value ->
            value as String
            buf.writeString(value)
        })
    }
}
