package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.times
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import javax.annotation.Nonnull

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
            if(Modifier.isFinal(mods)) meta.addFlag(SavingFieldFlag.FINAL)
            if(Modifier.isTransient(mods)) meta.addFlag(SavingFieldFlag.TRANSIENT)
            if(field.isAnnotationPresent(Save::class.java)) meta.addFlag(SavingFieldFlag.ANNOTATED)
            if(field.isAnnotationPresent(Nonnull::class.java)) meta.addFlag(SavingFieldFlag.NONNULL)
            if(field.isAnnotationPresent(NoSync::class.java)) meta.addFlag(SavingFieldFlag.NOSYNC)

            map.put(name, FieldCache(meta,
                    MethodHandleHelper.wrapperForGetter<Any>(field),
                    MethodHandleHelper.wrapperForSetter<Any>(field),
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
                    if (types.isEmpty())
                        getters.put(getNameFromMethod(clazz, it, true), it)
                }
                if (it.isAnnotationPresent(SaveMethodSetter::class.java)) {
                    val types = it.parameterTypes
                    if (types.size == 1)
                        setters.put(getNameFromMethod(clazz, it, false), it)
                }
            }
        }

        val pairs = mutableMapOf<String, Triple<Method, Method, Class<*>>>()
        getters.forEach {
            val (name, getter) = it
            if (name in setters) {
                val setter = setters[name]!!
                val getReturnType = getter.returnType
                val setReturnType = setter.parameterTypes[0]
                if (getReturnType == setReturnType)
                    pairs.put(name, Triple(getter, setter, getReturnType))
            }
        }


        pairs.toList().sortedBy {
            it.first
        }.forEach {
            val (name, triple) = it
            val (getter, setter, type) = triple
            getter.isAccessible = true
            setter.isAccessible = true

            val wrapperForGetter = MethodHandleHelper.wrapperForMethod<Any>(getter)
            val wrapperForSetter = MethodHandleHelper.wrapperForMethod<Any>(setter)

            val meta = FieldMetadata(FieldType.create(getter), SavingFieldFlag.ANNOTATED, SavingFieldFlag.METHOD)

            if(getter.isAnnotationPresent(Nonnull::class.java) && setter.parameterAnnotations[0].any { it is Nonnull })
                meta.addFlag(SavingFieldFlag.NONNULL)
            if(getter.isAnnotationPresent(NoSync::class.java) && setter.isAnnotationPresent(NoSync::class.java))
                meta.addFlag(SavingFieldFlag.NOSYNC)

            map.put(name, FieldCache(meta,
                    { obj -> wrapperForGetter(obj, arrayOf()) },
                    { obj, inp -> wrapperForSetter(obj, arrayOf(inp)) }))
        }
    }

    private val alreadyDone = mutableListOf<String>()
    private val ILLEGAL_NAMES = listOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")

    private val nameMap = mutableMapOf<Field, String>()

    private fun getNameFromField(clazz: Class<*>, f: Field): String {
        val got = nameMap[f]
        if (got != null) return got

        val string = if(f.isAnnotationPresent(Save::class.java)) f.getAnnotation(Save::class.java).saveName else ""
        var name = if (string == "") f.name else string
        if (name in ILLEGAL_NAMES)
            name += "X"
        if (name in alreadyDone) {
            val msg = "Name $name already in use for class ${clazz.name}! Adding dashes to the end to mitigate this."
            val pad = "*" * msg.length
            LibrarianLog.warn(pad)
            LibrarianLog.warn(msg)
            LibrarianLog.warn(pad)
            while (name in alreadyDone)
                name += "-"
        }
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

        var uses = 0
        for (i in alreadyDone)
            if (i == name) uses++

        if (uses > 1)
            throw IllegalArgumentException("Method savename $name already in use for class ${clazz.name}, this is illegal for methods.")
        alreadyDone.add(name)
        return name
    }
}

data class FieldCache(val meta: FieldMetadata, val getter: (Any) -> Any?, private val setter_: (Any, Any?) -> Unit, var name: String = "") {
    val setter = if(meta.hasFlag(SavingFieldFlag.NONNULL)) {
        { instance: Any, value: Any? ->
            if(value == null) {
                setter_(instance, DefaultValues.getDefaultValue(meta.type))
            } else {
                setter_(instance, value)
            }
        }
    } else {
        setter_
    }
}

data class FieldMetadata private constructor(val type: FieldType, private var flagsInternal: MutableSet<SavingFieldFlag>) {
    constructor(type: FieldType, vararg flags: SavingFieldFlag) : this(type, EnumSet.noneOf(SavingFieldFlag::class.java).let { it.addAll(flags); it })

    val flags: Set<SavingFieldFlag>
        get() = flagsInternal

    fun hasFlag(flag: SavingFieldFlag) = flags.contains(flag)
    fun containsOnly(vararg allowed: SavingFieldFlag) = flags.containsAll(allowed.asList())
    fun doesNotContain(vararg disallowed: SavingFieldFlag) = !disallowed.any { it in flags }

    fun addFlag(flag: SavingFieldFlag) {
        flagsInternal.add(flag)
    }
    fun removeFlag(flag: SavingFieldFlag) {
        flagsInternal.remove(flag)
    }
}

enum class SavingFieldFlag { FIELD, METHOD, ANNOTATED, NONNULL, NOSYNC, TRANSIENT, FINAL }
