package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.io.File
import java.lang.reflect.Field

/**
 * Created by Elad on 10/14/2016.
 */
object EasyConfigHandler {
    val fieldMapStr: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapInt: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapBoolean: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()

    fun init(configf: File, asm: ASMDataTable?) {
        if(asm == null) return
        val config = Configuration(configf)
        findByClass(Any::class.java, asm)
        findByClass(Boolean::class.javaPrimitiveType!!, asm)
        findByClass(Char::class.javaPrimitiveType!!, asm)
        findByClass(Byte::class.javaPrimitiveType!!, asm)
        findByClass(Short::class.javaPrimitiveType!!, asm)
        findByClass(Int::class.javaPrimitiveType!!, asm)
        findByClass(Float::class.javaPrimitiveType!!, asm)
        findByClass(Long::class.javaPrimitiveType!!, asm)
        if (LibrarianLib.DEV_ENVIRONMENT) {
            fieldMapStr.keys.forEach { println("Found string config property field ${it.declaringClass.name}.${it.name}") }
            if (fieldMapStr.keys.size == 0) println("No string config property fields found!")
        }

        config.load()
        fieldMapStr.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getString("def", ""), it.value.getString("comment", "")).string)
        }
        fieldMapInt.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getInt("def", 0), it.value.getString("comment", "")).int)
        }
        fieldMapBoolean.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getBoolean("def", false), it.value.getString("comment", "")).boolean)
        }
        config.save()

    }
    private fun findByClass(clazz: Class<*>, asm: ASMDataTable) {
        AnnotationHelper.findAnnotatedObjects(asm, clazz, ConfigPropertyString::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            fieldMapStr.put(field, info)
        })
        AnnotationHelper.findAnnotatedObjects(asm, clazz, ConfigPropertyInt::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            fieldMapInt.put(field, info)
        })
        AnnotationHelper.findAnnotatedObjects(asm, clazz, ConfigPropertyBoolean::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            fieldMapBoolean.put(field, info)
        })
    }
}
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyString(val catagory: String, val id: String, val comment: String, val def: String, val devOnly: Boolean = false)
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyInt(val catagory: String, val id: String, val comment: String, val def: Int, val devOnly: Boolean = false)
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBoolean(val catagory: String, val id: String, val comment: String, val def: Boolean, val devOnly: Boolean = false)