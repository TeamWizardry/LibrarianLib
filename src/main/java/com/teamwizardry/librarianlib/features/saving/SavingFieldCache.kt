package com.teamwizardry.librarianlib.features.saving

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.kotlin.DefaultedMutableMap
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import org.jetbrains.annotations.NotNull
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

/**
 * @author WireSegal
 * Created at 1:43 PM on 10/14/2016.
 */
object SavingFieldCache {

    val atSaveMap = LinkedHashMap<FieldType, Map<String, FieldCache>>()

    @JvmStatic
    fun getClassFields(type: FieldType): Map<String, FieldCache> {
        val existing = atSaveMap[type]
        if (existing != null) return existing

        val map = linkedMapOf<String, FieldCache>()
        buildClassFields(type, map)
        buildClassGetSetters(type, map)
        alreadyDone.clear()
        methodGettersWOSetters.clear()
        methodSettersWOGetters.clear()

        atSaveMap[type] = map

        return map
    }

    fun buildClassFields(type: FieldType, map: MutableMap<String, FieldCache>) {
        val fields = mutableSetOf<Field>()
        val properties = mutableSetOf<KProperty<*>>()

        var clazz: Class<*>? = type.clazz
        while (clazz != null) {
            clazz.declaredFields.filterTo(fields) {
                it.declaredAnnotations
                !Modifier.isStatic(it.modifiers)
            }
            properties.addAll(clazz.kotlin.declaredMemberProperties)

            clazz = clazz.superclass
        }

        fields.removeIf { it.kotlinProperty in properties }

        fields.map {
            getNameFromField(type, it) to it
        }.forEach {
                    val (name, field) = it
                    field.isAccessible = true

                    val meta = createMetaForField(field, type)

                    if (meta.containsAny())
                        map[name] = FieldCache(
                                meta,
                                getFieldGetter(field),
                                getFieldSetter(field, type),
                                field.name
                        )
                }


        properties.forEach {
            if (it.findAnnotation<Save>() != null) {
                val name = getNameFromProperty(type, it)


                val meta = FieldMetadata(FieldType.create(it), SavingFieldFlag.ANNOTATED, SavingFieldFlag.METHOD)

                it.annotations.forEach {
                    meta.addAnnotation(it, true)
                }

                if (!it.returnType.isMarkedNullable)
                    meta.addFlag(SavingFieldFlag.NONNULL)
                if (it.javaField == null || it.findAnnotation<NoSync>() != null)
                    meta.addFlag(SavingFieldFlag.NO_SYNC)
                if (it.findAnnotation<NonPersistent>() != null)
                    meta.addFlag(SavingFieldFlag.NON_PERSISTENT)
                if (it !is KMutableProperty<*>)
                    meta.addFlag(SavingFieldFlag.FINAL)


                if (meta.hasFlag(SavingFieldFlag.NO_SYNC) && meta.hasFlag(SavingFieldFlag.NON_PERSISTENT))
                    errorList[type][name].add("Annotated with both @NoSync and @NonPersistent. This field will never be used.")

                val setterLambda: (Any, Any?) -> Unit = if (it !is KMutableProperty<*>)
                    { _, _ -> throw IllegalAccessException("Tried to set final property $name for class $type (no save setter)") }
                else
                    { obj, inp -> it.setter.call(obj, inp) }

                map[name] = FieldCache(meta,
                        { obj -> it.getter.call(obj) },
                        setterLambda)
            }
        }
    }

    fun getFieldGetter(field: Field): (Any) -> Any? {
        return getKotlinFieldGetter(field) ?: getJavaFieldGetter(field)
    }

    fun getKotlinFieldGetter(field: Field): ((Any) -> Any?)? {
        val property = field.kotlinProperty ?: return null
        val method = property.getter.javaMethod ?: return null
        method.isAccessible = true
        val handle = MethodHandleHelper.wrapperForMethod<Any>(method)
        return { obj -> handle(obj, arrayOf()) }
    }

    fun getJavaFieldGetter(field: Field) = MethodHandleHelper.wrapperForGetter<Any>(field)

    fun getFieldSetter(field: Field, enclosing: FieldType): (Any, Any?) -> Unit {
        return if (Modifier.isFinal(field.modifiers))
            getFinalFieldSetter(field, enclosing)
        else
            getKotlinFieldSetter(field) ?: getJavaFieldSetter(field)
    }

