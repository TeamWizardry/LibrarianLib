package com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics

import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagDouble
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Created by TheCodeWarrior
 */
object SerializeVectors {
    init {
        vec3d()
        vec3i()
        vec2d()
        blockPos()
    }

    private fun vec3d() {
        SerializerRegistry.register("minecraft:vec3d", Serializer(Vec3d::class.java))

        SerializerRegistry["minecraft:vec3d"]?.register(Targets.NBT, Targets.NBT.impl<Vec3d>
        ({ nbt, _, _ ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
        }, { value, _ ->
            val tag = NBTTagCompound()
            tag.setDouble("x", value.xCoord)
            tag.setDouble("y", value.yCoord)
            tag.setDouble("z", value.zCoord)
            tag
        }))

        SerializerRegistry["minecraft:vec3d"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec3d>
        ({ buf, _, _ ->
            Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
        }, { buf, value, _ ->
            buf.writeDouble(value.xCoord)
            buf.writeDouble(value.yCoord)
            buf.writeDouble(value.zCoord)
        }))
    }

    private fun vec3i() {
        SerializerRegistry.register("minecraft:vec3i", Serializer(Vec3i::class.java))

        SerializerRegistry["minecraft:vec3i"]?.register(Targets.NBT, Targets.NBT.impl<Vec3i>
        ({ nbt, _, _ ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Vec3i(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
        }, { value, _ ->
            val tag = NBTTagCompound()
            tag.setInteger("x", value.x)
            tag.setInteger("y", value.y)
            tag.setInteger("z", value.z)
            tag
        }))

        SerializerRegistry["minecraft:vec3i"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec3i>
        ({ buf, _, _ ->
            Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
        }, { buf, value, _ ->
            buf.writeInt(value.x)
            buf.writeInt(value.y)
            buf.writeInt(value.z)
        }))
    }

    private fun blockPos() {
        SerializerRegistry.register("minecraft:blockpos", Serializer(BlockPos::class.java))

        SerializerRegistry["minecraft:blockpos"]?.register(Targets.NBT, Targets.NBT.impl<BlockPos>
        ({ nbt, _, _ ->
            val tag = nbt.safeCast(NBTTagList::class.java)
            BlockPos(tag.getIntAt(0), tag.getIntAt(1), tag.getIntAt(2))
        }, { value, _ ->
            val tag = NBTTagList()
            tag.appendTag(NBTTagInt(value.x))
            tag.appendTag(NBTTagInt(value.y))
            tag.appendTag(NBTTagInt(value.z))
            tag
        }))

        SerializerRegistry["minecraft:blockpos"]?.register(Targets.BYTES, Targets.BYTES.impl<BlockPos>
        ({ buf, _, _ ->
            BlockPos.fromLong(buf.readLong())
        }, { buf, value, _ ->
            buf.writeLong(value.toLong())
        }))
    }

    private fun vec2d() {
        SerializerRegistry.register("liblib:vec2d", Serializer(Vec2d::class.java))

        SerializerRegistry["liblib:vec2d"]?.register(Targets.NBT, Targets.NBT.impl<Vec2d>
        ({ nbt, _, _ ->
            val tag = nbt.safeCast(NBTTagList::class.java)
            Vec2d(tag.getDoubleAt(0), tag.getDoubleAt(1))
        }, { value, _ ->
            val tag = NBTTagList()
            tag.appendTag(NBTTagDouble(value.x))
            tag.appendTag(NBTTagDouble(value.y))
            tag
        }))

        SerializerRegistry["liblib:vec2d"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec2d>
        ({ buf, _, _ ->
            Vec2d(buf.readDouble(), buf.readDouble())
        }, { buf, value, _ ->
            buf.writeDouble(value.x)
            buf.writeDouble(value.y)
        }))
    }
}
