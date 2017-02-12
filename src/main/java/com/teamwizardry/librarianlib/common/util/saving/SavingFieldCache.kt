package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.DefaultedMutableMap
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.withRealDefault
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

/**
 * @author WireSegal
 * Created at 1:43 PM on 10/14/2016.
 */
object SavingFieldCache {

    val atSaveMap = LinkedHashMap<Class<*>, Map<String, FieldCache>>()

    @JvmStatic
    fun getClassFields(clazz: Class<*>): Map<String, FieldCache> {
        val existing = atSaveMap[clazz]
        if (existing != null) return existing

        val map = linkedMapOf<String, FieldCache>()
        buildClassFields(clazz, map)
        buildClassGetSetters(clazz, map)
        alreadyDone.clear()

        atSaveMap.put(clazz, map)

        return map
    }

    fun buildClassFields(clazz: Class<*>, map: MutableMap<String, FieldCache>) {
        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            !Modifier.isStatic(it.modifiers)
        }

        fields.map {
            getNameFromField(clazz, it) to it
        }.forEach {
            val (name, field) = it
            field.isAccessible = true

            val mods = field.modifiers
            val meta = FieldMetadata(FieldType.create(field), SavingFieldFlag.FIELD)
            if (Modifier.isFinal(mods)) meta.addFlag(SavingFieldFlag.FINAL)
            if (Modifier.isTransient(mods)) meta.addFlag(SavingFieldFlag.TRANSIENT)
            if (field.isAnnotationPresent(Save::class.java)) meta.addFlag(SavingFieldFlag.ANNOTATED)
            if (field.isAnnotationPresent(NotNull::class.java)) meta.addFlag(SavingFieldFlag.NONNULL)
            if (field.type.isPrimitive) meta.addFlag(SavingFieldFlag.NONNULL)
            if (field.isAnnotationPresent(NoSync::class.java)) meta.addFlag(SavingFieldFlag.NO_SYNC)
            if (field.isAnnotationPresent(CapabilityProvide::class.java)) {
                meta.addFlag(SavingFieldFlag.CAPABILITY)
                val annot = field.getAnnotation(CapabilityProvide::class.java)
                if (EnumFacing.UP in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_UP)
                if (EnumFacing.DOWN in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_DOWN)
                if (EnumFacing.NORTH in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_NORTH)
                if (EnumFacing.SOUTH in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_SOUTH)
                if (EnumFacing.EAST in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_EAST)
                if (EnumFacing.WEST in annot.sides) meta.addFlag(SavingFieldFlag.CAPABILITY_WEST)
            }

            val setterLambda: (Any, Any?) -> Unit = if (meta.hasFlag(SavingFieldFlag.FINAL)) {
                { obj, inp -> throw IllegalAccessException("Tried to set final property $name for class ${clazz.simpleName} (final field)") }
            } else {
                MethodHandleHelper.wrapperForSetter<Any>(field)
            }

            map.put(name, FieldCache(meta,
                    MethodHandleHelper.wrapperForGetter<Any>(field),
                    setterLambda,
                    field.name))
        }
    }

    fun buildClassGetSetters(clazz: Class<*>, map: MutableMap<String, FieldCache>) {
        val getters = mutableMapOf<String, Method>()
        val setters = mutableMapOf<String, Method>()


        clazz.declaredMethods.forEach {
            it.declaredAnnotations
            val mods = it.modifiers
            if (!Modifier.isStatic(mods)) {
                if (it.isAnnotationPresent(SaveMethodGetter::class.java)) {
                    val types = it.parameterTypes
                    val name = getNameFromMethod(clazz, it, true)
                    if (types.isEmpty()) {
                        getters.put(name, it)
                    } else {
                        errorList[clazz][name].add("Getter has parameters")
                    }
                }
                if (it.isAnnotationPresent(SaveMethodSetter::class.java)) {
                    val types = it.parameterTypes
                    val name = getNameFromMethod(clazz, it, false)
                    if (types.size == 1) {
                        setters.put(name, it)
                    } else {
                        errorList[clazz][name].add("Setter has ${types.size} parameters, they must have exactly 1")
                    }
                }
            }
        }

        val pairs = mutableMapOf<String, Triple<Method, Method?, Class<*>>>()
        getters.forEach {
            val (name, getter) = it
            val getReturnType = getter.returnType
            if (name in setters) {
                val setter = setters[name]!!
                val setReturnType = setter.parameterTypes[0]
                if (getReturnType == setReturnType)
                    pairs.put(name, Triple(getter, setter, getReturnType))
                else
                    errorList[clazz][name].add("Getter and setter have mismatched types")
            }
        }

        setters.filterKeys { it !in getters }.forEach {
            val name = it.key
            errorList[clazz][name].add("Setter has no getter")
        }


        pairs.toList().sortedBy {
            it.first
        }.forEach {
            val (name, triple) = it
            val (getter, setter, type) = triple
            getter.isAccessible = true
            setter?.isAccessible = true

            val wrapperForGetter = MethodHandleHelper.wrapperForMethod<Any>(getter)
            val wrapperForSetter = setter?.let { MethodHandleHelper.wrapperForMethod<Any>(it) }

            val meta = FieldMetadata(FieldType.create(getter), SavingFieldFlag.ANNOTATED, SavingFieldFlag.METHOD)

            if (getter.isAnnotationPresent(NotNull::class.java) && (setter == null || setter.parameterAnnotations[0].any { it is NotNull }))
                meta.addFlag(SavingFieldFlag.NONNULL)
            if (type.isPrimitive)
                meta.addFlag(SavingFieldFlag.NONNULL)
            if (getter.isAnnotationPresent(NoSync::class.java) && (setter == null || setter.isAnnotationPresent(NoSync::class.java)))
                meta.addFlag(SavingFieldFlag.NO_SYNC)
            if (setter == null)
                meta.addFlag(SavingFieldFlag.FINAL)

            val setterLambda: (Any, Any?) -> Unit = if (wrapperForSetter == null)
                { obj, inp -> throw IllegalAccessException("Tried to set final property $name for class ${clazz.simpleName} (no save setter)") }
            else
                { obj, inp -> wrapperForSetter(obj, arrayOf(inp)) }

            map.put(name, FieldCache(meta,
                    { obj -> wrapperForGetter(obj, arrayOf()) },
                    setterLambda))
        }
    }

    private val alreadyDone = mutableListOf<String>()
    private val ILLEGAL_NAMES = listOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")

    private val nameMap = mutableMapOf<Field, String>()

    private fun getNameFromField(clazz: Class<*>, f: Field): String {
        val got = nameMap[f]
        if (got != null) return got

        val string = if (f.isAnnotationPresent(Save::class.java)) f.getAnnotation(Save::class.java).saveName else ""
        var name = if (string == "") f.name else string

        if (name in ILLEGAL_NAMES)
            name += "X"
        if (name in alreadyDone)
            errorList[clazz][name].add("Name already in use for field")

        alreadyDone.add(name)
        nameMap[f] = name
        return name
    }

    private fun getNameFromMethod(clazz: Class<*>, m: Method, getter: Boolean): String {
        var name = if (getter)
            m.getAnnotation(SaveMethodGetter::class.java).saveName
        else
            m.getAnnotation(SaveMethodSetter::class.java).saveName

        if (name in ILLEGAL_NAMES)
            name += "X"
        if (name in alreadyDone)
            errorList[clazz][name].add("Name already in use for ${if (getter) "getter" else "setter"}")
        alreadyDone.add(name)
        return name
    }

    private val errorList = mutableMapOf<Class<*>, DefaultedMutableMap<String, MutableList<String>>>().withRealDefault { mutableMapOf<String, MutableList<String>>().withRealDefault { mutableListOf<String>() } }

    fun handleErrors() {
        if (errorList.size == 0)
            return

        val lines = mutableListOf<String>()

        errorList.forEach {
            val (clazz, props) = it
            lines.add("- ${clazz.simpleName}")

            props.forEach {
                val (name, errors) = it
                lines.add("  - $name")
                errors.forEach {
                    lines.add("    - $it")
                }
            }
        }

        LibrarianLog.bigDie("INVALID SAVE FIELDS", lines)
    }
}

