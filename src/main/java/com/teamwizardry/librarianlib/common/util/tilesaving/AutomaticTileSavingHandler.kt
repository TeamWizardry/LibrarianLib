package com.teamwizardry.librarianlib.common.util.tilesaving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.reflect.Modifier
import java.util.*

/**
* @author WireSegal and Elad
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

        val alreadyDone = mutableListOf<String>()
        val map = mapOf(*(fields.map {
            it.isAccessible = true
            val string = it.getAnnotation(Save::class.java).saveName
            val name = if (string == "") it.name else string
            if (name in alreadyDone) {
                val msg = "Name $name already in use for class ${clazz.name}! Some things may not be saved!"
                val pad = Array(msg.length) { "*" }.joinToString("")
                LibrarianLog.warn(pad)
                LibrarianLog.warn(msg)
                LibrarianLog.warn(pad)
            } else
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
    private val map = HashMap<Class<*>, Pair<(Any) -> NBTBase, (NBTBase) -> Any>>()

    init {
        mapHandler(Char::class.javaPrimitiveType!!, { NBTTagByte(it.toByte()) }, { (it as NBTPrimitive).byte.toChar() })
        mapHandler(Byte::class.javaPrimitiveType!!, ::NBTTagByte, { (it as NBTPrimitive).byte })
        mapHandler(Short::class.javaPrimitiveType!!, ::NBTTagShort, { (it as NBTPrimitive).short })
        mapHandler(Int::class.javaPrimitiveType!!, ::NBTTagInt, { (it as NBTPrimitive).int })
        mapHandler(Long::class.javaPrimitiveType!!, ::NBTTagLong, { (it as NBTPrimitive).long })
        mapHandler(Float::class.javaPrimitiveType!!, ::NBTTagFloat, { (it as NBTPrimitive).float })
        mapHandler(Double::class.javaPrimitiveType!!, ::NBTTagDouble, { (it as NBTPrimitive).double })
        mapHandler(Boolean::class.javaPrimitiveType!!, { NBTTagByte(if (it) 1 else 0) }, { (it as NBTPrimitive).byte == 1.toByte() })

        mapHandler(Color::class.java, { NBTTagInt(it.rgb) }, { Color((it as NBTPrimitive).int, true) })
        mapHandler(String::class.java, ::NBTTagString, { (it as NBTTagString).string })
        mapHandler(NBTTagCompound::class.java, { it }, { it as NBTTagCompound })
        mapHandler(ItemStack::class.java, { it.serializeNBT() }, { ItemStack.loadItemStackFromNBT(it as NBTTagCompound) })
        mapHandler(BlockPos::class.java, { NBTTagLong(it.toLong()) }, { BlockPos.fromLong((it as NBTPrimitive).long) })

        mapHandler(ItemStackHandler::class.java, { it.serializeNBT() }, {
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
