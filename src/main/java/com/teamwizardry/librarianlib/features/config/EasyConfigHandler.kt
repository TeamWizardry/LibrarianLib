package com.teamwizardry.librarianlib.features.config

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.neogui.provided.book.helper.BookTranslationDataEvent
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.utilities.AnnotationHelper
import com.teamwizardry.librarianlib.features.utilities.AnnotationInfo
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.lang.reflect.Field


/**
 * Created by Elad on 10/14/2016.
 * This object contains utilities for the automatic config system.
 */
object EasyConfigHandler {
    private lateinit var CONFIG_DIR: File

    private var workingId = currentModId

    private val allIds = mutableSetOf<String>()
    private val toLoad = mutableMapOf<String, File?>()
    private var loaded = false

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun translateConfigValues(e: BookTranslationDataEvent) {
        if (e.providedData.size() >= 2) {
            val first = e.providedData.get(0)

            if (first.isJsonPrimitive && first.asString == "config") {
                val joined = e.providedData.joinToString(".") { it.asString }.removePrefix(first.asString + ".")
                val field = bookConfigValues[joined]
                if (field != null) e.stringValue = field.toString()
            }
        }
    }

    private val bookConfigValues: MutableMap<String, Any> = mutableMapOf()

    private val allFields: MutableMap<FieldEntry<*>, Pair<String, String>> = mutableMapOf()
    private val toLog: MutableList<Pair<String, String>> = mutableListOf()

    private val fieldMapStr: MutableList<FieldEntry<String>> = mutableListOf()
    private val fieldMapInt: MutableList<FieldEntry<Int>> = mutableListOf()
    private val fieldMapBoolean: MutableList<FieldEntry<Boolean>> = mutableListOf()
    private val fieldMapDouble: MutableList<FieldEntry<Double>> = mutableListOf()
    private val fieldMapLong: MutableList<FieldEntry<Long>> = mutableListOf()

    private val fieldMapStrArr: MutableList<FieldEntry<Array<String>>> = mutableListOf()
    private val fieldMapIntArr: MutableList<FieldEntry<IntArray>> = mutableListOf()
    private val fieldMapBooleanArr: MutableList<FieldEntry<BooleanArray>> = mutableListOf()
    private val fieldMapDoubleArr: MutableList<FieldEntry<DoubleArray>> = mutableListOf()
    private val fieldMapLongArr: MutableList<FieldEntry<LongArray>> = mutableListOf()

    private data class FieldEntry<T>(val modId: String,
                                     val setter: (T) -> Unit,
                                     val defaultValue: T?,
                                     val devOnly: Boolean,
                                     val comment: String,
                                     val category: String,
                                     val identifier: String,
                                     val max: T? = null,
                                     val min: T? = null,
                                     val sortingId: Int = 0,
                                     val requiresRestart: Boolean = false,
                                     val requiresWorld: Boolean = false) {

        val editedComment: String

        init {
            var commented = comment
            if (min != null)
                commented += "\nMin: $min"
            if (defaultValue != null)
                commented += "\nDefault: $min"
            if (max != null)
                commented += "\nMax: $min"
            editedComment = commented
        }
    }

    @Suppress("DEPRECATION")
    internal fun bootstrap(asm: ASMDataTable, dir: File) {
        loaded = true
        CONFIG_DIR = dir
        MinecraftForge.EVENT_BUS.register(this)

        findAllProperties(asm)


        allIds.forEach {
            initInternal(it, toLoad[it])
        }
        toLoad.clear()
    }

    private val mappings = mutableMapOf<String, File>()

    @JvmStatic
    @JvmOverloads
    fun init(modid: String = currentModId, configf: File? = if (loaded) File(CONFIG_DIR, "$currentModId.cfg") else null) {
        if (!loaded)
            toLoad.put(modid, configf)
        else LibrarianLog.error("Trying to activate the config file override too late. Call it in init. Mod: $modid")
    }

    @SubscribeEvent
    fun onConfigChanged(e: ConfigChangedEvent) {
        initInternal(e.modID, mappings[e.modID])
    }

