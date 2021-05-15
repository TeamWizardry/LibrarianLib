package com.teamwizardry.librarianlib.scribe.nbt

import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.AbstractNumberTag

internal object Vec2dSerializer: NbtSerializer<Vec2d>() {
    override fun deserialize(tag: Tag, existing: Vec2d?): Vec2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Vec2d(
            tag.expect<AbstractNumberTag>("X").double,
            tag.expect<AbstractNumberTag>("Y").double
        )
    }

    override fun serialize(value: Vec2d): Tag {
        val tag = CompoundTag()
        tag.put("X", DoubleTag.of(value.x))
        tag.put("Y", DoubleTag.of(value.y))
        return tag
    }
}

internal object Vec2iSerializer: NbtSerializer<Vec2i>() {
    override fun deserialize(tag: Tag, existing: Vec2i?): Vec2i {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Vec2i(
            tag.expect<AbstractNumberTag>("X").int,
            tag.expect<AbstractNumberTag>("Y").int
        )
    }

    override fun serialize(value: Vec2i): Tag {
        val tag = CompoundTag()
        tag.put("X", IntTag.of(value.x))
        tag.put("Y", IntTag.of(value.y))
        return tag
    }
}

internal object Rect2dSerializer: NbtSerializer<Rect2d>() {
    override fun deserialize(tag: Tag, existing: Rect2d?): Rect2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Rect2d(
            tag.expect<AbstractNumberTag>("X").double,
            tag.expect<AbstractNumberTag>("Y").double,
            tag.expect<AbstractNumberTag>("Width").double,
            tag.expect<AbstractNumberTag>("Height").double
        )
    }

    override fun serialize(value: Rect2d): Tag {
        val tag = CompoundTag()
        tag.put("X", DoubleTag.of(value.x))
        tag.put("Y", DoubleTag.of(value.y))
        tag.put("Width", DoubleTag.of(value.width))
        tag.put("Height", DoubleTag.of(value.height))
        return tag
    }
}

internal object Matrix3dSerializer: NbtSerializer<Matrix3d>() {
    override fun deserialize(tag: Tag, existing: Matrix3d?): Matrix3d {
        val list = tag.expectType<ListTag>("tag")
        return Matrix3d(
            list[0].expectType<AbstractNumberTag>("00").double,
            list[1].expectType<AbstractNumberTag>("01").double,
            list[2].expectType<AbstractNumberTag>("02").double,
            list[3].expectType<AbstractNumberTag>("10").double,
            list[4].expectType<AbstractNumberTag>("11").double,
            list[5].expectType<AbstractNumberTag>("12").double,
            list[6].expectType<AbstractNumberTag>("20").double,
            list[7].expectType<AbstractNumberTag>("21").double,
            list[8].expectType<AbstractNumberTag>("22").double
        )
    }

    override fun serialize(value: Matrix3d): Tag {
        return ListTag().also {
            it.add(DoubleTag.of(value.m00))
            it.add(DoubleTag.of(value.m01))
            it.add(DoubleTag.of(value.m02))
            it.add(DoubleTag.of(value.m10))
            it.add(DoubleTag.of(value.m11))
            it.add(DoubleTag.of(value.m12))
            it.add(DoubleTag.of(value.m20))
            it.add(DoubleTag.of(value.m21))
            it.add(DoubleTag.of(value.m22))
        }
    }
}

internal object MutableMatrix3dSerializer: NbtSerializer<MutableMatrix3d>() {
    override fun deserialize(tag: Tag, existing: MutableMatrix3d?): MutableMatrix3d {
        val list = tag.expectType<ListTag>("tag")
        return (existing ?: MutableMatrix3d()).set(
            list[0].expectType<AbstractNumberTag>("00").double,
            list[1].expectType<AbstractNumberTag>("01").double,
            list[2].expectType<AbstractNumberTag>("02").double,
            list[3].expectType<AbstractNumberTag>("10").double,
            list[4].expectType<AbstractNumberTag>("11").double,
            list[5].expectType<AbstractNumberTag>("12").double,
            list[6].expectType<AbstractNumberTag>("20").double,
            list[7].expectType<AbstractNumberTag>("21").double,
            list[8].expectType<AbstractNumberTag>("22").double
        )
    }

    override fun serialize(value: MutableMatrix3d): Tag {
        return ListTag().also {
            it.add(DoubleTag.of(value.m00))
            it.add(DoubleTag.of(value.m01))
            it.add(DoubleTag.of(value.m02))
            it.add(DoubleTag.of(value.m10))
            it.add(DoubleTag.of(value.m11))
            it.add(DoubleTag.of(value.m12))
            it.add(DoubleTag.of(value.m20))
            it.add(DoubleTag.of(value.m21))
            it.add(DoubleTag.of(value.m22))
        }
    }
}

