package com.teamwizardry.librarianlib.common.util

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
     * This class saves information about annotations gotten from [findAnnotatedObjects], [findAnnotatedClasses], and
     * [findAnnotatedMethods]. That information can be of three types by default: String, Integer, and Boolean.
     * In case other types may be needed, just get the information manually from the [map] field.
     */
    data class AnnotationInfo(val map: Map<String, Any>) {

        fun getString(id: String, def: String?): String? {
            val `val` = map[id]
            return if (`val` == null) def else `val`.toString()
        }

        fun getInt(id: String, def: Int): Int {
            val `val` = map[id]
            return if (`val` == null) def else `val`.hashCode()
        }

        fun getBoolean(id: String, def: Boolean): Boolean {
            val `val` = map[id]
            return if (`val` == null) def else `val` as Boolean
        }

        fun getStringList(id: String): List<String> {
            val `val` = map[id]

            if (`val` is String) {
                return listOf(`val`.toString())
            } else if (`val` is List<*>) {
                return `val` as List<String>
            }

            return emptyList()
        }
    }


    /**
     * Find all annotated fields of super-type [objClass] with annotation [annotationClass] from data table [table]
     * and send them to the callback [callback].
     */
    fun <T> findAnnotatedObjects(table: ASMDataTable, objClass: Class<T>, annotationClass: Class<*>, callback: (Field, AnnotationInfo)->Unit) {
        for (data in table.getAll(annotationClass.name)) {
            try {
                val index = data.objectName.indexOf('(')

                if (index != -1) {
                    continue
                }

                val field = Class.forName(data.className).getDeclaredField(data.objectName)

                if (field == null || !objClass.isAssignableFrom(field.type)) {
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
    fun <T> findAnnotatedClasses(table: ASMDataTable?, superClass: Class<T>, annotationClass: Class<*>, callback: (Class<T>, AnnotationInfo)->Unit) {
        if(table == null) return
        for (data in table.getAll(annotationClass.name)) {
            try {
                callback(Class.forName(data.className).asSubclass(superClass) as Class<T>, AnnotationInfo(data.annotationInfo))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }


    /**
     * Find all annotated methods with annotation [annotationClass] from data table [table]
     * and send them to the callback [callback].
     */
    fun findAnnotatedMethods(table: ASMDataTable, annotationClass: Class<*>, callback: (Method, Array<Class<*>>, AnnotationInfo)->Unit) {
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
