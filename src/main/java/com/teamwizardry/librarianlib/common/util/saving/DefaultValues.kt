package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import java.awt.Color
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object DefaultValues {
    val map = hashMapOf<FieldType, () -> Any>()
    val classMap = hashMapOf<Class<*>, () -> Any>()
    private val specialHandlers = mutableListOf<(FieldType) -> (() -> Any)?>()
    private val genericHandlers = mutableListOf<(FieldTypeGeneric) -> (() -> Any)?>()

    init {
        mapDefault(Boolean::class.javaPrimitiveType!!, false)
        mapDefault(Boolean::class.javaObjectType, false)
        mapDefault(BooleanArray::class.java, booleanArrayOf())

        mapDefault(Char::class.javaPrimitiveType!!, 0.toChar())
        mapDefault(Byte::class.javaPrimitiveType!!, 0.toByte())
        mapDefault(Short::class.javaPrimitiveType!!, 0.toShort())
        mapDefault(Int::class.javaPrimitiveType!!, 0)
        mapDefault(Long::class.javaPrimitiveType!!, 0.toLong())

        mapDefault(Char::class.javaObjectType, 0.toChar())
        mapDefault(Byte::class.javaObjectType, 0.toByte())
        mapDefault(Short::class.javaObjectType, 0.toShort())
        mapDefault(Int::class.javaObjectType, 0)
        mapDefault(Long::class.javaObjectType, 0.toLong())

        mapDefault(Float::class.javaPrimitiveType!!, 0.toFloat())
        mapDefault(Double::class.javaPrimitiveType!!, 0.toDouble())

        mapDefault(Float::class.javaObjectType, 0.toFloat())
        mapDefault(Double::class.javaObjectType, 0.toDouble())

        mapDefault(String::class.java, "")

        mapDefault(CharArray::class.java, charArrayOf())
        mapDefault(ByteArray::class.java, byteArrayOf())
        mapDefault(ShortArray::class.java, shortArrayOf())
        mapDefault(IntArray::class.java, intArrayOf())
        mapDefault(LongArray::class.java, longArrayOf())
        mapDefault(FloatArray::class.java, floatArrayOf())
        mapDefault(DoubleArray::class.java, doubleArrayOf())

        mapDefault(Color::class.java, Color.BLACK)
        mapDefaultGenerator(NBTTagCompound::class.java) { NBTTagCompound() }

        mapDefault(Vec3d::class.java, Vec3d.ZERO)
        mapDefault(Vec2d::class.java, Vec2d.ZERO)
        mapDefault(Vec3i::class.java, Vec3i.NULL_VECTOR)
        mapDefault(BlockPos::class.java, BlockPos.ORIGIN)

        registerSpecialDefault handler@ { type ->
            if (!type.clazz.isEnum)
                return@handler null

            val firstValue = type.clazz.enumConstants.first()
            return@handler { firstValue }
        }

        val arrayInstance = arrayOf<Any>() // WARNING! Identity equalities and synchronized blocks will go screwy with these, just be aware
        registerSpecialDefault handler@ { type ->
            if (!type.clazz.isArray)
                return@handler null

            return@handler { arrayInstance }
        }

        mapDefaultGenerator(ArrayList::class.java, { ArrayList<Any>() })
        mapDefaultGenerator(HashMap::class.java, { HashMap<Any, Any>() })
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapDefaultGenerator(clazz: Class<T>, defaultGenerator: () -> T) {
        classMap.put(clazz, defaultGenerator as () -> Any)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> mapDefault(clazz: Class<T>, defaultValue: T) {
        classMap.put(clazz, { defaultValue as Any })
    }

    @JvmStatic
    fun aliasAs(aliasTo: Class<*>, clazz: Class<*>) {
        val aliasValue = map[FieldType.create(aliasTo)]
        if (aliasValue != null)
            map[FieldType.create(clazz)] = aliasValue
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerSpecialDefault(handler: (FieldType) -> (() -> Any)?) {
        specialHandlers.add(handler)
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun registerGenericDefault(handler: (FieldTypeGeneric) -> (() -> Any)?) {
        genericHandlers.add(handler)
    }

    @JvmStatic
    fun getDefaultValue(type: FieldType): Any {
        val default = map[type] ?: createSpecialDefaults(type) ?: classMap[type.clazz] ?: throw IllegalArgumentException(type.toString())
        return default()
    }

    fun createSpecialDefaults(type: FieldType): (() -> Any)? {
        if (type is FieldTypeGeneric) {
            for (handler in genericHandlers) {
                val serializer = handler(type)
                if (serializer != null) {
                    map[type] = serializer
                    return serializer
                }
            }
        }
        for (handler in specialHandlers) {
            val serializer = handler(type)
            if (serializer != null) {
                map[type] = serializer
                return serializer
            }
        }
        return null
    }
}
