package com.teamwizardry.librarianlib.common.util

import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.lang.reflect.Field
import java.lang.reflect.Method

object AnnotationHelper {
    data class AnnotationInfo(private val map: Map<String, Any>) {

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
