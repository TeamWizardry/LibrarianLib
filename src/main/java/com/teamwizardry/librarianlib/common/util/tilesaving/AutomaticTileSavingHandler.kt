package com.teamwizardry.librarianlib.common.util.tilesaving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.reflect.Modifier
import java.util.*

/**
* @author WireSegal
* Created at 1:43 PM on 10/14/2016.
*/
object FieldCache : LinkedHashMap<Class<out TileMod>, Map<String, Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>>>() {
    @JvmStatic
    fun getClassFields(clazz: Class<out TileMod>): Map<String, Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>> {
        val existing = this[clazz]
        if (existing != null) return existing

        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            !Modifier.isStatic(it.modifiers) && it.isAnnotationPresent(Save::class.java)
        }

        val alreadyDone = mutableListOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")
        val map = mapOf(*(fields.map {
            it.isAccessible = true
            val string = it.getAnnotation(Save::class.java).saveName
            var name = if (string == "") it.name else string
            if (name in alreadyDone) {
                val msg = "Name $name already in use for class ${clazz.name}! Adding dashes to the end to mitigate this."
                val pad = Array(msg.length) { "*" }.joinToString("")
                LibrarianLog.warn(pad)
                LibrarianLog.warn(msg)
                LibrarianLog.warn(pad)
                while (name in alreadyDone)
                    name += "-"
            }
            alreadyDone.add(name)
            name to Triple(it.type,
                    MethodHandleHelper.wrapperForGetter<Any>(publicLookup().unreflectGetter(it)),
                    MethodHandleHelper.wrapperForSetter<Any>(publicLookup().unreflectSetter(it)))
        }).toTypedArray())

        put(clazz, map)

        return map
    }
}

object SerializationHandlers {
    private val map = HashMap<Class<*>, Pair<(Any?) -> NBTBase, (NBTBase) -> Any?>>()

    @Suppress("UNCHECKED_CAST")
    private fun <T: NBTBase> castNBTTag(tag: NBTBase, clazz: Class<T>): T {
        return (
                if (clazz.isAssignableFrom(tag.javaClass))
                    tag
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
                    throw IllegalArgumentException("Unknown NBT type to cast to")
        ) as T
    }

    init {
        mapHandler(Char::class.javaPrimitiveType!!, { NBTTagByte(it?.toByte() ?: 0) }, { castNBTTag(it, NBTPrimitive::class.java).byte.toChar() })
        mapHandler(Byte::class.javaPrimitiveType!!, { NBTTagByte(it ?: 0) }, { castNBTTag(it, NBTPrimitive::class.java).byte })
        mapHandler(Short::class.javaPrimitiveType!!, { NBTTagShort(it ?: 0) }, { castNBTTag(it, NBTPrimitive::class.java).short })
        mapHandler(Int::class.javaPrimitiveType!!, { NBTTagInt(it ?: 0) }, { castNBTTag(it, NBTPrimitive::class.java).int })
        mapHandler(Long::class.javaPrimitiveType!!, { NBTTagLong(it ?: 0) }, { castNBTTag(it, NBTPrimitive::class.java).long })
        mapHandler(Float::class.javaPrimitiveType!!, { NBTTagFloat(it ?: 0f) }, { castNBTTag(it, NBTPrimitive::class.java).float })
        mapHandler(Double::class.javaPrimitiveType!!, { NBTTagDouble(it ?: 0.0) }, { castNBTTag(it, NBTPrimitive::class.java).double })
        mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it ?: false) 1 else 0) }, { castNBTTag(it, NBTPrimitive::class.java).byte == 1.toByte() })

        mapHandler(Color::class.java, { NBTTagInt(it?.rgb ?: 0) }, { Color(castNBTTag(it, NBTPrimitive::class.java).int, true) })
        mapHandler(String::class.java, { NBTTagString(it ?: "") }, { castNBTTag(it, NBTTagString::class.java).string })
        mapHandler(NBTTagCompound::class.java, { it ?: NBTTagCompound() }, { castNBTTag(it, NBTTagCompound::class.java) })

        mapHandler(BlockPos::class.java, { NBTTagLong(it?.toLong() ?: 0) }, { BlockPos.fromLong(castNBTTag(it, NBTPrimitive::class.java).long) })
        mapHandler(Vec3d::class.java, {
            val list = NBTTagList()
            if (it != null) {
                list.appendTag(NBTTagDouble(it.xCoord))
                list.appendTag(NBTTagDouble(it.yCoord))
                list.appendTag(NBTTagDouble(it.zCoord))
            }
            list
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            if (tag.hasNoTags()) null
            else {
                val x = tag.getDoubleAt(0)
                val y = tag.getDoubleAt(1)
                val z = tag.getDoubleAt(2)
                Vec3d(x, y, z)
            }
        })

        mapHandler(Vec3i::class.java, {
            val list = NBTTagList()
            if (it != null) {
                list.appendTag(NBTTagInt(it.x))
                list.appendTag(NBTTagInt(it.y))
                list.appendTag(NBTTagInt(it.z))
            }
            list
        }, {
            val tag = castNBTTag(it, NBTTagList::class.java)
            if (tag.hasNoTags()) null
            else {
                val x = tag.getIntAt(0)
                val y = tag.getIntAt(1)
                val z = tag.getIntAt(2)
                Vec3i(x, y, z)
            }
        })

        mapHandler(ItemStack::class.java, { it?.serializeNBT() ?: NBTTagCompound() }, {
            val compound = castNBTTag(it, NBTTagCompound::class.java)
            if (compound.hasNoTags()) null
            else ItemStack.loadItemStackFromNBT(compound)
        })

        mapHandler(ItemStackHandler::class.java, { it?.serializeNBT() ?: NBTTagCompound() }, {
            val handler = ItemStackHandler()
            val compound = castNBTTag(it, NBTTagCompound::class.java)
            if (compound.hasNoTags())
                null
            else {
                handler.deserializeNBT(compound)
                handler
            }
        })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T?) -> NBTBase, reader: (NBTBase) -> T?) {
        map.put(clazz, (writer as (Any?) -> NBTBase) to (reader as (NBTBase) -> Any?))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((T?) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((Any?) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((NBTBase) -> T?)? {
        val pair = map[clazz] ?: return null
        return pair.second as (NBTBase) -> T?
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((NBTBase) -> Any?)? {
        val pair = map[clazz] ?: return null
        return pair.second
    }

}

/**
 * Apply this to a field to have it be serialized by the write/read nbt methods.
 */
@Target(AnnotationTarget.FIELD)
annotation class Save(val saveName: String = "")
