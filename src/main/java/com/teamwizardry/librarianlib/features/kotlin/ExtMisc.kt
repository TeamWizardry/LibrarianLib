@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketSpamlessMessage
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.ChunkCache
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistryEntry
import java.lang.reflect.*
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.kotlinFunction

@Suppress("FunctionName")
@SideOnly(Side.CLIENT)
fun Minecraft(): Minecraft = Minecraft.getMinecraft()

fun Int.abs() = if (this < 0) -this else this

operator fun TextFormatting.plus(str: String) = "$this$str"
operator fun TextFormatting.plus(other: TextFormatting) = "$this$other"

fun String.localize(vararg parameters: Any) = LibrarianLib.PROXY.translate(this, *parameters)

fun String.canLocalize() = LibrarianLib.PROXY.canTranslate(this)

fun String.extract(regex: Regex, group: Int = 1, default: String = "") = regex.find(this)?.groupValues?.get(group) ?: default
fun String.extract(regex: String, group: Int = 1, default: String = "") = this.extract(regex.toRegex(), group, default)

fun String.toRl(): ResourceLocation = ResourceLocation(this)
val missingno = ResourceLocation("minecraft:missingno")
val IForgeRegistryEntry<*>.key get() = registryName ?: missingno

fun <K, V> MutableMap<K, V>.withRealDefault(default: (K) -> V): DefaultedMutableMap<K, V> {
    return when (this) {
        is RealDefaultImpl -> RealDefaultImpl(this.map, default)
        else -> RealDefaultImpl(this, default)
    }
}

interface DefaultedMutableMap<K, V> : MutableMap<K, V> {
    override fun get(key: K): V
}

private class RealDefaultImpl<K, V>(val map: MutableMap<K, V>, val default: (K) -> V) : DefaultedMutableMap<K, V>, MutableMap<K, V> by map {
    override fun get(key: K): V {
        return map.getOrPut(key) { default(key) }
    }
}

// World

fun World.collideAABB(boundingBox: AxisAlignedBB, offset: Vec3d, entity: Entity? = null): Vec3d {
    var bbSoFar = boundingBox
    var x = offset.x
    var y = offset.y
    var z = offset.z

    val list1 = this.getCollisionBoxes(entity, boundingBox.expand(x, y, z))

    list1.forEach { y = it.calculateYOffset(bbSoFar, y) }
    bbSoFar = bbSoFar.offset(0.0, y, 0.0)
    list1.forEach { x = it.calculateXOffset(bbSoFar, x) }
    bbSoFar = bbSoFar.offset(x, 0.0, 0.0)
    list1.forEach { z = it.calculateZOffset(bbSoFar, z) }
    bbSoFar = bbSoFar.offset(0.0, 0.0, z)

    return vec(x, y, z)
}

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

private val singletonMap = IdentityHashMap<Class<*>, Any?>()

/**
 * Searches for one of a few things:
 *
 * * If the class is a Kotlin object it will get the instance of it
 * * If the class has a static final field named `INSTANCE` with the same type as this class, gets the array of that field (if it is null, returns null)
 * * If the class has a zero-argument constructor, instantiates an instance of the class
 * * If none of these requirements was fulfilled or if the `INSTANCE` field contained null, returns null.
 *
 * After the first time this property is accessed for a class, its instance is cached for faster lookups.
 */
val <T : Any> Class<T>.singletonInstance: T?
    get() {
        @Suppress("UNCHECKED_CAST")
        if (this in singletonMap) return singletonMap[this] as T?

        val kt = this.kotlin.objectInstance
        if (kt != null) {
            singletonMap[this] = kt
            return kt
        }

        val field = this.declaredFields.find {
            Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers) && it.name == "INSTANCE" && it.type == this
        }

        if (field != null) {
            val value = field.get(null)
            singletonMap[this] = value
            @Suppress("UNCHECKED_CAST")
            return value as T?
        }

        try {
            val constructor = this.getConstructor()
            val value = constructor.newInstance()
            singletonMap[this] = value
            return value
        } catch (e: NoSuchMethodException) {
            // NOOP
        }

        singletonMap[this] = null
        return null
    }
