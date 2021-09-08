package com.teamwizardry.librarianlib.scribe.nbt

import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.AbstractNbtNumber

internal object Vec2dSerializer: NbtSerializer<Vec2d>() {
    override fun deserialize(tag: NbtElement): Vec2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Vec2d(
            tag.expect<AbstractNbtNumber>("X").doubleValue(),
            tag.expect<AbstractNbtNumber>("Y").doubleValue()
        )
    }

    override fun serialize(value: Vec2d): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtDouble.of(value.x))
        tag.put("Y", NbtDouble.of(value.y))
        return tag
    }
}

internal object Vec2iSerializer: NbtSerializer<Vec2i>() {
    override fun deserialize(tag: NbtElement): Vec2i {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Vec2i(
            tag.expect<AbstractNbtNumber>("X").intValue(),
            tag.expect<AbstractNbtNumber>("Y").intValue()
        )
    }

    override fun serialize(value: Vec2i): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtInt.of(value.x))
        tag.put("Y", NbtInt.of(value.y))
        return tag
    }
}

internal object Rect2dSerializer: NbtSerializer<Rect2d>() {
    override fun deserialize(tag: NbtElement): Rect2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Rect2d(
            tag.expect<AbstractNbtNumber>("X").doubleValue(),
            tag.expect<AbstractNbtNumber>("Y").doubleValue(),
            tag.expect<AbstractNbtNumber>("Width").doubleValue(),
            tag.expect<AbstractNbtNumber>("Height").doubleValue()
        )
    }

    override fun serialize(value: Rect2d): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtDouble.of(value.x))
        tag.put("Y", NbtDouble.of(value.y))
        tag.put("Width", NbtDouble.of(value.width))
        tag.put("Height", NbtDouble.of(value.height))
        return tag
    }
}

internal object Matrix3dSerializer: NbtSerializer<Matrix3d>() {
    override fun deserialize(tag: NbtElement): Matrix3d {
        val list = tag.expectType<NbtList>("tag")
        return Matrix3d(
            list[0].expectType<AbstractNbtNumber>("00").doubleValue(),
            list[1].expectType<AbstractNbtNumber>("01").doubleValue(),
            list[2].expectType<AbstractNbtNumber>("02").doubleValue(),
            list[3].expectType<AbstractNbtNumber>("10").doubleValue(),
            list[4].expectType<AbstractNbtNumber>("11").doubleValue(),
            list[5].expectType<AbstractNbtNumber>("12").doubleValue(),
            list[6].expectType<AbstractNbtNumber>("20").doubleValue(),
            list[7].expectType<AbstractNbtNumber>("21").doubleValue(),
            list[8].expectType<AbstractNbtNumber>("22").doubleValue()
        )
    }

    override fun serialize(value: Matrix3d): NbtElement {
        return NbtList().also {
            it.add(NbtDouble.of(value.m00))
            it.add(NbtDouble.of(value.m01))
            it.add(NbtDouble.of(value.m02))
            it.add(NbtDouble.of(value.m10))
            it.add(NbtDouble.of(value.m11))
            it.add(NbtDouble.of(value.m12))
            it.add(NbtDouble.of(value.m20))
            it.add(NbtDouble.of(value.m21))
            it.add(NbtDouble.of(value.m22))
        }
    }
}

internal object MutableMatrix3dSerializer: NbtSerializer<MutableMatrix3d>() {
    override fun deserialize(tag: NbtElement): MutableMatrix3d {
        val list = tag.expectType<NbtList>("tag")
        return MutableMatrix3d().set(
            list[0].expectType<AbstractNbtNumber>("00").doubleValue(),
            list[1].expectType<AbstractNbtNumber>("01").doubleValue(),
            list[2].expectType<AbstractNbtNumber>("02").doubleValue(),
            list[3].expectType<AbstractNbtNumber>("10").doubleValue(),
            list[4].expectType<AbstractNbtNumber>("11").doubleValue(),
            list[5].expectType<AbstractNbtNumber>("12").doubleValue(),
            list[6].expectType<AbstractNbtNumber>("20").doubleValue(),
            list[7].expectType<AbstractNbtNumber>("21").doubleValue(),
            list[8].expectType<AbstractNbtNumber>("22").doubleValue()
        )
    }

    override fun serialize(value: MutableMatrix3d): NbtElement {
        return NbtList().also {
            it.add(NbtDouble.of(value.m00))
            it.add(NbtDouble.of(value.m01))
            it.add(NbtDouble.of(value.m02))
            it.add(NbtDouble.of(value.m10))
            it.add(NbtDouble.of(value.m11))
            it.add(NbtDouble.of(value.m12))
            it.add(NbtDouble.of(value.m20))
            it.add(NbtDouble.of(value.m21))
            it.add(NbtDouble.of(value.m22))
        }
    }
}

