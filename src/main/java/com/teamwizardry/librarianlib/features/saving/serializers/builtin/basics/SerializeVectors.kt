package com.teamwizardry.librarianlib.features.saving.serializers.builtin.basics

import com.teamwizardry.librarianlib.features.kotlin.safeCast
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import com.teamwizardry.librarianlib.features.saving.serializers.builtin.Targets
import net.minecraft.nbt.NBTTagCompound
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
        ({ nbt, existing, sync ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
        }, { value, sync ->
            val tag = NBTTagCompound()
            tag.setDouble("x", value.xCoord)
            tag.setDouble("y", value.yCoord)
            tag.setDouble("z", value.zCoord)
            tag
        }))

        SerializerRegistry["minecraft:vec3d"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec3d>
        ({ buf, existing, sync ->
            Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
        }, { buf, value, sync ->
            buf.writeDouble(value.xCoord)
            buf.writeDouble(value.yCoord)
            buf.writeDouble(value.zCoord)
        }))
    }

    private fun vec3i() {
        SerializerRegistry.register("minecraft:vec3i", Serializer(Vec3i::class.java))

        SerializerRegistry["minecraft:vec3i"]?.register(Targets.NBT, Targets.NBT.impl<Vec3i>
        ({ nbt, existing, sync ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Vec3i(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
        }, { value, sync ->
            val tag = NBTTagCompound()
            tag.setInteger("x", value.x)
            tag.setInteger("y", value.y)
            tag.setInteger("z", value.z)
            tag
        }))

        SerializerRegistry["minecraft:vec3i"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec3i>
        ({ buf, existing, sync ->
            Vec3i(buf.readInt(), buf.readInt(), buf.readInt())
        }, { buf, value, sync ->
            buf.writeInt(value.x)
            buf.writeInt(value.y)
            buf.writeInt(value.z)
        }))
    }

    private fun blockPos() {
        SerializerRegistry.register("minecraft:blockpos", Serializer(BlockPos::class.java))

        SerializerRegistry["minecraft:blockpos"]?.register(Targets.NBT, Targets.NBT.impl<BlockPos>
        ({ nbt, existing, sync ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
        }, { value, sync ->
            val tag = NBTTagCompound()
            tag.setInteger("x", value.x)
            tag.setInteger("y", value.y)
            tag.setInteger("z", value.z)
            tag
        }))

        SerializerRegistry["minecraft:blockpos"]?.register(Targets.BYTES, Targets.BYTES.impl<BlockPos>
        ({ buf, existing, sync ->
            BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        }, { buf, value, sync ->
            buf.writeInt(value.x)
            buf.writeInt(value.y)
            buf.writeInt(value.z)
        }))
    }

    private fun vec2d() {
        SerializerRegistry.register("liblib:vec2d", Serializer(Vec2d::class.java))

        SerializerRegistry["liblib:vec2d"]?.register(Targets.NBT, Targets.NBT.impl<Vec2d>
        ({ nbt, existing, sync ->
            val tag = nbt.safeCast(NBTTagCompound::class.java)
            Vec2d(tag.getDouble("x"), tag.getDouble("y"))
        }, { value, sync ->
            val tag = NBTTagCompound()
            tag.setDouble("x", value.x)
            tag.setDouble("y", value.y)
            tag
        }))

        SerializerRegistry["liblib:vec2d"]?.register(Targets.BYTES, Targets.BYTES.impl<Vec2d>
        ({ buf, existing, sync ->
            Vec2d(buf.readDouble(), buf.readDouble())
        }, { buf, value, sync ->
            buf.writeDouble(value.x)
            buf.writeDouble(value.y)
        }))
    }
}
