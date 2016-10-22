package com.teamwizardry.librarianlib.common.util.tilesaving

import com.teamwizardry.librarianlib.common.base.block.TileMod
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler
import java.lang.reflect.Field
import java.util.*

/**
* @author WireSegal and Elad
* Created at 1:43 PM on 10/14/2016.
*/
object FieldCache : LinkedHashMap<Class<out TileMod>, Map<String, Field>>() {
    @JvmStatic
    fun getClassFields(clazz: Class<out TileMod>): Map<String, Field> {
        val existing = this[clazz]
        if (existing != null) return existing

        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            it.isAnnotationPresent(Save::class.java)
        }

        val map = mapOf(*(fields.map {
            it.isAccessible = true
            val string = it.getAnnotation(Save::class.java).saveName
            (if (string == "") it.name else string) to it
        }).toTypedArray())

        put(clazz, map)

        return map
    }
}
object SerializationHandlers {
    private val map = HashMap<Class<*>, Pair<(Any) -> NBTBase, (NBTBase) -> Any>>()

    init {
        mapHandler(Byte::class.javaPrimitiveType!!, ::NBTTagByte, { (it as NBTPrimitive).byte })
        mapHandler(Short::class.javaPrimitiveType!!, ::NBTTagShort, {(it as NBTPrimitive).short})
        mapHandler(Int::class.javaPrimitiveType!!, ::NBTTagInt, {(it as NBTPrimitive).int})
        mapHandler(Long::class.javaPrimitiveType!!, ::NBTTagLong, {(it as NBTPrimitive).long})
        mapHandler(Float::class.javaPrimitiveType!!, ::NBTTagFloat, {(it as NBTPrimitive).float})
        mapHandler(Double::class.javaPrimitiveType!!, ::NBTTagDouble, {(it as NBTPrimitive).double})
        mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it) 1 else 0)}, {(it as NBTPrimitive).byte == 1.toByte()})

        mapHandler(String::class.java, ::NBTTagString, {(it as NBTTagString).string})
        mapHandler(NBTTagCompound::class.java, {it}, {it as NBTTagCompound})
        mapHandler(ItemStack::class.java, {it.serializeNBT()}, { ItemStack.loadItemStackFromNBT(it as NBTTagCompound)})
        mapHandler(BlockPos::class.java, {NBTTagLong(it.toLong())}, {BlockPos.fromLong((it as NBTPrimitive).long)})

        mapHandler(ItemStackHandler::class.java, {it.serializeNBT()}, {
            val handler = ItemStackHandler()
            handler.deserializeNBT(it as NBTTagCompound)
            handler
        })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (T) -> NBTBase, reader: (NBTBase) -> T) {
        map.put(clazz, (writer as (Any) -> NBTBase) to (reader as (NBTBase) -> Any))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((T) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first as (T) -> NBTBase
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((Any) -> NBTBase)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((NBTBase) -> T)? {
        val pair = map[clazz] ?: return null
        return pair.second as (NBTBase) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((NBTBase) -> Any)? {
        val pair = map[clazz] ?: return null
        return pair.second
    }

}

/**
 * Apply this to a field to have it be serialized by the write/read nbt methods.
 */
@Target(AnnotationTarget.FIELD)
annotation class Save(val saveName: String = "")