internal object Matrix4dSerializer: NbtSerializer<Matrix4d>() {
    override fun deserialize(tag: NbtElement): Matrix4d {
        val list = tag.expectType<NbtList>("tag")
        return Matrix4d(
            list[0].expectType<AbstractNbtNumber>("00").doubleValue(),
            list[1].expectType<AbstractNbtNumber>("01").doubleValue(),
            list[2].expectType<AbstractNbtNumber>("02").doubleValue(),
            list[3].expectType<AbstractNbtNumber>("03").doubleValue(),
            list[4].expectType<AbstractNbtNumber>("10").doubleValue(),
            list[5].expectType<AbstractNbtNumber>("11").doubleValue(),
            list[6].expectType<AbstractNbtNumber>("12").doubleValue(),
            list[7].expectType<AbstractNbtNumber>("13").doubleValue(),
            list[8].expectType<AbstractNbtNumber>("20").doubleValue(),
            list[9].expectType<AbstractNbtNumber>("21").doubleValue(),
            list[10].expectType<AbstractNbtNumber>("22").doubleValue(),
            list[11].expectType<AbstractNbtNumber>("23").doubleValue(),
            list[12].expectType<AbstractNbtNumber>("30").doubleValue(),
            list[13].expectType<AbstractNbtNumber>("31").doubleValue(),
            list[14].expectType<AbstractNbtNumber>("32").doubleValue(),
            list[15].expectType<AbstractNbtNumber>("33").doubleValue()
        )
    }

    override fun serialize(value: Matrix4d): NbtElement {
        return NbtList().also {
            it.add(NbtDouble.of(value.m00))
            it.add(NbtDouble.of(value.m01))
            it.add(NbtDouble.of(value.m02))
            it.add(NbtDouble.of(value.m03))
            it.add(NbtDouble.of(value.m10))
            it.add(NbtDouble.of(value.m11))
            it.add(NbtDouble.of(value.m12))
            it.add(NbtDouble.of(value.m13))
            it.add(NbtDouble.of(value.m20))
            it.add(NbtDouble.of(value.m21))
            it.add(NbtDouble.of(value.m22))
            it.add(NbtDouble.of(value.m23))
            it.add(NbtDouble.of(value.m30))
            it.add(NbtDouble.of(value.m31))
            it.add(NbtDouble.of(value.m32))
            it.add(NbtDouble.of(value.m33))
        }
    }
}

internal object MutableMatrix4dSerializer: NbtSerializer<MutableMatrix4d>() {
    override fun deserialize(tag: NbtElement): MutableMatrix4d {
        val list = tag.expectType<NbtList>("tag")
        return MutableMatrix4d().set(
            list[0].expectType<AbstractNbtNumber>("00").doubleValue(),
            list[1].expectType<AbstractNbtNumber>("01").doubleValue(),
            list[2].expectType<AbstractNbtNumber>("02").doubleValue(),
            list[3].expectType<AbstractNbtNumber>("03").doubleValue(),
            list[4].expectType<AbstractNbtNumber>("10").doubleValue(),
            list[5].expectType<AbstractNbtNumber>("11").doubleValue(),
            list[6].expectType<AbstractNbtNumber>("12").doubleValue(),
            list[7].expectType<AbstractNbtNumber>("13").doubleValue(),
            list[8].expectType<AbstractNbtNumber>("20").doubleValue(),
            list[9].expectType<AbstractNbtNumber>("21").doubleValue(),
            list[10].expectType<AbstractNbtNumber>("22").doubleValue(),
            list[11].expectType<AbstractNbtNumber>("23").doubleValue(),
            list[12].expectType<AbstractNbtNumber>("30").doubleValue(),
            list[13].expectType<AbstractNbtNumber>("31").doubleValue(),
            list[14].expectType<AbstractNbtNumber>("32").doubleValue(),
            list[15].expectType<AbstractNbtNumber>("33").doubleValue()
        )
    }

    override fun serialize(value: MutableMatrix4d): NbtElement {
        return NbtList().also {
            it.add(NbtDouble.of(value.m00))
            it.add(NbtDouble.of(value.m01))
            it.add(NbtDouble.of(value.m02))
            it.add(NbtDouble.of(value.m03))
            it.add(NbtDouble.of(value.m10))
            it.add(NbtDouble.of(value.m11))
            it.add(NbtDouble.of(value.m12))
            it.add(NbtDouble.of(value.m13))
            it.add(NbtDouble.of(value.m20))
            it.add(NbtDouble.of(value.m21))
            it.add(NbtDouble.of(value.m22))
            it.add(NbtDouble.of(value.m23))
            it.add(NbtDouble.of(value.m30))
            it.add(NbtDouble.of(value.m31))
            it.add(NbtDouble.of(value.m32))
            it.add(NbtDouble.of(value.m33))
        }
    }
}

internal object QuaternionSerializer: NbtSerializer<Quaternion>() {
    override fun deserialize(tag: NbtElement): Quaternion {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Quaternion(
            tag.expect<AbstractNbtNumber>("X").doubleValue(),
            tag.expect<AbstractNbtNumber>("Y").doubleValue(),
            tag.expect<AbstractNbtNumber>("Z").doubleValue(),
            tag.expect<AbstractNbtNumber>("W").doubleValue()
        )
    }

    override fun serialize(value: Quaternion): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtDouble.of(value.x))
        tag.put("Y", NbtDouble.of(value.y))
        tag.put("Z", NbtDouble.of(value.z))
        tag.put("W", NbtDouble.of(value.w))
        return tag
    }
}
