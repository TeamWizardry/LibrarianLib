package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.io.File
import java.lang.reflect.Field

/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for the automatic config system. Its [init] method should be invoked at
 * pre-initialization time.
 */
class EasyConfigHandler {
    val fieldMapStr: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapInt: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapBoolean: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    private var generated = false

    fun init(modid: String, configf: File, asm: ASMDataTable?) {
        if (asm == null) return
        val config = Configuration(configf)
        if (!generated) {
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
            generated = true
        }

        config.load()
        fieldMapStr.filter {
            it.value.getString("modid", "") == modid
        }.forEach {
            it.key.isAccessible = true
            if (!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getString("def", ""), it.value.getString("comment", "")).string)
        }

        fieldMapInt.filter {
            it.value.getString("modid", "") == modid
        }.forEach {
            it.key.isAccessible = true
            if (!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getInt("def", 0), it.value.getString("comment", "")).int)
        }

        fieldMapBoolean.filter {
            it.value.getString("modid", "") == modid
        }.forEach {
            it.key.isAccessible = true
            if (!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getBoolean("def", false), it.value.getString("comment", "")).boolean)
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

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [String] (or in Kotlin String?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyString(val modid: String, val category: String, val id: String, val comment: String, val def: String, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Int] (or in Kotlin Int?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyInt(val modid: String, val category: String, val id: String, val comment: String, val def: Int, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Boolean] (or in Kotlin Boolean?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBoolean(val modid: String, val category: String, val id: String, val comment: String, val def: Boolean, val devOnly: Boolean = false)
