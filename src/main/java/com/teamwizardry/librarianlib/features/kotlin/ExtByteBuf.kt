@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.network.ByteBufUtils

// ByteBuf =============================================================================================================

fun ByteBuf.writeString(value: String) = ByteBufUtils.writeUTF8String(this, value)
fun ByteBuf.readString(): String = ByteBufUtils.readUTF8String(this)

fun ByteBuf.writeStack(value: ItemStack) = ByteBufUtils.writeItemStack(this, value)
fun ByteBuf.readStack(): ItemStack = ByteBufUtils.readItemStack(this)

fun ByteBuf.writeTag(value: NBTTagCompound) = ByteBufUtils.writeTag(this, value)
fun ByteBuf.readTag(): NBTTagCompound? = ByteBufUtils.readTag(this)

fun ByteBuf.writeVarInt(value: Int) {
    var input = value
    while (input and -128 != 0) {
        this.writeByte(input and 127 or 128)
        input = input ushr 7
    }

    this.writeByte(input)
}

fun ByteBuf.readVarInt(): Int {
    var i = 0
    var j = 0

    while (true) {
        val b0 = this.readByte()
        i = i or (b0.toInt() and 127 shl j++ * 7)

        if (j > 5) {
            throw RuntimeException("VarInt too big")
        }

        if (b0.toInt() and 128 != 128) {
            break
        }
    }

    return i
}

fun ByteBuf.writeVarLong(value: Long) {
    var input = value
    while (input and -128L != 0L) {
        this.writeByte((input and 127L).toInt() or 128)
        input = value ushr 7
    }

    this.writeByte(value.toInt())
}

fun ByteBuf.readVarLong(): Long {
    var i = 0L
    var j = 0

    while (true) {
        val b0 = this.readByte()
        i = i or ((b0.toInt() and 127).toLong() shl j++ * 7)

        if (j > 10) {
            throw RuntimeException("VarLong too big")
        }

        if (b0.toInt() and 128 != 128) {
            break
        }
    }

    return i
}

fun ByteBuf.writeBooleanArray(value: BooleanArray) {
    val len = value.size
    this.writeVarInt(len)

    val toReturn = ByteArray((len + 7) / 8) // +7 to round up
    for (entry in toReturn.indices) {
        (0..7)
                .filter { entry * 8 + it < len && value[entry * 8 + it] }
                .forEach { toReturn[entry] = (toReturn[entry].toInt() or (128 shr it)).toByte() }
    }
    this.writeBytes(toReturn)
}

fun ByteBuf.readBooleanArray(tryReadInto: BooleanArray? = null): BooleanArray {
    val len = this.readVarInt()
    val bytes = ByteArray((len + 7) / 8)
    this.readBytes(bytes)

    val toReturn = if (tryReadInto != null && tryReadInto.size == len) tryReadInto else BooleanArray(len)
    for (entry in bytes.indices) {
        for (bit in 0..7) {
            val bitThing = bytes[entry].toInt() and (128 shr bit)
            if (entry * 8 + bit < len && bitThing != 0) {
                toReturn[entry * 8 + bit] = true
            }
        }
    }
    return toReturn
}

fun ByteBuf.writeNullSignature() {
    writeBoolean(true)
}

fun ByteBuf.writeNonnullSignature() {
    writeBoolean(false)
}

fun ByteBuf.hasNullSignature(): Boolean = readBoolean()

fun ByteBuf.writeFluidStack(value: FluidStack) {
    this.writeString(FluidRegistry.getFluidName(value.fluid))
    this.writeInt(value.amount)
    if (value.tag != null) {
        this.writeBoolean(true)
        this.writeTag(value.tag)
    } else this.writeBoolean(false)
}

fun ByteBuf.readFluidStack(): FluidStack? {
    val fluid = FluidRegistry.getFluid(this.readString())
    return if (fluid != null) {
        val amount = this.readInt()
        if (this.readBoolean()) FluidStack(fluid, amount, this.readTag()) else FluidStack(fluid, amount)
    } else null
}
