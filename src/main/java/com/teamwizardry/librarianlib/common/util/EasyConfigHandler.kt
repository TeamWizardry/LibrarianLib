package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.io.File
import java.lang.reflect.Field


/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for the automatic config system. Its [init] method should be invoked at
 * pre-initialization time.
 */
object EasyConfigHandler {
    val fieldMapStr: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapInt: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapBoolean: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    val fieldMapDouble: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    var asmDataTable: ASMDataTable? = null
    fun init(configf: File, asm: ASMDataTable? = null, modid: String = Loader.instance().activeModContainer().modId) {
        if(asm != null && asmDataTable == null) asmDataTable = asm
        else if(asm == null && asmDataTable == null) throw RuntimeException("Not yet initialized!")
        val config = Configuration(configf)
        findByClass(Any::class.java, asmDataTable!!)
        findByClass(Boolean::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Char::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Byte::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Short::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Int::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Float::class.javaPrimitiveType!!, asmDataTable!!)
        findByClass(Long::class.javaPrimitiveType!!, asmDataTable!!)
        if (LibrarianLib.DEV_ENVIRONMENT) {
            val pad = " " * modid.length

            LibrarianLog.info("$modid | All config properties found:")
            fieldMapStr.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapInt.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapDouble.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapBoolean.forEach { LibrarianLog.info("$pad | ${it.key}") }
        }

        config.load()
        fieldMapStr.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getString("def", ""), it.value.getString("comment", "")).string)
        }
        fieldMapInt.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getInt("def", 0), it.value.getString("comment", "")).int)
        }
        fieldMapBoolean.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getBoolean("def", false), it.value.getString("comment", "")).boolean)
        }
        fieldMapDouble.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("catagory", ""), it.value.getString("id", ""), it.value.getBoolean("def", false), it.value.getString("comment", "")).double)
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
        AnnotationHelper.findAnnotatedObjects(asm, clazz, ConfigPropertyDouble::class.java, { field: Field, info: AnnotationHelper.AnnotationInfo ->
            fieldMapDouble.put(field, info)
        })
    }
}

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [String] (or in Kotlin String?]
 * that you wish to use as a config property. Use [catagory] to indicate the config catagory in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyString(val modid: String, val catagory: String, val id: String, val comment: String, val def: String, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Int] (or in Kotlin Int?]
 * that you wish to use as a config property. Use [catagory] to indicate the config catagory in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyInt(val modid: String, val catagory: String, val id: String, val comment: String, val def: Int, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Boolean] (or in Kotlin Boolean?]
 * that you wish to use as a config property. Use [catagory] to indicate the config catagory in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBoolean(val modid: String, val catagory: String, val id: String, val comment: String, val def: Boolean, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Double] (or in Kotlin Double?]
 * that you wish to use as a config property. Use [catagory] to indicate the config catagory in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [def] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyDouble(val modid: String, val catagory: String, val id: String, val comment: String, val def: Double, val devOnly: Boolean = false)