    fun getKotlinFieldSetter(field: Field): ((Any, Any?) -> Unit)? {
        val property = field.kotlinProperty
        if (property == null || property !is KMutableProperty<*>)
            return null
        val method = property.setter.javaMethod ?: return null
        method.isAccessible = true
        val handle = MethodHandleHelper.wrapperForMethod<Any>(method)
        return { obj, value -> handle(obj, arrayOf(value)) }
    }

    fun getJavaFieldSetter(field: Field) = MethodHandleHelper.wrapperForSetter<Any>(field)

    fun getFinalFieldSetter(field: Field, enclosing: FieldType): (Any, Any?) -> Unit =
            { _, _ -> throw IllegalAccessException("Tried to set final field/property ${field.name} for class $enclosing (final field)") }

    fun createMetaForField(field: Field, enclosing: FieldType): FieldMetadata {
        val resolved = enclosing.resolve(field.genericType, field.annotatedType)

        val meta = FieldMetadata(resolved, SavingFieldFlag.FIELD)

        addJavaFlagsForField(field, meta)
        addAnnotationFlagsForField(field, meta)
        addAnnotationsForField(field, meta)

        return meta
    }

    fun addJavaFlagsForField(field: Field, meta: FieldMetadata) {
        val mods = field.modifiers

        if (Modifier.isFinal(mods)) meta.addFlag(SavingFieldFlag.FINAL)
        if (Modifier.isTransient(mods)) meta.addFlag(SavingFieldFlag.TRANSIENT)
        if (field.type.isPrimitive) meta.addFlag(SavingFieldFlag.NONNULL)
    }

    fun addAnnotationsForField(field: AccessibleObject, meta: FieldMetadata) {
        field.declaredAnnotations.forEach {
            meta.addAnnotation(it, false)
            meta.addAnnotation(it, true)
        }
    }

    fun addAnnotationFlagsForField(field: AccessibleObject, meta: FieldMetadata) {
        if (field.isAnnotationPresent(Save::class.java)) meta.addFlag(SavingFieldFlag.ANNOTATED)
        if (field.isAnnotationPresent(Module::class.java)) meta.addFlag(SavingFieldFlag.MODULE)
        if (field.isAnnotationPresent(NotNull::class.java)) meta.addFlag(SavingFieldFlag.NONNULL)
        if (field.isAnnotationPresent(NoSync::class.java)) meta.addFlag(SavingFieldFlag.NO_SYNC)
        if (field.isAnnotationPresent(NonPersistent::class.java)) meta.addFlag(SavingFieldFlag.NON_PERSISTENT)
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
    }

