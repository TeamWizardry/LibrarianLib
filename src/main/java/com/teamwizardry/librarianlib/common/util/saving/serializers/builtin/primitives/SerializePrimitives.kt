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

        SerializerRegistry["java:char"]?.register(Targets.NBT, Targets.NBT.impl<Char>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).short.toChar()
        }, { value ->
            NBTTagShort(value.toShort())
        }))

        SerializerRegistry["java:char"]?.register(Targets.BYTES, Targets.BYTES.impl<Char>
        ({ buf, existing ->
            buf.readChar()
        }, { buf, value ->
            buf.writeChar(value.toInt())
        }))
    }

    private fun byte() {
        SerializerRegistry.register("java:byte", Serializer(Byte::class.javaPrimitiveType!!, Byte::class.javaObjectType))

        SerializerRegistry["java:byte"]?.register(Targets.NBT, Targets.NBT.impl<Byte>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).byte
        }, { value ->
            NBTTagByte(value)
        }))

        SerializerRegistry["java:byte"]?.register(Targets.BYTES, Targets.BYTES.impl<Byte>
        ({ buf, existing ->
            buf.readByte()
        }, { buf, value ->
            buf.writeByte(value.toInt())
        }))
    }

    private fun short() {
        SerializerRegistry.register("java:short", Serializer(Short::class.javaPrimitiveType!!, Short::class.javaObjectType))

        SerializerRegistry["java:short"]?.register(Targets.NBT, Targets.NBT.impl<Short>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).short
        }, { value ->
            NBTTagShort(value)
        }))

        SerializerRegistry["java:short"]?.register(Targets.BYTES, Targets.BYTES.impl<Short>
        ({ buf, existing ->
            buf.readShort()
        }, { buf, value ->
            buf.writeShort(value.toInt())
        }))
    }

    private fun int() {
        SerializerRegistry.register("java:int", Serializer(Int::class.javaPrimitiveType!!, Int::class.javaObjectType))

        SerializerRegistry["java:int"]?.register(Targets.NBT, Targets.NBT.impl<Int>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).int
        }, { value ->
            NBTTagInt(value)
        }))

        SerializerRegistry["java:int"]?.register(Targets.BYTES, Targets.BYTES.impl<Int>
        ({ buf, existing ->
            buf.readInt()
        }, { buf, value ->
            buf.writeInt(value)
        }))
    }

    private fun long() {
        SerializerRegistry.register("java:long", Serializer(Long::class.javaPrimitiveType!!, Long::class.javaObjectType))

        SerializerRegistry["java:long"]?.register(Targets.NBT, Targets.NBT.impl<Long>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).long
        }, { value ->
            NBTTagLong(value)
        }))

        SerializerRegistry["java:long"]?.register(Targets.BYTES, Targets.BYTES.impl<Long>
        ({ buf, existing ->
            buf.readLong()
        }, { buf, value ->
            buf.writeLong(value)
        }))
    }

    private fun float() {
        SerializerRegistry.register("java:float", Serializer(Float::class.javaPrimitiveType!!, Float::class.javaObjectType))

        SerializerRegistry["java:float"]?.register(Targets.NBT, Targets.NBT.impl<Float>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).float
        }, { value ->
            NBTTagFloat(value)
        }))

        SerializerRegistry["java:float"]?.register(Targets.BYTES, Targets.BYTES.impl<Float>
        ({ buf, existing ->
            buf.readFloat()
        }, { buf, value ->
            buf.writeFloat(value)
        }))
    }

    private fun double() {
        SerializerRegistry.register("java:double", Serializer(Double::class.javaPrimitiveType!!, Double::class.javaObjectType))

        SerializerRegistry["java:double"]?.register(Targets.NBT, Targets.NBT.impl<Double>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).double
        }, { value ->
            NBTTagDouble(value)
        }))

        SerializerRegistry["java:double"]?.register(Targets.BYTES, Targets.BYTES.impl<Double>
        ({ buf, existing ->
            buf.readDouble()
        }, { buf, value ->
            buf.writeDouble(value)
        }))
    }

    private fun boolean() {
        SerializerRegistry.register("java:boolean", Serializer(Boolean::class.javaPrimitiveType!!, Boolean::class.javaObjectType))

        SerializerRegistry["java:boolean"]?.register(Targets.NBT, Targets.NBT.impl<Boolean>
        ({ nbt, existing ->
            nbt.safeCast(NBTPrimitive::class.java).byte == 1.toByte()
        }, { value ->
            NBTTagByte(if(value) 1 else 0)
        }))

        SerializerRegistry["java:boolean"]?.register(Targets.BYTES, Targets.BYTES.impl<Boolean>
        ({ buf, existing ->
            buf.readBoolean()
        }, { buf, value ->
            buf.writeBoolean(value)
        }))
    }

    private fun string() {
        SerializerRegistry.register("java:string", Serializer(String::class.java))

        SerializerRegistry["java:string"]?.register(Targets.NBT, Targets.NBT.impl<String>
        ({ nbt, existing ->
            nbt.safeCast(NBTTagString::class.java).string
        }, { value ->
            NBTTagString(value)
        }))

        SerializerRegistry["java:string"]?.register(Targets.BYTES, Targets.BYTES.impl<String>
        ({ buf, existing ->
            buf.readString()
        }, { buf, value ->
            buf.writeString(value)
        }))
    }
}
