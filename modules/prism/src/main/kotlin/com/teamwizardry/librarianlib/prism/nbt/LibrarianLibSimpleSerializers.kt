package com.teamwizardry.librarianlib.prism.nbt

import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.DoubleNBT
import net.minecraft.nbt.INBT
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.nbt.NumberNBT

internal object Vec2dSerializer: NBTSerializer<Vec2d>() {
    override fun deserialize(tag: INBT, existing: Vec2d?): Vec2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec2d(
            tag.expect<NumberNBT>("X").double,
            tag.expect<NumberNBT>("Y").double
        )
    }

    override fun serialize(value: Vec2d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        return tag
    }
}

internal object Vec2iSerializer: NBTSerializer<Vec2i>() {
    override fun deserialize(tag: INBT, existing: Vec2i?): Vec2i {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Vec2i(
            tag.expect<NumberNBT>("X").int,
            tag.expect<NumberNBT>("Y").int
        )
    }

    override fun serialize(value: Vec2i): INBT {
        val tag = CompoundNBT()
        tag.put("X", IntNBT.valueOf(value.x))
        tag.put("Y", IntNBT.valueOf(value.y))
        return tag
    }
}

internal object Rect2dSerializer: NBTSerializer<Rect2d>() {
    override fun deserialize(tag: INBT, existing: Rect2d?): Rect2d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Rect2d(
            tag.expect<NumberNBT>("X").double,
            tag.expect<NumberNBT>("Y").double,
            tag.expect<NumberNBT>("Width").double,
            tag.expect<NumberNBT>("Height").double
        )
    }

    override fun serialize(value: Rect2d): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Width", DoubleNBT.valueOf(value.width))
        tag.put("Height", DoubleNBT.valueOf(value.height))
        return tag
    }
}

internal object Matrix3dSerializer: NBTSerializer<Matrix3d>() {
    override fun deserialize(tag: INBT, existing: Matrix3d?): Matrix3d {
        val list = tag.expectType<ListNBT>("tag")
        return Matrix3d(
            list[0].expectType<NumberNBT>("00").double,
            list[1].expectType<NumberNBT>("01").double,
            list[2].expectType<NumberNBT>("02").double,
            list[3].expectType<NumberNBT>("10").double,
            list[4].expectType<NumberNBT>("11").double,
            list[5].expectType<NumberNBT>("12").double,
            list[6].expectType<NumberNBT>("20").double,
            list[7].expectType<NumberNBT>("21").double,
            list[8].expectType<NumberNBT>("22").double
        )
    }

    override fun serialize(value: Matrix3d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value[0, 0]))
            it.add(DoubleNBT.valueOf(value[0, 1]))
            it.add(DoubleNBT.valueOf(value[0, 2]))
            it.add(DoubleNBT.valueOf(value[1, 0]))
            it.add(DoubleNBT.valueOf(value[1, 1]))
            it.add(DoubleNBT.valueOf(value[1, 2]))
            it.add(DoubleNBT.valueOf(value[2, 0]))
            it.add(DoubleNBT.valueOf(value[2, 1]))
            it.add(DoubleNBT.valueOf(value[2, 2]))
        }
    }
}

internal object MutableMatrix3dSerializer: NBTSerializer<MutableMatrix3d>() {
    override fun deserialize(tag: INBT, existing: MutableMatrix3d?): MutableMatrix3d {
        val list = tag.expectType<ListNBT>("tag")
        return (existing ?: MutableMatrix3d()).set(
            list[0].expectType<NumberNBT>("00").double,
            list[1].expectType<NumberNBT>("01").double,
            list[2].expectType<NumberNBT>("02").double,
            list[3].expectType<NumberNBT>("10").double,
            list[4].expectType<NumberNBT>("11").double,
            list[5].expectType<NumberNBT>("12").double,
            list[6].expectType<NumberNBT>("20").double,
            list[7].expectType<NumberNBT>("21").double,
            list[8].expectType<NumberNBT>("22").double
        )
    }

    override fun serialize(value: MutableMatrix3d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value[0, 0]))
            it.add(DoubleNBT.valueOf(value[0, 1]))
            it.add(DoubleNBT.valueOf(value[0, 2]))
            it.add(DoubleNBT.valueOf(value[1, 0]))
            it.add(DoubleNBT.valueOf(value[1, 1]))
            it.add(DoubleNBT.valueOf(value[1, 2]))
            it.add(DoubleNBT.valueOf(value[2, 0]))
            it.add(DoubleNBT.valueOf(value[2, 1]))
            it.add(DoubleNBT.valueOf(value[2, 2]))
        }
    }
}

