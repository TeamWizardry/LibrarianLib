package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.*
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import java.awt.Color
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.reflect.Modifier
import java.util.*

/**
* @author WireSegal
* Created at 1:43 PM on 10/14/2016.
*/
object MessageFieldCache : LinkedHashMap<Class<out PacketBase>, List<Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>>>() {
    @JvmStatic
    fun getClassFields(clazz: Class<out PacketBase>): List<Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>> {
        val existing = this[clazz]
        if (existing != null) return existing

        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            val mods = it.modifiers
            !Modifier.isStatic(mods) && !Modifier.isFinal(mods) && !Modifier.isTransient(mods) && it.isAnnotationPresent(Save::class.java)
        }

        val alreadyDone = mutableListOf<String>()

        val map = fields.sortedBy {
            val string = it.getAnnotation(Save::class.java).saveName
            var name = if (string == "") it.name else string
            while (name in alreadyDone)
                name += "-"
            alreadyDone.add(name)
            name
        }.map {
            it.isAccessible = true
            Triple(it.type,
                    MethodHandleHelper.wrapperForGetter<Any>(publicLookup().unreflectGetter(it)),
                    MethodHandleHelper.wrapperForSetter<Any>(publicLookup().unreflectSetter(it)))
        }

        put(clazz, map)

        return map
    }
}

object MessageSerializationHandlers {
    private val map = HashMap<Class<*>, Pair<(ByteBuf, Any) -> Unit, (ByteBuf) -> Any>>()

    init {
        // Primitives and String
        mapHandler(Char::class.javaPrimitiveType!!, { buf, obj -> buf.writeChar(obj.toInt()) }, ByteBuf::readChar)
        mapHandler(Byte::class.javaPrimitiveType!!, { buf, obj -> buf.writeByte(obj.toInt()) }, ByteBuf::readByte)
        mapHandler(Short::class.javaPrimitiveType!!, { buf, obj -> buf.writeShort(obj.toInt()) }, ByteBuf::readShort)
        mapHandler(Int::class.javaPrimitiveType!!, ByteBuf::writeInt, ByteBuf::readInt)
        mapHandler(Long::class.javaPrimitiveType!!, ByteBuf::writeLong, ByteBuf::readLong)

        mapHandler(Float::class.javaPrimitiveType!!, ByteBuf::writeFloat, ByteBuf::readFloat)
        mapHandler(Double::class.javaPrimitiveType!!, ByteBuf::writeDouble, ByteBuf::readDouble)
        mapHandler(Boolean::class.javaPrimitiveType!!, ByteBuf::writeBoolean, ByteBuf::readBoolean)
        mapHandler(String::class.java, ByteBuf::writeString, ByteBuf::readString)

        // Misc.
        mapHandler(Color::class.java, { buf, obj -> buf.writeInt(obj.rgb) }, { Color(it.readInt(), true) })
        mapHandler(NBTTagCompound::class.java, ByteBuf::writeTag, ByteBuf::readTag)
        mapHandler(ItemStack::class.java, ByteBuf::writeStack, ByteBuf::readStack)

        // Vectors
        mapHandler(Vec3d::class.java, {
            buf, obj -> buf.writeDouble(obj.xCoord).writeDouble(obj.yCoord).writeDouble(obj.zCoord)
        }, {
            Vec3d(it.readDouble(), it.readDouble(), it.readDouble())
        })
        mapHandler(Vec3i::class.java, {
            buf, obj -> buf.writeInt(obj.x).writeInt(obj.y).writeInt(obj.z)
        }, {
            Vec3i(it.readInt(), it.readInt(), it.readInt())
        })
        mapHandler(Vec2d::class.java, {
            buf, obj -> buf.writeDouble(obj.x).writeDouble(obj.y)
        }, {
            Vec2d(it.readDouble(), it.readDouble())
        })
        mapHandler(BlockPos::class.java, { buf, obj -> buf.writeLong(obj.toLong()) }, { BlockPos.fromLong(it.readLong()) })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapHandler(clazz: Class<T>, writer: (ByteBuf, T) -> Any?, reader: (ByteBuf) -> T) {
        map.put(clazz, ({ buf: ByteBuf, obj: Any -> writer(buf, obj as T) } as (ByteBuf, Any) -> Unit) to (reader as (ByteBuf) -> Any))
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getWriter(clazz: Class<T>): ((ByteBuf, T) -> Unit)? {
        val pair = map[clazz] ?: return null
        return pair.first as (ByteBuf, T) -> Unit
    }

    @JvmStatic
    fun getWriterUnchecked(clazz: Class<*>): ((ByteBuf, Any) -> Unit)? {
        val pair = map[clazz] ?: return null
        return pair.first
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> getReader(clazz: Class<T>): ((ByteBuf) -> T)? {
        val pair = map[clazz] ?: return null
        return pair.second as (ByteBuf) -> T
    }

    @JvmStatic
    fun getReaderUnchecked(clazz: Class<*>): ((ByteBuf) -> Any)? {
        val pair = map[clazz] ?: return null
        return pair.second
    }

}
