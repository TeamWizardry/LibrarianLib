@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.common.network.ByteBufUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type



/**
 * Created by TheCodeWarrior
 */

fun Int.abs() = if (this < 0) -this else this

operator fun TextFormatting.plus(str: String) = "$this$str"

operator fun String.plus(form: TextFormatting) = "$this$form"
operator fun TextFormatting.plus(other: TextFormatting) = "$this$other"

fun String.localize(vararg parameters: Any): String {
    return LibrarianLib.PROXY.translate(this, *parameters)
}

fun String.canLocalize(): Boolean {
    return LibrarianLib.PROXY.canTranslate(this)
}

fun <K, V> MutableMap<K, V>.withRealDefault(default: (K) -> V): DefaultedMutableMap<K, V> {
    return when(this) {
        is RealDefaultImpl -> RealDefaultImpl(this.map, default)
        else -> RealDefaultImpl(this, default)
    }
}

interface DefaultedMutableMap<K, V> : MutableMap<K, V> {
    override fun get(key: K): V
}

private class RealDefaultImpl<K, V>(val map: MutableMap<K, V>, val default: (K) -> V) : DefaultedMutableMap<K, V>, MutableMap<K, V> by map {
    override fun get(key: K): V {
        return map.getOrPut(key, { default(key) })
    }
}

// Vec3d ===============================================================================================================

operator fun Vec3d.times(other: Vec3d): Vec3d = Vec3d(this.xCoord * other.xCoord, this.yCoord * other.yCoord, this.zCoord * other.zCoord)
operator fun Vec3d.times(other: Double): Vec3d = this.scale(other)
operator fun Vec3d.times(other: Float): Vec3d = this * other.toDouble()
operator fun Vec3d.times(other: Int): Vec3d = this * other.toDouble()

operator fun Vec3d.div(other: Vec3d) = Vec3d(this.xCoord / other.xCoord, this.yCoord / other.yCoord, this.zCoord / other.zCoord)
operator fun Vec3d.div(other: Double): Vec3d = this * (1 / other)
operator fun Vec3d.div(other: Float): Vec3d = this / other.toDouble()
operator fun Vec3d.div(other: Int): Vec3d = this / other.toDouble()

operator fun Vec3d.plus(other: Vec3d): Vec3d = this.add(other)
operator fun Vec3d.minus(other: Vec3d): Vec3d = this.subtract(other)
operator fun Vec3d.unaryMinus(): Vec3d = this * -1.0

infix fun Vec3d.dot(other: Vec3d) = this.dotProduct(other)

infix fun Vec3d.cross(other: Vec3d) = this.crossProduct(other)

fun Vec3d.withX(other: Double) = Vec3d(other, this.yCoord, this.zCoord)
fun Vec3d.withY(other: Double) = Vec3d(this.xCoord, other, this.zCoord)
fun Vec3d.withZ(other: Double) = Vec3d(this.xCoord, this.yCoord, other)

fun Vec3d.withX(other: Float) = this.withX(other.toDouble())
fun Vec3d.withY(other: Float) = this.withY(other.toDouble())
fun Vec3d.withZ(other: Float) = this.withZ(other.toDouble())

fun Vec3d.withX(other: Int) = this.withX(other.toDouble())
fun Vec3d.withY(other: Int) = this.withY(other.toDouble())
fun Vec3d.withZ(other: Int) = this.withZ(other.toDouble())


// Vec2d ===============================================================================================================

operator fun Vec2d.times(other: Vec2d) = this.mul(other)
operator fun Vec2d.times(other: Double) = this.mul(other)
operator fun Vec2d.times(other: Float) = this * other.toDouble()
operator fun Vec2d.times(other: Int) = this * other.toDouble()

operator fun Vec2d.div(other: Vec2d) = this.divide(other)
operator fun Vec2d.div(other: Double) = this.divide(other)
operator fun Vec2d.div(other: Float) = this / other.toDouble()
operator fun Vec2d.div(other: Int) = this / other.toDouble()

operator fun Vec2d.plus(other: Vec2d) = this.add(other)
operator fun Vec2d.minus(other: Vec2d) = this.add(other)
operator fun Vec2d.unaryMinus() = this * -1

// AxisAlignedBB =======================================================================================================

operator fun AxisAlignedBB.contains(other: Vec3d) =
        this.minX <= other.xCoord && this.maxX >= other.xCoord &&
                this.minY <= other.yCoord && this.maxY >= other.yCoord &&
                this.minZ <= other.zCoord && this.maxZ >= other.zCoord

// Class ===============================================================================================================

