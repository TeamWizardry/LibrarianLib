package com.teamwizardry.librarianlib.common.util

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.util.EasyConfigHandler.init
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
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

    private var workingId = currentModId

    private val toLoad = mutableListOf<Pair<String, File?>>()
    private var loaded = false

    private val allFields: MutableMap<Triple<*, *, *>, String> = mutableMapOf()

    private val fieldMapStr: MutableList<Triple<String, (String) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapInt: MutableList<Triple<String, (Int) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapBoolean: MutableList<Triple<String, (Boolean) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapDouble: MutableList<Triple<String, (Double) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapLong: MutableList<Triple<String, (Long) -> Unit, AnnotationInfo>> = mutableListOf()

    private val fieldMapStrArr: MutableList<Triple<String, (Array<String>) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapIntArr: MutableList<Triple<String, (IntArray) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapBooleanArr: MutableList<Triple<String, (BooleanArray) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapDoubleArr: MutableList<Triple<String, (DoubleArray) -> Unit, AnnotationInfo>> = mutableListOf()
    private val fieldMapLongArr: MutableList<Triple<String, (LongArray) -> Unit, AnnotationInfo>> = mutableListOf()

    internal fun bootstrap(asm: ASMDataTable, dir: File) {
        loaded = true
        CONFIG_DIR = dir
        findByClass(String::class.java, ConfigPropertyString::class.java, fieldMapStr, asm)
        findByClass(Boolean::class.javaPrimitiveType!!, ConfigPropertyBoolean::class.java, fieldMapBoolean, asm)
        findByClass(Int::class.javaPrimitiveType!!, ConfigPropertyInt::class.java, fieldMapInt, asm)
        findByClass(Double::class.javaPrimitiveType!!, ConfigPropertyDouble::class.java, fieldMapDouble, asm)
        findByClass(Long::class.javaPrimitiveType!!, ConfigPropertyLong::class.java, fieldMapLong, asm)

        findByClass(Array<String>::class.java, ConfigPropertyStringArray::class.java, fieldMapStrArr, asm)
        findByClass(BooleanArray::class.java, ConfigPropertyBooleanArray::class.java, fieldMapBooleanArr, asm)
        findByClass(IntArray::class.java, ConfigPropertyIntArray::class.java, fieldMapIntArr, asm)
        findByClass(DoubleArray::class.java, ConfigPropertyDoubleArray::class.java, fieldMapDoubleArr, asm)
        findByClass(LongArray::class.java, ConfigPropertyLongArray::class.java, fieldMapLongArr, asm)

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

        if (LibrarianLib.DEV_ENVIRONMENT)
            LibrarianLog.info("$modid | All config properties found:")

        workingId = modid

        val config = if (configf == null)
            Configuration(File(CONFIG_DIR, "$modid.cfg"))
        else
            Configuration(configf)

        config.load()
        fieldMapStr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getString("defaultValue"), it.comment).string)
        }
        fieldMapInt.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getInt("defaultValue"), it.comment).int)
        }
        fieldMapBoolean.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getBoolean("defaultValue"), it.comment).boolean)
        }
        fieldMapDouble.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getDouble("defaultValue"), it.comment).double)
        }
        fieldMapLong.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getLong("defaultValue").toString(), it.comment).long)
        }

        fieldMapStrArr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getStringArray("defaultValue"), it.comment).stringList)
        }
        fieldMapIntArr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getIntArray("defaultValue"), it.comment).intList)
        }
        fieldMapBooleanArr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getBooleanArray("defaultValue"), it.comment).booleanList)
        }
        fieldMapDoubleArr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            it.second(config.get(it.category, it.id, it.third.getDoubleArray("defaultValue"), it.comment).doubleList)
        }
        fieldMapLongArr.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val arr = it.third.getLongArray("defaultValue")
            it.second(config.get(it.category, it.id, arr.map(Long::toString).toTypedArray(), it.comment).stringList.mapIndexed { i, s ->
                try {
                    s.toLong()
                } catch (e: NumberFormatException) {
                    arr[i]
                } }.toLongArray())
        }
        config.save()
    }

    private fun <T> findByClass(clazz: Class<*>, annotationClass: Class<*>, target: MutableList<Triple<String, (T) -> Unit, AnnotationInfo>>, asm: ASMDataTable) {
        val inst = clazz.kotlin.objectInstance
        AnnotationHelper.findAnnotatedObjects(asm, clazz, annotationClass, { field: Field, info: AnnotationInfo ->
            injectField(inst, field, info, annotationClass, target)
        })
    }

    private fun <T> injectField(inst: Any?, field: Field, info: AnnotationInfo, annotationClass: Class<*>, target: MutableList<Triple<String, (T) -> Unit, AnnotationInfo>>) {
        field.isAccessible = true
        val modid = OwnershipHandler.getModId(field.declaringClass) ?: "unknown"
        val triple = Triple(modid, { it: T -> field.set(inst, it) }, info)
        val name = annotationClass.simpleName
        val paddedName = name + " " * (26 - name.length)
        allFields.put(triple as Triple<*, *, *>, "$paddedName | ${field.declaringClass.typeName}.${field.name}")
        target.add(triple)
    }

    private fun <T> logFieldName(it: Triple<String, (T) -> Unit, AnnotationInfo>) {
        val pad = workingId.length * " "
        LibrarianLog.info(" $pad | " + allFields[it])
    }

    private val <T> Triple<String, (T) -> Unit, AnnotationInfo>.category: String
        get() = third.getString("category")

    private val <T> Triple<String, (T) -> Unit, AnnotationInfo>.id: String
        get() = third.getString("id")

    private val <T> Triple<String, (T) -> Unit, AnnotationInfo>.comment: String
        get() = third.getString("comment")

    private fun <T> shouldUse(it: Triple<String, (T) -> Unit, AnnotationInfo>): Boolean {
        if (it.third.getBoolean("devOnly") && !LibrarianLib.DEV_ENVIRONMENT) return false
        val modid = it.third.getString("modid")
        return workingId == modid || modid == noModId && modid == it.first
    }
}

private val noModId = ""

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [String]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyString(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: String, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Int]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyInt(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: Int, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Long]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyLong(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: Long, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Boolean]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBoolean(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: Boolean, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Double]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyDouble(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: Double, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [Array]<[String]>
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyStringArray(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: Array<String>, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [IntArray]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyIntArray(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: IntArray, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [BooleanArray]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyBooleanArray(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: BooleanArray, val devOnly: Boolean = false)

/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [DoubleArray]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyDoubleArray(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: DoubleArray, val devOnly: Boolean = false)
/**
 * This annotation should be applied to non-final, static (if in Kotlin, [JvmStatic]) fields of type [DoubleArray]
 * that you wish to use as a config property. Use [category] to indicate the config category in the config file,
 * [id] will indicate the name of the property, [comment] will be the comment above the entry in the config file,
 * [defaultValue] is the default value, and if [devOnly] (optional) is set to true, this config property will only be set in a
 * development environment.
 */
@Target(AnnotationTarget.FIELD) annotation class ConfigPropertyLongArray(val modid: String = noModId, val category: String, val id: String, val comment: String, val defaultValue: LongArray, val devOnly: Boolean = false)