    private fun initInternal(modid: String, configf: File?) {
        if (allFields.none { it.key.modId == modid }) return

        if (LibrarianLib.DEV_ENVIRONMENT)
            LibrarianLog.info("$modid | All config properties found:")

        workingId = modid

        val f = configf ?: File(CONFIG_DIR, "$modid.cfg")
        mappings[modid] = f
        val config = Configuration(f)

        config.load()
        toLog.clear()

        fieldMapStr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: "", it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).string
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapInt.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: 0, it.editedComment, it.min ?: -Int.MAX_VALUE, it.max ?: Int.MIN_VALUE)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).int
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapBoolean.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: false, it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).boolean
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapDouble.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: 0.0, it.editedComment, it.min ?: -Double.MAX_VALUE, it.max ?: Double.MAX_VALUE)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).double
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapLong.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, (it.defaultValue ?: 0L).toString(), it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).long
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapStrArr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: arrayOf(), it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).stringList
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapIntArr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val maxArr = it.max ?: intArrayOf()
            val max = if (maxArr.isEmpty()) Int.MAX_VALUE else maxArr[0]
            val minArr = it.min ?: intArrayOf()
            val min = if (minArr.isEmpty()) -Int.MAX_VALUE else minArr[0]
            val value = config.get(it.category, it.identifier, it.defaultValue ?: intArrayOf(), it.editedComment, min, max)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).intList
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapBooleanArr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val value = config.get(it.category, it.identifier, it.defaultValue ?: booleanArrayOf(), it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).booleanList
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapDoubleArr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val maxArr = it.max ?: doubleArrayOf()
            val max = if (maxArr.isEmpty()) Double.MAX_VALUE else maxArr[0]
            val minArr = it.min ?: doubleArrayOf()
            val min = if (minArr.isEmpty()) -Double.MAX_VALUE else minArr[0]
            val value = config.get(it.category, it.identifier, it.defaultValue ?: doubleArrayOf(), it.editedComment, min, max)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).doubleList
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }
        fieldMapLongArr.sortedBy { it.sortingId }.filter { shouldUse(it) }.forEach {
            logFieldName(it)
            val arr = it.defaultValue ?: longArrayOf()
            val value = config.get(it.category, it.identifier, arr.map(Long::toString).toTypedArray(), it.editedComment)
                    .setRequiresMcRestart(it.requiresRestart).setRequiresWorldRestart(it.requiresWorld).stringList.mapIndexed { i, s ->
                try {
                    s.toLong()
                } catch (e: NumberFormatException) {
                    if (arr.size > i) arr[i] else 0L
                }
            }.toLongArray()
            it.setter(value)
            bookConfigValues[it.modId + "." + it.identifier] = value
        }


        if (LibrarianLib.DEV_ENVIRONMENT) {
            val maxNameLen = toLog.maxBy { it.first.length }?.first?.length ?: 0
            toLog.forEach { LibrarianLog.info("${modid.length * " "} | ${it.first}${" " * (maxNameLen - it.first.length)} | ${it.second}") }
        }

        toLog.clear()
        config.save()
    }

    private fun findAllProperties(asm: ASMDataTable) {
        AnnotationHelper.findAnnotatedObjects(asm, null, ConfigProperty::class.java) { field, info ->
            field.isAccessible = true
            var modid = info.getString("configId")
            if (modid.isBlank())
                modid = OwnershipHandler.getModId(field.declaringClass) ?: "unknown"
            val inst = field.declaringClass.kotlin.objectInstance

            when (field.type) {
                String::class.java -> addToMaps("String", inst, modid, field, info, fieldMapStr)
                Int::class.java -> addToMaps("Int", inst, modid, field, info, fieldMapInt) { _, _, _, identifier, _ ->
                    val annot = field.getAnnotation(ConfigIntRange::class.java)
                    FieldEntry(modid, { it: Int -> field.set(inst, it) }, field.get(inst) as Int,
                            field.isAnnotationPresent(ConfigDevOnly::class.java),
                            info.getString("comment"), info.getString("category"), identifier, annot?.max ?: Int.MAX_VALUE, annot?.min ?: Int.MIN_VALUE,
                            info.getInt("sortingId"), field.isAnnotationPresent(ConfigNeedsFullRestart::class.java),
                            field.isAnnotationPresent(ConfigNeedsWorldRestart::class.java))
                }
                Double::class.java -> addToMaps("Double", inst, modid, field, info, fieldMapDouble) { _, _, _, identifier, _ ->
                    val annot = field.getAnnotation(ConfigDoubleRange::class.java)
                    FieldEntry(modid, { it: Double -> field.set(inst, it) }, field.get(inst) as Double,
                            field.isAnnotationPresent(ConfigDevOnly::class.java),
                            info.getString("comment"), info.getString("category"), identifier, annot?.max ?: Double.MAX_VALUE, annot?.min ?: Double.MIN_VALUE,
                            info.getInt("sortingId"), field.isAnnotationPresent(ConfigNeedsFullRestart::class.java),
                            field.isAnnotationPresent(ConfigNeedsWorldRestart::class.java))
                }
                Boolean::class.java -> addToMaps("Boolean", inst, modid, field, info, fieldMapBoolean)
                Long::class.java -> addToMaps("Long", inst, modid, field, info, fieldMapLong)
                Array<String>::class.java -> addToMaps("StringArray", inst, modid, field, info, fieldMapStrArr)
                IntArray::class.java -> addToMaps("IntArray", inst, modid, field, info, fieldMapIntArr) { _, _, _, identifier, _ ->
                    val annot = field.getAnnotation(ConfigIntRange::class.java)
                    FieldEntry(modid, { it: IntArray -> field.set(inst, it) }, field.get(inst) as IntArray,
                            field.isAnnotationPresent(ConfigDevOnly::class.java),
                            info.getString("comment"), info.getString("category"), identifier, intArrayOf(annot?.max ?: Int.MAX_VALUE), intArrayOf(annot?.min ?: Int.MIN_VALUE),
                            info.getInt("sortingId"), field.isAnnotationPresent(ConfigNeedsFullRestart::class.java),
                            field.isAnnotationPresent(ConfigNeedsWorldRestart::class.java))
                }
                DoubleArray::class.java -> addToMaps("DoubleArray", inst, modid, field, info, fieldMapDoubleArr) { _, _, _, identifier, _ ->
                    val annot = field.getAnnotation(ConfigDoubleRange::class.java)
                    FieldEntry(modid, { it: DoubleArray -> field.set(inst, it) }, field.get(inst) as DoubleArray,
                            field.isAnnotationPresent(ConfigDevOnly::class.java),
                            info.getString("comment"), info.getString("category"), identifier, doubleArrayOf(annot?.max ?: Double.MAX_VALUE), doubleArrayOf(annot?.min ?: Double.MIN_VALUE),
                            info.getInt("sortingId"), field.isAnnotationPresent(ConfigNeedsFullRestart::class.java),
                            field.isAnnotationPresent(ConfigNeedsWorldRestart::class.java))
                }
                BooleanArray::class.java -> addToMaps("BooleanArray", inst, modid, field, info, fieldMapBooleanArr)
                LongArray::class.java -> addToMaps("LongArray", inst, modid, field, info, fieldMapLongArr)
            }
        }
    }

    private inline fun <reified T> addToMaps(name: String, inst: Any?, modid: String, field: Field, info: AnnotationInfo, target: MutableList<FieldEntry<T>>,
                                             noinline makeFieldEntry: (String, Any?, Field, String, AnnotationInfo) -> FieldEntry<T> = EasyConfigHandler::makeFieldEntryDefault) {
        var identifier = info.getString("name")
        if (identifier.isBlank()) identifier = VariantHelper.toSnakeCase(field.name)

        allIds.add(modid)

        val fieldEntry = makeFieldEntry(modid, inst, field, identifier, info)
        target.add(fieldEntry)
        allFields[fieldEntry] = name to "${field.declaringClass.typeName}.${field.name}"
    }

    private inline fun <reified T> makeFieldEntryDefault(modid: String, inst: Any?, field: Field, identifier: String, info: AnnotationInfo): FieldEntry<T> {
        return FieldEntry(modid, { it: T -> field.set(inst, it) }, field.get(inst) as T,
                field.isAnnotationPresent(ConfigDevOnly::class.java),
                info.getString("comment"), info.getString("category"), identifier, null, null,
                info.getInt("sortingId"), field.isAnnotationPresent(ConfigNeedsFullRestart::class.java),
                field.isAnnotationPresent(ConfigNeedsWorldRestart::class.java))
    }

    private fun <T> logFieldName(it: FieldEntry<T>) {
        toLog.add(allFields[it] ?: return)
    }

    private fun <T> shouldUse(it: FieldEntry<T>): Boolean {
        if (it.devOnly && !LibrarianLib.DEV_ENVIRONMENT) return false
        return workingId == it.modId
    }
}


/**
 * Apply to a field you wish to be a config property.
 * [category] is the place in the config you want your property to be.
 * [configId] is a way to specify which config id you'd like your property to be attached to.
 * By default, it's the mod id of your class.
 * [name] is the name of the config property. By default, it's the field name snakecased.
 * [comment] is the comment you wish to apply to the config property.
 * [sortingId] can be used to order your properties, from smallest to largest.
 * Order is not guaranteed between two identical sorting ids.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigProperty(val category: String,
                                val comment: String = "",
                                val name: String = "",
                                val configId: String = "",
                                val sortingId: Int = 0)

/**
 * Apply this to an @[ConfigProperty] to have it only be loaded in the dev environment.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigDevOnly

/**
 * Apply this to an @[ConfigProperty] to have it require an MC restart to change.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigNeedsFullRestart

/**
 * Apply this to an @[ConfigProperty] to have it require a world restart to change.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigNeedsWorldRestart

/**
 * Apply this to a @[ConfigProperty] double or double array field to clamp its min and max values.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigDoubleRange(val min: Double, val max: Double)

/**
 * Apply this to a @[ConfigProperty] int or int array field to clamp its min and max values.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class ConfigIntRange(val min: Int, val max: Int)