    fun buildClassGetSetters(type: FieldType, map: MutableMap<String, FieldCache>) {
        val getters = mutableMapOf<String, Method>()
        val setters = mutableMapOf<String, Method>()

        val methods = mutableSetOf<Method>()
        var clazz: Class<*>? = type.clazz
        while (clazz != null) {
            clazz.declaredMethods.filterTo(methods) {
                it.declaredAnnotations
                !Modifier.isStatic(it.modifiers)
            }

            clazz = clazz.superclass
        }

        methods.forEach {
            if (it.isAnnotationPresent(SaveMethodGetter::class.java)) {
                val types = it.parameterTypes
                val name = getNameFromMethod(type, it, true)
                if (types.isEmpty()) {
                    getters[name] = it
                } else {
                    errorList[type][name].add("Getter has parameters")
                }
            }
            if (it.isAnnotationPresent(SaveMethodSetter::class.java)) {
                val types = it.parameterTypes
                val name = getNameFromMethod(type, it, false)
                if (types.size == 1) {
                    setters[name] = it
                } else {
                    errorList[type][name].add("Setter has ${types.size} parameters, they must have exactly 1")
                }
            }
        }

        val pairs = mutableMapOf<String, Triple<Method, Method?, Class<*>>>()
        getters.forEach {
            val (name, getter) = it
            val getReturnType = getter.returnType
            if (name in setters) {
                val setter = setters[name]!!
                if (setter.parameterCount == 0)
                    errorList[type][name].add("Setter has no parameters")
                val setReturnType = setter.parameterTypes[0]
                if (getReturnType == setReturnType)
                    pairs[name] = Triple(getter, setter, getReturnType)
                else
                    errorList[type][name].add("Getter and setter have mismatched types")
            }
        }

        setters.filterKeys { it !in getters }.forEach {
            val name = it.key
            errorList[type][name].add("Setter has no getter")
        }


        pairs.toList().sortedBy {
            it.first
        }.forEach {
                    val (name, triple) = it
                    val (getter, setter, propertyType) = triple
                    getter.isAccessible = true
                    setter?.isAccessible = true

                    val wrapperForGetter = MethodHandleHelper.wrapperForMethod<Any>(getter)
                    val wrapperForSetter = setter?.let { MethodHandleHelper.wrapperForMethod<Any>(it) }

                    val meta = FieldMetadata(FieldType.create(getter), SavingFieldFlag.ANNOTATED, SavingFieldFlag.METHOD)

                    getter.declaredAnnotations.forEach {
                        meta.addAnnotation(it, true)
                    }
                    setter?.declaredAnnotations?.forEach {
                        meta.addAnnotation(it, false)
                    }

                    if (isGetSetPairNotNull(getter, setter))
                        meta.addFlag(SavingFieldFlag.NONNULL)
                    if (propertyType.isPrimitive)
                        meta.addFlag(SavingFieldFlag.NONNULL)
                    if (getter.isAnnotationPresent(NoSync::class.java) && (setter == null || setter.isAnnotationPresent(NoSync::class.java)))
                        meta.addFlag(SavingFieldFlag.NO_SYNC)
                    if (getter.isAnnotationPresent(NonPersistent::class.java) && (setter == null || setter.isAnnotationPresent(NonPersistent::class.java)))
                        meta.addFlag(SavingFieldFlag.NON_PERSISTENT)
                    if (setter == null)
                        meta.addFlag(SavingFieldFlag.FINAL)

                    if (meta.hasFlag(SavingFieldFlag.NO_SYNC) && meta.hasFlag(SavingFieldFlag.NON_PERSISTENT))
                        errorList[type][name].add("Annotated with both @NoSync and @NonPersistent. This field will never be used.")

                    val setterLambda: (Any, Any?) -> Unit = if (wrapperForSetter == null)
                        { _, _ -> throw IllegalAccessException("Tried to set final property $name for class $type (no save setter)") }
                    else
                        { obj, inp -> wrapperForSetter(obj, arrayOf(inp)) }

                    map[name] = FieldCache(meta,
                            { obj -> wrapperForGetter(obj, arrayOf()) },
                            setterLambda)
                }
    }

    private fun isGetSetPairNotNull(getter: Method, setter: Method?): Boolean {
        return isGetterMethodNotNull(getter) && (setter == null || isSetterMethodNotNull(setter))
    }

    private fun isGetterMethodNotNull(getter: Method): Boolean {
        val kt = getter.kotlinFunction
        return getter.isAnnotationPresent(NotNull::class.java) || (kt != null && !kt.returnType.isMarkedNullable)
    }

    private fun isSetterMethodNotNull(setter: Method): Boolean {
        val kt = setter.kotlinFunction
        return setter.parameterAnnotations[0].any { it is NotNull } || (kt != null && !kt.parameters[0].type.isMarkedNullable)
    }

    private val alreadyDone = mutableListOf<String>()
    private val methodGettersWOSetters = mutableSetOf<String>()
    private val methodSettersWOGetters = mutableSetOf<String>()

    private val ILLEGAL_NAMES = listOf("id", "x", "y", "z", "ForgeData", "ForgeCaps")

    private val nameMap = mutableMapOf<Field, String>()
    private val namePropMap = mutableMapOf<KProperty<*>, String>()

    private fun getNameFromProperty(type: FieldType, f: KProperty<*>): String {
        val got = namePropMap[f]
        if (got != null) return got

        val string = f.findAnnotation<Save>()?.saveName ?: ""
        var name = if (string == "") f.name else string

        if (name in ILLEGAL_NAMES)
            name += "X"
        if (name in alreadyDone)
            errorList[type][name].add("Name already in use for field")

        alreadyDone.add(name)
        namePropMap[f] = name
        return name
    }