internal object Matrix4dSerializer: NbtSerializer<Matrix4d>() {
    override fun deserialize(tag: Tag, existing: Matrix4d?): Matrix4d {
        val list = tag.expectType<ListTag>("tag")
        return Matrix4d(
            list[0].expectType<AbstractNumberTag>("00").double,
            list[1].expectType<AbstractNumberTag>("01").double,
            list[2].expectType<AbstractNumberTag>("02").double,
            list[3].expectType<AbstractNumberTag>("03").double,
            list[4].expectType<AbstractNumberTag>("10").double,
            list[5].expectType<AbstractNumberTag>("11").double,
            list[6].expectType<AbstractNumberTag>("12").double,
            list[7].expectType<AbstractNumberTag>("13").double,
            list[8].expectType<AbstractNumberTag>("20").double,
            list[9].expectType<AbstractNumberTag>("21").double,
            list[10].expectType<AbstractNumberTag>("22").double,
            list[11].expectType<AbstractNumberTag>("23").double,
            list[12].expectType<AbstractNumberTag>("30").double,
            list[13].expectType<AbstractNumberTag>("31").double,
            list[14].expectType<AbstractNumberTag>("32").double,
            list[15].expectType<AbstractNumberTag>("33").double
        )
    }

    override fun serialize(value: Matrix4d): Tag {
        return ListTag().also {
            it.add(DoubleTag.of(value.m00))
            it.add(DoubleTag.of(value.m01))
            it.add(DoubleTag.of(value.m02))
            it.add(DoubleTag.of(value.m03))
            it.add(DoubleTag.of(value.m10))
            it.add(DoubleTag.of(value.m11))
            it.add(DoubleTag.of(value.m12))
            it.add(DoubleTag.of(value.m13))
            it.add(DoubleTag.of(value.m20))
            it.add(DoubleTag.of(value.m21))
            it.add(DoubleTag.of(value.m22))
            it.add(DoubleTag.of(value.m23))
            it.add(DoubleTag.of(value.m30))
            it.add(DoubleTag.of(value.m31))
            it.add(DoubleTag.of(value.m32))
            it.add(DoubleTag.of(value.m33))
        }
    }
}

internal object MutableMatrix4dSerializer: NbtSerializer<MutableMatrix4d>() {
    override fun deserialize(tag: Tag, existing: MutableMatrix4d?): MutableMatrix4d {
        val list = tag.expectType<ListTag>("tag")
        return (existing ?: MutableMatrix4d()).set(
            list[0].expectType<AbstractNumberTag>("00").double,
            list[1].expectType<AbstractNumberTag>("01").double,
            list[2].expectType<AbstractNumberTag>("02").double,
            list[3].expectType<AbstractNumberTag>("03").double,
            list[4].expectType<AbstractNumberTag>("10").double,
            list[5].expectType<AbstractNumberTag>("11").double,
            list[6].expectType<AbstractNumberTag>("12").double,
            list[7].expectType<AbstractNumberTag>("13").double,
            list[8].expectType<AbstractNumberTag>("20").double,
            list[9].expectType<AbstractNumberTag>("21").double,
            list[10].expectType<AbstractNumberTag>("22").double,
            list[11].expectType<AbstractNumberTag>("23").double,
            list[12].expectType<AbstractNumberTag>("30").double,
            list[13].expectType<AbstractNumberTag>("31").double,
            list[14].expectType<AbstractNumberTag>("32").double,
            list[15].expectType<AbstractNumberTag>("33").double
        )
    }

    override fun serialize(value: MutableMatrix4d): Tag {
        return ListTag().also {
            it.add(DoubleTag.of(value.m00))
            it.add(DoubleTag.of(value.m01))
            it.add(DoubleTag.of(value.m02))
            it.add(DoubleTag.of(value.m03))
            it.add(DoubleTag.of(value.m10))
            it.add(DoubleTag.of(value.m11))
            it.add(DoubleTag.of(value.m12))
            it.add(DoubleTag.of(value.m13))
            it.add(DoubleTag.of(value.m20))
            it.add(DoubleTag.of(value.m21))
            it.add(DoubleTag.of(value.m22))
            it.add(DoubleTag.of(value.m23))
            it.add(DoubleTag.of(value.m30))
            it.add(DoubleTag.of(value.m31))
            it.add(DoubleTag.of(value.m32))
            it.add(DoubleTag.of(value.m33))
        }
    }
}

internal object QuaternionSerializer: NbtSerializer<Quaternion>() {
    override fun deserialize(tag: Tag, existing: Quaternion?): Quaternion {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Quaternion(
            tag.expect<AbstractNumberTag>("X").double,
            tag.expect<AbstractNumberTag>("Y").double,
            tag.expect<AbstractNumberTag>("Z").double,
            tag.expect<AbstractNumberTag>("W").double
        )
    }

    override fun serialize(value: Quaternion): Tag {
        val tag = CompoundTag()
        tag.put("X", DoubleTag.of(value.x))
        tag.put("Y", DoubleTag.of(value.y))
        tag.put("Z", DoubleTag.of(value.z))
        tag.put("W", DoubleTag.of(value.w))
        return tag
    }
}