data class FieldCache(val meta: FieldMetadata, val getter: (Any) -> Any?, private val setter_: (Any, Any?) -> Unit, var name: String = "") {
    val setter = if (meta.hasFlag(SavingFieldFlag.NONNULL)) {
        { instance: Any, value: Any? ->
            if (value == null) {
                setter_(instance, DefaultValues.getDefaultValue(meta.type))
            } else {
                setter_(instance, value)
            }
        }
    } else {
        setter_
    }


    companion object {
        val providersGetter = MethodHandleHelper.wrapperForGetter(CapabilityManager::class.java, "providers")

        @Suppress("UNCHECKED_CAST")
        val providers by lazy {
            providersGetter.invoke(CapabilityManager.INSTANCE) as IdentityHashMap<String, Capability<*>>
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getCapability(container: Any, capability: Capability<T>, side: EnumFacing?): T? {
        return if (hasCapability(capability, side)) getter(container) as? T else null
    }

    fun hasCapability(capability: Capability<*>, side: EnumFacing?): Boolean {
        val interfaces = meta.type.interfaces
        @Suppress("UNCHECKED_CAST")

        if (interfaces
                .map { it.name.intern() }
                .none { providers[it] == capability })
            return false
        if (!meta.hasFlag(SavingFieldFlag.CAPABILITY)) return false

        return when (side) {
            EnumFacing.DOWN -> meta.hasFlag(SavingFieldFlag.CAPABILITY_DOWN)
            EnumFacing.UP -> meta.hasFlag(SavingFieldFlag.CAPABILITY_UP)
            EnumFacing.NORTH -> meta.hasFlag(SavingFieldFlag.CAPABILITY_NORTH)
            EnumFacing.SOUTH -> meta.hasFlag(SavingFieldFlag.CAPABILITY_SOUTH)
            EnumFacing.WEST -> meta.hasFlag(SavingFieldFlag.CAPABILITY_WEST)
            EnumFacing.EAST -> meta.hasFlag(SavingFieldFlag.CAPABILITY_EAST)
            else -> true
        }
    }
}

data class FieldMetadata private constructor(val type: FieldType, private var flagsInternal: MutableSet<SavingFieldFlag>) {
    constructor(type: FieldType, vararg flags: SavingFieldFlag) : this(type, EnumSet.noneOf(SavingFieldFlag::class.java).let { it.addAll(flags); it })

    val flags: Set<SavingFieldFlag>
        get() = flagsInternal

    fun hasFlag(flag: SavingFieldFlag) = flags.contains(flag)
    fun containsOnly(vararg allowed: SavingFieldFlag) = flags.containsAll(allowed.asList())
    fun doesNotContain(vararg disallowed: SavingFieldFlag) = !disallowed.any { it in flags }

    fun addFlag(flag: SavingFieldFlag) = flagsInternal.add(flag)
    fun removeFlag(flag: SavingFieldFlag) = flagsInternal.remove(flag)
}

enum class SavingFieldFlag {
    FIELD, METHOD, ANNOTATED, NONNULL, NO_SYNC, TRANSIENT, FINAL,
    CAPABILITY, CAPABILITY_UP, CAPABILITY_DOWN, CAPABILITY_NORTH, CAPABILITY_SOUTH, CAPABILITY_EAST, CAPABILITY_WEST
}