// Player ==============================================================================================================

fun EntityPlayer.sendMessage(str: String, actionBar: Boolean = false)
        = sendStatusMessage(str.toComponent(), actionBar)

fun EntityPlayer.sendSpamlessMessage(str: String, channelName: String)
        = sendSpamlessMessage(str, channelName.hashCode())

fun EntityPlayer.sendSpamlessMessage(comp: ITextComponent, channelName: String)
        = sendSpamlessMessage(comp, channelName.hashCode())

fun EntityPlayer.sendSpamlessMessage(str: String, uniqueId: Int)
        = sendSpamlessMessage(str.toComponent(), uniqueId)

fun EntityPlayer.sendSpamlessMessage(comp: ITextComponent, uniqueId: Int) {
    val packet = PacketSpamlessMessage(comp, uniqueId)
    if (this is EntityPlayerMP)
        PacketHandler.NETWORK.sendTo(packet, this)
    else
        LibrarianLib.PROXY.runIfClient(packet)
}

fun Entity.setVelocityAndUpdate(vec: Vec3d) = setVelocityAndUpdate(vec.x, vec.y, vec.z)
fun Entity.setVelocityAndUpdate(x: Double = motionX, y: Double = motionY, z: Double = motionZ) {
    motionX = x
    motionY = y
    motionZ = z
    if (this is EntityPlayerMP)
        connection.sendPacket(SPacketEntityVelocity(this))
}

var Entity.motionVec: Vec3d
    get() = Vec3d(motionX, motionY, motionZ)
    set(value) {
        this.motionX = value.x
        this.motionY = value.y
        this.motionZ = value.z
    }

// String ==============================================================================================================

operator fun CharSequence.times(n: Int) = this.repeat(n)
operator fun Int.times(n: CharSequence) = n.repeat(this)
fun String.toComponent() = TextComponentString(this)

// ICapabilityProvider ==============================================================================================================
fun <T, R> ICapabilityProvider.ifCap(capability: Capability<T>, facing: EnumFacing?, callback: (T) -> R): R? {
    if (this.hasCapability(capability, facing))
        return callback(this.getCapability(capability, facing)!!)
    return null
}

// Relating to NonNullList =============================================================================================
private class FakeNonnullList<T : Any>(delegate: MutableList<T>) : NonNullList<T>(delegate, null) {
    constructor(delegate: MutableList<T?>, default: T) : this(delegate.map { it ?: default }.toMutableList())
}

fun <T : Any> Iterable<T>.toNonnullList() = toMutableList().asNonnullList()
fun <T : Any> Iterable<T?>.toNonnullList(default: T): NonNullList<T> = toMutableList().asNonnullList(default)
fun <T : Any> kotlin.Array<T>.toNonnullList() = toMutableList().asNonnullList()
fun <T : Any> kotlin.Array<T?>.toNonnullList(default: T): NonNullList<T> = toMutableList().asNonnullList(default)
fun ByteArray.toNonnullList() = toMutableList().asNonnullList()
fun ShortArray.toNonnullList() = toMutableList().asNonnullList()
fun IntArray.toNonnullList() = toMutableList().asNonnullList()
fun LongArray.toNonnullList() = toMutableList().asNonnullList()
fun BooleanArray.toNonnullList() = toMutableList().asNonnullList()
fun FloatArray.toNonnullList() = toMutableList().asNonnullList()
fun DoubleArray.toNonnullList() = toMutableList().asNonnullList()
fun CharArray.toNonnullList() = toMutableList().asNonnullList()
fun CharSequence.toNonnullList() = toMutableList().asNonnullList()
fun <T : Any> MutableList<T>.asNonnullList(): NonNullList<T> = FakeNonnullList(this)
fun <T : Any> MutableList<T?>.asNonnullList(default: T): NonNullList<T> = FakeNonnullList(this, default)

fun <T : Any> Iterable<T>.nullable() = toMutableList<T?>()
fun <T : Any> kotlin.Array<T>.nullable() = toMutableList<T?>()


// ItemStack ===========================================================================================================

