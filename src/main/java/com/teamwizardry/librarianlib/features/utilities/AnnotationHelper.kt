package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for getting annotations from classes. It is not restricted to internal use and may be
 * used freely in all classes.
 */
object AnnotationHelper {


    /**
     * Find all annotated fields of super-type [objClass] with annotation [annotationClass] from data table [table]
     * and send them to the callback [callback].
     */
    fun findAnnotatedObjects(table: ASMDataTable, objClass: Class<*>?, annotationClass: Class<*>, callback: (Field, AnnotationInfo) -> Unit) {
        for (data in table.getAll(annotationClass.name)) {
            try {
                val index = data.objectName.indexOf('(')

                if (index != -1) {
                    continue
                }

                val field = try {
                    Class.forName(data.className)
                } catch (e: ClassNotFoundException) { // Class is sided and we are on wrong side
                    continue
                }.getDeclaredField(data.objectName)

                if (field == null || !(objClass?.isAssignableFrom(field.type) ?: true)) {
                    continue
                }

                field.isAccessible = true
                callback(field, AnnotationInfo(data.annotationInfo))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    /**
     * Find all annotated classes of super-type [superClass] with annotation [annotationClass] from data table [table]
     * and send them to the callback [callback].
     */
    fun <T> findAnnotatedClasses(table: ASMDataTable? = LibrarianLib.PROXY.asmDataTable, superClass: Class<T>, annotationClass: Class<*>, callback: (Class<out T>, AnnotationInfo) -> Unit) {
        if (table == null) return
        for (data in table.getAll(annotationClass.name)) {
            try {
                callback(Class.forName(data.className).asSubclass(superClass) as Class<out T>, AnnotationInfo(data.annotationInfo))
            } catch (ex: Exception) {
                throw ex
            }

        }
    }


    /**
     * Find all annotated methods with annotation [annotationClass] from data table [table]
     * and send them to the callback [callback].
     */
    fun findAnnotatedMethods(table: ASMDataTable, annotationClass: Class<*>, callback: (Method, Array<Class<*>>, AnnotationInfo) -> Unit) {
        for (data in table.getAll(annotationClass.name)) {
            try {
                val index = data.objectName.indexOf('(')

                if (index != -1 && data.objectName.indexOf(')') == index + 1) {
                    val method = Class.forName(data.className).getDeclaredMethod(data.objectName.substring(0, index))

                    if (method != null) {
                        method.isAccessible = true
                        callback(method, method.parameterTypes, AnnotationInfo(data.annotationInfo))
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}

/**
 * This class saves information about annotations gotten from [AnnotationHelper.findAnnotatedObjects], [AnnotationHelper.findAnnotatedClasses], and
 * [AnnotationHelper.findAnnotatedMethods]. That information can be of three types by default: String, Integer, and Boolean.
 * In case other types may be needed, just get the information manually from the [map] field.
 */
data class AnnotationInfo(val map: Map<String, Any>) {

    fun getString(id: String): String {
        val value = map[id]
        return value?.toString() ?: ""
    }

    fun getInt(id: String): Int {
        val value = map[id]
        return value?.hashCode() ?: 0
    }

    fun getDouble(id: String): Double {
        val value = map[id]
        return value?.toString()?.toDouble() ?: 0.0
    }

    fun getBoolean(id: String): Boolean {
        val value = map[id]
        return value as? Boolean ?: false
    }

    fun getLong(id: String): Long {
        val value = map[id]
        return value as? Long ?: 0L
    }

    fun getStringArray(id: String): Array<String> {
        val value = map[id]

        return (value as? Array<*>)?.map(Any?::toString)?.toTypedArray() ?: ((value as? List<*>)?.map(Any?::toString)?.toTypedArray() ?: arrayOf(value.toString()))
    }

    fun getIntArray(id: String): IntArray {
        val value = map[id]
        return value as? IntArray ?: ((value as? List<*>)?.map(Any?::toString)?.map(String::toInt)?.toIntArray() ?: intArrayOf())
    }

    fun getDoubleArray(id: String): DoubleArray {
        val value = map[id]
        return value as? DoubleArray ?: ((value as? List<*>)?.map(Any?::toString)?.map(String::toDouble)?.toDoubleArray() ?: doubleArrayOf())
    }

    fun getBooleanArray(id: String): BooleanArray {
        val value = map[id]
        return value as? BooleanArray ?: ((value as? List<*>)?.map(Any?::toString)?.map(String::toBoolean)?.toBooleanArray() ?: booleanArrayOf())
    }

    fun getLongArray(id: String): LongArray {
        val value = map[id]
        return value as? LongArray ?: ((value as? List<*>)?.map(Any?::toString)?.map(String::toLong)?.toLongArray() ?: longArrayOf())
    }

    inline fun <reified T> get(id: String) = when (T::class.java) {
        String::class.java -> getString(id) as T
        Int::class.java -> getInt(id) as T
        Double::class.java -> getDouble(id) as T
        Boolean::class.java -> getBoolean(id) as T
        Long::class.java -> getLong(id) as T
        Array<String>::class.java -> getStringArray(id) as T
        IntArray::class.java -> getIntArray(id) as T
        DoubleArray::class.java -> getDoubleArray(id) as T
        BooleanArray::class.java -> getBooleanArray(id) as T
        LongArray::class.java -> getLongArray(id) as T
        else -> null
    }
}