internal object Matrix4dSerializer: NBTSerializer<Matrix4d>() {
    override fun deserialize(tag: INBT, existing: Matrix4d?): Matrix4d {
        val list = tag.expectType<ListNBT>("tag")
        return Matrix4d(
            list[0].expectType<NumberNBT>("00").double,
            list[1].expectType<NumberNBT>("01").double,
            list[2].expectType<NumberNBT>("02").double,
            list[3].expectType<NumberNBT>("03").double,
            list[4].expectType<NumberNBT>("10").double,
            list[5].expectType<NumberNBT>("11").double,
            list[6].expectType<NumberNBT>("12").double,
            list[7].expectType<NumberNBT>("13").double,
            list[8].expectType<NumberNBT>("20").double,
            list[9].expectType<NumberNBT>("21").double,
            list[10].expectType<NumberNBT>("22").double,
            list[11].expectType<NumberNBT>("23").double,
            list[12].expectType<NumberNBT>("30").double,
            list[13].expectType<NumberNBT>("31").double,
            list[14].expectType<NumberNBT>("32").double,
            list[15].expectType<NumberNBT>("33").double
        )
    }

    override fun serialize(value: Matrix4d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value[0, 0]))
            it.add(DoubleNBT.valueOf(value[0, 1]))
            it.add(DoubleNBT.valueOf(value[0, 2]))
            it.add(DoubleNBT.valueOf(value[0, 3]))
            it.add(DoubleNBT.valueOf(value[1, 0]))
            it.add(DoubleNBT.valueOf(value[1, 1]))
            it.add(DoubleNBT.valueOf(value[1, 2]))
            it.add(DoubleNBT.valueOf(value[1, 3]))
            it.add(DoubleNBT.valueOf(value[2, 0]))
            it.add(DoubleNBT.valueOf(value[2, 1]))
            it.add(DoubleNBT.valueOf(value[2, 2]))
            it.add(DoubleNBT.valueOf(value[2, 3]))
            it.add(DoubleNBT.valueOf(value[3, 0]))
            it.add(DoubleNBT.valueOf(value[3, 1]))
            it.add(DoubleNBT.valueOf(value[3, 2]))
            it.add(DoubleNBT.valueOf(value[3, 3]))
        }
    }
}

internal object MutableMatrix4dSerializer: NBTSerializer<MutableMatrix4d>() {
    override fun deserialize(tag: INBT, existing: MutableMatrix4d?): MutableMatrix4d {
        val list = tag.expectType<ListNBT>("tag")
        return (existing ?: MutableMatrix4d()).set(
            list[0].expectType<NumberNBT>("00").double,
            list[1].expectType<NumberNBT>("01").double,
            list[2].expectType<NumberNBT>("02").double,
            list[3].expectType<NumberNBT>("03").double,
            list[4].expectType<NumberNBT>("10").double,
            list[5].expectType<NumberNBT>("11").double,
            list[6].expectType<NumberNBT>("12").double,
            list[7].expectType<NumberNBT>("13").double,
            list[8].expectType<NumberNBT>("20").double,
            list[9].expectType<NumberNBT>("21").double,
            list[10].expectType<NumberNBT>("22").double,
            list[11].expectType<NumberNBT>("23").double,
            list[12].expectType<NumberNBT>("30").double,
            list[13].expectType<NumberNBT>("31").double,
            list[14].expectType<NumberNBT>("32").double,
            list[15].expectType<NumberNBT>("33").double
        )
    }

    override fun serialize(value: MutableMatrix4d): INBT {
        return ListNBT().also {
            it.add(DoubleNBT.valueOf(value[0, 0]))
            it.add(DoubleNBT.valueOf(value[0, 1]))
            it.add(DoubleNBT.valueOf(value[0, 2]))
            it.add(DoubleNBT.valueOf(value[0, 3]))
            it.add(DoubleNBT.valueOf(value[1, 0]))
            it.add(DoubleNBT.valueOf(value[1, 1]))
            it.add(DoubleNBT.valueOf(value[1, 2]))
            it.add(DoubleNBT.valueOf(value[1, 3]))
            it.add(DoubleNBT.valueOf(value[2, 0]))
            it.add(DoubleNBT.valueOf(value[2, 1]))
            it.add(DoubleNBT.valueOf(value[2, 2]))
            it.add(DoubleNBT.valueOf(value[2, 3]))
            it.add(DoubleNBT.valueOf(value[3, 0]))
            it.add(DoubleNBT.valueOf(value[3, 1]))
            it.add(DoubleNBT.valueOf(value[3, 2]))
            it.add(DoubleNBT.valueOf(value[3, 3]))
        }
    }
}

internal object QuaternionSerializer: NBTSerializer<Quaternion>() {
    override fun deserialize(tag: INBT, existing: Quaternion?): Quaternion {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundNBT>("tag")
        return Quaternion(
            tag.expect<NumberNBT>("X").double,
            tag.expect<NumberNBT>("Y").double,
            tag.expect<NumberNBT>("Z").double,
            tag.expect<NumberNBT>("W").double
        )
    }

    override fun serialize(value: Quaternion): INBT {
        val tag = CompoundNBT()
        tag.put("X", DoubleNBT.valueOf(value.x))
        tag.put("Y", DoubleNBT.valueOf(value.y))
        tag.put("Z", DoubleNBT.valueOf(value.z))
        tag.put("W", DoubleNBT.valueOf(value.w))
        return tag
    }
}
