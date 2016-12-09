package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler.init
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.discovery.ASMDataTable
import java.io.File
import java.lang.reflect.Field


/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for the automatic config system. Its [init] method should be invoked at
 * pre-initialization time.
 */
object EasyConfigHandler {
    private lateinit var CONFIG_DIR: File

    private val toLoad = mutableListOf<Pair<String, File?>>()
    private var loaded = false

    private val fieldMapStr: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    private val fieldMapDouble: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    private val fieldMapInt: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()
    private val fieldMapBoolean: MutableMap<Field, AnnotationHelper.AnnotationInfo> = mutableMapOf()

    internal fun bootstrap(asm: ASMDataTable, dir: File) {
        loaded = true
        CONFIG_DIR = dir
        findByClass(Any::class.java, asm)
        findByClass(Boolean::class.javaPrimitiveType!!, asm)
        findByClass(Char::class.javaPrimitiveType!!, asm)
        findByClass(Byte::class.javaPrimitiveType!!, asm)
        findByClass(Short::class.javaPrimitiveType!!, asm)
        findByClass(Int::class.javaPrimitiveType!!, asm)
        findByClass(Float::class.javaPrimitiveType!!, asm)
        findByClass(Double::class.javaPrimitiveType!!, asm)
        findByClass(Long::class.javaPrimitiveType!!, asm)
        if (LibrarianLib.DEV_ENVIRONMENT) {
            val pad = " " * LibrarianLib.MODID.length

            LibrarianLog.info("${LibrarianLib.MODID} | All config properties found:")
            fieldMapStr.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapInt.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapDouble.forEach { LibrarianLog.info("$pad | ${it.key}") }
            fieldMapBoolean.forEach { LibrarianLog.info("$pad | ${it.key}") }
        }

        toLoad.forEach { init(it.first, it.second) }
        toLoad.clear()
    }

    @JvmStatic
    @JvmOverloads
    fun init(modid: String = currentModId, configf: File? = if (loaded) File(CONFIG_DIR, "$currentModId.cfg") else null) {
        if (!loaded) {
            toLoad.add(modid to configf)
            return
        }

        val config = if (configf == null)
            Configuration(File(CONFIG_DIR, "$modid.cfg"))
        else
            Configuration(configf)

        config.load()
        fieldMapStr.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getString("defaultValue", ""), it.value.getString("comment", "")).string)
        }
        fieldMapInt.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getInt("defaultValue", 0), it.value.getString("comment", "")).int)
        }
        fieldMapBoolean.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getBoolean("defaultValue", false), it.value.getString("comment", "")).boolean)
        }
        fieldMapDouble.filter { it.value.getString("modid", "") == modid }.forEach {
            it.key.isAccessible = true
            if(!it.value.getBoolean("devOnly", false) || LibrarianLib.DEV_ENVIRONMENT)
                it.key.set(null, config.get(it.value.getString("category", ""), it.value.getString("id", ""), it.value.getDouble("defaultValue", 0.0), it.value.getString("comment", "")).double)
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
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyString(val modid: String, val category: String, val id: String, val comment: String, val defaultValue: String, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Int] (or in Kotlin Int?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyInt(val modid: String, val category: String, val id: String, val comment: String, val defaultValue: Int, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Boolean] (or in Kotlin Boolean?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBoolean(val modid: String, val category: String, val id: String, val comment: String, val defaultValue: Boolean, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Double] (or in Kotlin Double?]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyDouble(val modid: String, val category: String, val id: String, val comment: String, val defaultValue: Double, val devOnly: Boolean = false)