fun <T> Class<T>.genericType(index: Int): Type? {
    val genericSuper = genericSuperclass
    return if (genericSuper is ParameterizedType) {
        val args = genericSuper.actualTypeArguments
        if (0 <= index && index < args.size)
            genericSuper.actualTypeArguments[index]
        else null
    } else null
}

fun <T> Class<T>.genericClass(index: Int): Class<*>? {
    val generic = genericType(index) ?: return null
    return if (generic is Class<*>) generic else null
}

@Suppress("UNCHECKED_CAST")
fun <T, O> Class<T>.genericClassTyped(index: Int) = genericClass(index) as Class<O>?

// ByteBuf =============================================================================================================

fun ByteBuf.writeString(value: String) = ByteBufUtils.writeUTF8String(this, value)
fun ByteBuf.readString(): String = ByteBufUtils.readUTF8String(this)

fun ByteBuf.writeStack(value: ItemStack) = ByteBufUtils.writeItemStack(this, value)
fun ByteBuf.readStack(): ItemStack = ByteBufUtils.readItemStack(this)

fun ByteBuf.writeTag(value: NBTTagCompound) = ByteBufUtils.writeTag(this, value)
fun ByteBuf.readTag(): NBTTagCompound = ByteBufUtils.readTag(this)

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
        for (bit in 0..7) {
            if (entry * 8 + bit < len && value[entry * 8 + bit]) {
                toReturn[entry] = (toReturn[entry].toInt() or (128 shr bit)).toByte()
            }
        }
    }
    this.writeBytes(toReturn)
}

fun ByteBuf.readBooleanArray(tryReadInto: BooleanArray? = null): BooleanArray {
    val len = this.readVarInt()
    val bytes = ByteArray((len + 7)/ 8)
    this.readBytes(bytes)

    val toReturn = if(tryReadInto != null && tryReadInto.size == len) tryReadInto else BooleanArray(len)
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

// NBTTagList ==========================================================================================================

val NBTTagList.indices: IntRange
    get() = 0..this.tagCount() - 1

operator fun NBTTagList.iterator(): Iterator<NBTBase> {
    return object : Iterator<NBTBase> {
        var i = 0
        val max = this@iterator.tagCount() - 1
        override fun hasNext() = i < max
        override fun next() = this@iterator[i++]
    }
}

fun <T : NBTBase> NBTTagList.forEach(run: (T) -> Unit) {
    for (i in this.indices) {
        @Suppress("UNCHECKED_CAST")
        run(this.get(i) as T)
    }
}

fun <T : NBTBase> NBTTagList.forEachIndexed(run: (Int, T) -> Unit) {
    for (i in this.indices) {
        @Suppress("UNCHECKED_CAST")
        run(i, this.get(i) as T)
    }
}

// NBT

@Suppress("UNCHECKED_CAST")
fun <T : NBTBase> NBTBase.safeCast(clazz: Class<T>): T {
    return (
            if (clazz.isAssignableFrom(this.javaClass))
                this
            else if (clazz == NBTPrimitive::class.java)
                NBTTagByte(0)
            else if (clazz == NBTTagByteArray::class.java)
                NBTTagByteArray(ByteArray(0))
            else if (clazz == NBTTagString::class.java)
                NBTTagString("")
            else if (clazz == NBTTagList::class.java)
                NBTTagList()
            else if (clazz == NBTTagCompound::class.java)
                NBTTagCompound()
            else if (clazz == NBTTagIntArray::class.java)
                NBTTagIntArray(IntArray(0))
            else
                throw IllegalArgumentException("Unknown NBT type to cast to: $clazz")
            ) as T
}

// NBTTagCompound ======================================================================================================

operator fun NBTTagCompound.iterator(): Iterator<Pair<String, NBTBase>> {
    return object : Iterator<Pair<String, NBTBase>> {
        val keys = this@iterator.keySet.iterator()
        override fun hasNext() = keys.hasNext()
        override fun next(): Pair<String, NBTBase> {
            val next = keys.next()
            return next to this@iterator[next]
        }
    }
}

operator fun NBTTagCompound.get(key: String): NBTBase = this.getTag(key)

// Player ==============================================================================================================

fun EntityPlayer.sendMessage(str: String) {
    this.sendStatusMessage(TextComponentString(str))
}

// String ==============================================================================================================

operator fun CharSequence.times(n: Int) = this.repeat(n)
operator fun Int.times(n: CharSequence) = n.repeat(this)

fun <T, R> ICapabilityProvider.ifCap(capability: Capability<T>, facing: EnumFacing?, callback: (T) -> R): R? {
    if(this.hasCapability(capability, facing))
        return callback(this.getCapability(capability, facing)!!)
    return null
}