fun <C : ICapabilityProvider, T, R> C.forCap(capability: Capability<T>?, facing: EnumFacing?, callback: (T) -> R): R? {
    if (capability != null && this.hasCapability(capability, facing))
        return callback(this.getCapability(capability, facing)!!)
    return null
}

val ItemStack.toolClasses: Set<String> get() = item.getToolClasses(this)

val ItemStack.isNotEmpty get() = !this.isEmpty

// Item ===========================================================================================================

fun Item.toStack(amount: Int = 1, meta: Int = 0) = ItemStack(this, amount, meta)

// Block ===========================================================================================================

fun Block.toStack(amount: Int = 1, meta: Int = 0) = ItemStack(this, amount, meta)

// Numbers =============================================================================================================

fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Short.clamp(min: Short, max: Short): Short = if (this < min) min else if (this > max) max else this
fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
fun Byte.clamp(min: Byte, max: Byte): Byte = if (this < min) min else if (this > max) max else this
fun Char.clamp(min: Char, max: Char): Char = if (this < min) min else if (this > max) max else this
fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this

// IBlockAccess ========================================================================================================

fun IBlockAccess.getTileEntitySafely(pos: BlockPos)
        = if (this is ChunkCache) this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) else this.getTileEntity(pos)

// listOf and mapOf ====================================================================================================

inline fun <reified K : Enum<K>, V> enumMapOf(): EnumMap<K, V> {
    return EnumMap(K::class.java)
}

inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>): EnumMap<K, V> {
    val map = enumMapOf<K, V>()
    map.putAll(pairs)
    return map
}

// Collections =========================================================================================================

fun <T : Any, E : Any, R : Collection<T>, F : Collection<E>> R.instanceOf(collection: F): Boolean {
    return javaClass.isAssignableFrom(collection.javaClass) && (this.isNotEmpty() && collection.isNotEmpty() && elementAt(0).javaClass.isAssignableFrom(collection.elementAt(0).javaClass))
}

// IBlockState =========================================================================================================

operator fun <T : Comparable<T>> IBlockState.get(value: IProperty<T>): T = getValue(value)

/**
 * Replaces [kotlin.reflect.jvm]'s Method.kotlinFunction to fix a crash with protected methods.
 * Effectiveness on protected methods might be a bit approximate (should be pretty accurate for most cases though),
 *  but is still better than an outright crash (imo).
 *
 * TODO: replace with Method.kotlinFunction once https://youtrack.jetbrains.com/issue/KT-17423 is resolved
 */
@Deprecated(message = "Official one has been fixed.", replaceWith = ReplaceWith("this.kotlinFunction", "kotlin.reflect.jvm.kotlinFunction"))
val Method.kotlinFunctionSafe: KFunction<*>?
    get() = kotlinFunction

/**
 * Checks whether a [Parameter] [kotlin.Array] matches a [KParameter] [kotlin.collections.List]
 */
fun kotlin.Array<Parameter>.matches(other: kotlin.collections.List<KParameter>): Boolean {
    if (size != other.size) return false
    var ok = true
    this.forEachIndexed { i, it ->
        ok = other[i].type == it.type.kotlin.starProjectedType
        if (!ok) return@forEachIndexed
    }
    return ok
}

inline fun <T : NBTBase> NBTTagList(size: Int, generator: (Int) -> T): NBTTagList {
    val list = NBTTagList()
    for (i in 0 until size)
        list.appendTag(generator(i))
    return list
}

inline fun NBTTagCompound(size: Int, generator: (Int) -> Pair<String, NBTBase>): NBTTagCompound {
    val list = NBTTagCompound()
    for (i in 0 until size) {
        val (key, value) = generator(i)
        list.setTag(key, value)
    }
    return list
}

inline fun NBTTagCompound.forEach(code: (key: String, value: NBTBase) -> Unit) {
    for (key in keySet)
        code(key, getTag(key))
}

inline fun <T> whileNonNull(statement: () -> T?, body: (T) -> Unit) {
    var value = statement()
    while(value != null) {
        body(value)
        value = statement()
    }
}
