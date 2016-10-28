package com.teamwizardry.librarianlib.common.util.saving

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import java.lang.invoke.MethodHandles.publicLookup
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

/**
 * @author WireSegal
 * Created at 1:43 PM on 10/14/2016.
 */
object SavingFieldCache : LinkedHashMap<Class<*>, Map<String, Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>>>() {
    @JvmStatic
    fun getClassFields(clazz: Class<*>): Map<String, Triple<Class<*>, (Any) -> Any?, (Any, Any?) -> Unit>> {
        val existing = this[clazz]
        if (existing != null) return existing

        val fields = clazz.declaredFields.filter {
            it.declaredAnnotations
            val mods = it.modifiers
            !Modifier.isStatic(mods) && !Modifier.isFinal(mods) && !Modifier.isTransient(mods) && it.isAnnotationPresent(Save::class.java)
        }

        val alreadyDone = mutableListOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")
        val map = linkedMapOf(*(fields.sortedBy {
            getNameFromField(clazz, it, alreadyDone)
        }.map {
            it.isAccessible = true
            getNameFromField(clazz, it, alreadyDone) to Triple(it.type,
                    MethodHandleHelper.wrapperForGetter<Any>(publicLookup().unreflectGetter(it)),
                    MethodHandleHelper.wrapperForSetter<Any>(publicLookup().unreflectSetter(it)))
        }).toTypedArray())

        put(clazz, map)

        return map
    }

    private val nameMap = mutableMapOf<Field, String>()
    private fun getNameFromField(clazz: Class<*>, f: Field, alreadyDone: MutableList<String>): String {
        val got = nameMap[f]
        if (got != null) return got

        val string = f.getAnnotation(Save::class.java).saveName
        var name = if (string == "") f.name else string
        if (name in alreadyDone) {
            val msg = "Name $name already in use for class ${clazz.name}! Adding dashes to the end to mitigate this."
            val pad = Array(msg.length) { "*" }.joinToString("")
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
}
