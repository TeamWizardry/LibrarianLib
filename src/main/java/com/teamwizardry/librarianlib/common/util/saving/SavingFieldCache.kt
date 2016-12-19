package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.handles.MethodHandleHelper
import com.teamwizardry.librarianlib.common.util.times
import org.jetbrains.annotations.NotNull
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import javax.annotation.Nonnull

/**
 * @author WireSegal
 * Created at 1:43 PM on 10/14/2016.
 */
object SavingFieldCache : LinkedHashMap<Class<*>, Map<String, FieldCache>>() {
    @JvmStatic
    fun getClassFields(clazz: Class<*>): Map<String, FieldCache> {
        val existing = this[clazz]
        if (existing != null) return existing

        val map = linkedMapOf<String, FieldCache>()
        buildClassFields(clazz, map)
        buildClassGetSetters(clazz, map)
        alreadyDone.clear()

        put(clazz, map)

        return map
    }

    fun buildClassFields(clazz: Class<*>, map: MutableMap<String, FieldCache>) {
        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            val mods = it.modifiers
            !Modifier.isStatic(mods) && !Modifier.isFinal(mods) && !Modifier.isTransient(mods) && it.isAnnotationPresent(Save::class.java)
        }

        fields.map {
            getNameFromField(clazz, it) to it
        }.forEach {
            val (name, field) = it
            field.isAccessible = true
            map.put(name, FieldCache(FieldType.create(field),
                    MethodHandleHelper.wrapperForGetter<Any>(field),
                    MethodHandleHelper.wrapperForSetter<Any>(field),
                    field.isAnnotationPresent(NotNull::class.java) || field.isAnnotationPresent(Nonnull::class.java),
                    !field.isAnnotationPresent(NoSync::class.java),
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
            val (getter, setter) = triple
            getter.isAccessible = true
            setter.isAccessible = true

            val wrapperForGetter = MethodHandleHelper.wrapperForMethod<Any>(getter)
            val wrapperForSetter = MethodHandleHelper.wrapperForMethod<Any>(setter)

            map.put(name, FieldCache(FieldType.create(getter),
                    { obj -> wrapperForGetter(obj, arrayOf()) },
                    { obj, inp -> wrapperForSetter(obj, arrayOf(inp)) },
                    getter.isAnnotationPresent(NotNull::class.java) || setter.parameterAnnotations[0].any { it is NotNull },
                    !getter.isAnnotationPresent(NoSync::class.java) || !setter.isAnnotationPresent(NoSync::class.java)))
        }
    }

    private val alreadyDone = mutableListOf<String>()
    private val ILLEGAL_NAMES = listOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")

    private val nameMap = mutableMapOf<Field, String>()
    private fun getNameFromField(clazz: Class<*>, f: Field): String {
        val got = nameMap[f]
        if (got != null) return got

        val string = f.getAnnotation(Save::class.java).saveName
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

        val uses = alreadyDone.count { it == name }

        if (uses > 1)
            throw IllegalArgumentException("Method savename $name already in use for class ${clazz.name}, this is illegal for methods.")
        alreadyDone.add(name)
        return name
    }
}

data class FieldCache(val type: FieldType, val getter: (Any) -> Any?, private val setter_: (Any, Any?) -> Unit, val nonnull: Boolean, val syncToClient: Boolean, var name: String = "") {
    val setter = if(nonnull) {
        { instance: Any, value: Any? ->
            if(value == null) {
                setter_(instance, DefaultValues.getDefaultValue(type))
            } else {
                setter_(instance, value)
            }
        }
    } else {
        setter_
    }
}