    private fun getNameFromField(type: FieldType, f: Field): String {
        val got = nameMap[f]
        if (got != null) return got

        val string = if (f.isAnnotationPresent(Save::class.java)) f.getAnnotation(Save::class.java).saveName else ""
        var name = if (string == "") f.name else string

        if (name in ILLEGAL_NAMES)
            name += "X"
        if (name in alreadyDone)
            errorList[type][name].add("Name already in use for field")

        alreadyDone.add(name)
        nameMap[f] = name
        return name
    }

    private fun getNameFromMethod(type: FieldType, m: Method, getter: Boolean): String {
        var name = if (getter)
            m.getAnnotation(SaveMethodGetter::class.java).saveName
        else
            m.getAnnotation(SaveMethodSetter::class.java).saveName

        if (name in ILLEGAL_NAMES)
            name += "X"

        var partOfUnmatchedPair = false

        partOfUnmatchedPair = partOfUnmatchedPair || getter && name in methodSettersWOGetters
        partOfUnmatchedPair = partOfUnmatchedPair || !getter && name in methodGettersWOSetters

        if (!partOfUnmatchedPair && name in alreadyDone)
            errorList[type][name].add("Name already in use for ${if (getter) "getter" else "setter"}")

        if (partOfUnmatchedPair) {
            methodSettersWOGetters.remove(name)
            methodGettersWOSetters.remove(name)
        } else {
            if (getter) {
                methodGettersWOSetters.add(name)
            } else {
                methodSettersWOGetters.add(name)
            }
        }

        alreadyDone.add(name)
        return name
    }

    private val errorList = mutableMapOf<FieldType, DefaultedMutableMap<String, MutableList<String>>>().withRealDefault { mutableMapOf<String, MutableList<String>>().withRealDefault { mutableListOf() } }

    fun handleErrors() {
        if (errorList.isEmpty())
            return

        val lines = mutableListOf<String>()

        errorList.forEach {
            val (_, props) = it
            lines.add("- ${it.key}")

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
                setter_(instance, SerializerRegistry.getDefault(meta.type))
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

data class FieldMetadata(val type: FieldType, private var flagsInternal: MutableSet<SavingFieldFlag>) {
    constructor(type: FieldType, vararg flags: SavingFieldFlag) : this(type, EnumSet.noneOf(SavingFieldFlag::class.java).let { it.addAll(flags); it })

    private var annotationsGet = mutableSetOf<Annotation>()
    private var annotationsSet = mutableSetOf<Annotation>()

    val flags: Set<SavingFieldFlag>
        get() = flagsInternal

    fun hasFlag(flag: SavingFieldFlag) = flags.contains(flag)
    fun containsAll(vararg list: SavingFieldFlag) = flags.containsAll(list.asList())
    fun containsAny(vararg list: SavingFieldFlag) = list.any { it in flags }
    fun containsAny() = flags.any()


    fun addFlag(flag: SavingFieldFlag) = flagsInternal.add(flag)
    fun removeFlag(flag: SavingFieldFlag) = flagsInternal.remove(flag)

    fun addAnnotation(annot: Annotation, getter: Boolean) {
        (if (getter) annotationsGet else annotationsSet).add(annot)
    }

    fun hasAnnotation(clazz: Class<*>): Boolean {
        @Suppress("UNCHECKED_CAST")
        return getAnnotation(clazz as Class<Annotation>) != null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Annotation> getAnnotation(clazz: Class<T>): T? {
        return getAnnotationGetter(clazz) ?: getAnnotationSetter(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Annotation> getAnnotationGetter(clazz: Class<T>): T? {
        return annotationsGet.find { clazz.isAssignableFrom(it.javaClass) } as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Annotation> getAnnotationSetter(clazz: Class<T>): T? {
        return annotationsSet.find { clazz.isAssignableFrom(it.javaClass) } as T?
    }
}

enum class SavingFieldFlag {
    FIELD, METHOD, ANNOTATED, NONNULL, NO_SYNC, NON_PERSISTENT, TRANSIENT, FINAL,
    CAPABILITY, CAPABILITY_UP, CAPABILITY_DOWN, CAPABILITY_NORTH, CAPABILITY_SOUTH, CAPABILITY_EAST, CAPABILITY_WEST,
    MODULE
}
