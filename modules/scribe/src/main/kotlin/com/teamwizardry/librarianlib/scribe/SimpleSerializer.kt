package com.teamwizardry.librarianlib.scribe

import com.teamwizardry.librarianlib.scribe.nbt.NbtSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.FieldMirror
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.InvalidTypeException
import dev.thecodewarrior.prism.PrismException
import dev.thecodewarrior.prism.SerializationException
import net.minecraft.nbt.NbtCompound
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.function.Predicate
import kotlin.reflect.full.hasAnnotation

/**
 * Any annotations with this meta-annotation will be treated as markers for simple serializers
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class SimpleSerializationMarker

public interface SimpleSerializer<T: Any> {
    /**
     * Create a tag containing the fields marked with any of the passed [markers] types.
     */
    public fun createTag(instance: T, vararg markers: Class<out Annotation>): NbtCompound

    /**
     * Create a tag containing the fields with marker annotations that succeed in the passed filter. If possible, prefer
     * using the version of [createTag] that accepts marker classes, since it can be more optimized.
     */
    public fun createTag(instance: T, predicate: Predicate<Collection<Annotation>>): NbtCompound

    /**
     * Applies a tag containing the fields marked with any of the passed [markers] types.
     */
    public fun applyTag(tag: NbtCompound, instance: T, vararg markers: Class<out Annotation>)

    /**
     * Applies a tag containing the fields that succeed in the passed filter. If possible, prefer using the version of
     * [applyTag] that accepts marker classes, since it can be more optimized.
     */
    public fun applyTag(tag: NbtCompound, instance: T, predicate: Predicate<Collection<Annotation>>)

    public companion object {
        private val typeMap = mutableMapOf<Class<*>, SimpleSerializerImpl<*>>()

        @JvmStatic
        public fun <T: Any> get(clazz: Class<T>): SimpleSerializer<T> {
            @Suppress("UNCHECKED_CAST")
            return typeMap.getOrPut(clazz) { SimpleSerializerImpl(clazz) } as SimpleSerializer<T>
        }

        private val nameMethods = mutableMapOf<Class<*>, MethodMirror>()

        @JvmStatic
        public fun getMarkerName(annotation: Annotation): String {
            val annotationClass = annotation.annotationClass.java
            if(!annotation.annotationClass.hasAnnotation<SimpleSerializationMarker>())
                throw IllegalArgumentException("Annotation type ${annotation.annotationClass.qualifiedName} " +
                        "is not a serialization marker")
            val method = nameMethods.getOrPut(annotationClass) {
                val method = Mirror.reflectClass(annotationClass).getMethod("value")
                if(method.returnType != Mirror.reflect<String>())
                    throw IllegalArgumentException("Expected `value` to be a String")
                method
            }
            return method.call(annotation)
        }
    }
}

private class SimpleSerializerImpl<T: Any>(val clazz: Class<T>): SimpleSerializer<T> {
    val mirror = Mirror.reflectClass(clazz)
    val properties = mutableListOf<Property>()

    init {
        logger.debug("FoundationSerialization: Scanning $mirror")

        val noSerializerErrors = mutableMapOf<FieldMirror, PrismException>()

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        fun Annotation.annotationType() = (this as java.lang.annotation.Annotation).annotationType()

        val nameMap = mutableMapOf<String, MutableList<FieldMirror>>()
        for(field in mirror.fields) {
            val markers = field.annotations.filter { it.annotationType().isAnnotationPresent(SimpleSerializationMarker::class.java) }
            if (markers.isEmpty()) continue
            if(field.isFinal)
                throw InvalidTypeException("SimpleSerializer can't modify the final field `${field.name}`")
            val names = markers.map { SimpleSerializer.getMarkerName(it) }
            if(names.count { it != "" } != 1)
                throw InvalidTypeException("Exactly one marker annotation for each field should have a name")
            val name = names.single { it != "" }

            logger.debug("Found marked field `$field`")

            nameMap.getOrPut(name) { mutableListOf() }.add(field)

            val serializer = try {
                Scribe.nbt[field.type]
            } catch (e: PrismException) {
                noSerializerErrors[field] = e
                logger.error("Error getting serializer for `$field`", e)
                null
            }

            if (serializer != null) {
                properties.add(Property(name, markers, markers.map { it.annotationType() }.toSet(), field,
                    serializer.value))
                logger.debug("Successfully added field `$field`")
            }
        }
        logger.debug("Found ${properties.size} fields")

        val duplicateErrors = nameMap.filterValues { it.size > 1 }

        if (duplicateErrors.isNotEmpty() || noSerializerErrors.isNotEmpty()) {
            var message = "Problems serializing class $mirror:"
            if (duplicateErrors.isNotEmpty()) {
                message += "\nDuplicate names:"
                duplicateErrors.forEach { (name, fields) ->
                    message += "\n- \"$name\""
                    fields.forEach { field ->
                        message += "\n  - ${field.annotations.joinToString("") { "$it " }}$field"
                    }
                }
            }
            if (noSerializerErrors.isNotEmpty()) {
                message += "\nUnable to find serializers: (search logs for \"Error getting serializer for\" to see full traces)"
                noSerializerErrors.forEach { (field, exception) ->
                    message += "\n- $field: $exception"
                }
            }
            throw InvalidTypeException(message)
        }
    }

    private data class Property(val name: String,
        val markers: List<Annotation>, val markerClasses: Set<Class<out Annotation>>,
        val field: FieldMirror, val nbtSerializer: NbtSerializer<*>
    )

    private inline fun createTagImpl(instance: T, test: (Property) -> Boolean): NbtCompound {
        val tag = NbtCompound()
        properties.forEach { property ->
            try {
                if (test(property)) {
                    val value = property.field.get<Any?>(instance)
                    if (value != null) {
                        tag.put(property.name, property.nbtSerializer.write(value))
                    }
                }
            } catch (e: Exception) {
                throw SerializationException("Error serializing property ${property.name} in ${mirror.simpleName}", e)
            }
        }
        return tag
    }

    override fun createTag(instance: T, vararg markers: Class<out Annotation>): NbtCompound {
        return createTagImpl(instance) { property -> markers.any { it in property.markerClasses } }
    }

    override fun createTag(instance: T, predicate: Predicate<Collection<Annotation>>): NbtCompound {
        return createTagImpl(instance) { property -> predicate.test(property.markers) }
    }

    private inline fun applyTagImpl(tag: NbtCompound, instance: T, test: (Property) -> Boolean) {
        for(property in properties) {
            try {
                if (test(property)) {
                    val propertyTag = tag.get(property.name)
                    val newValue = when {
                        propertyTag != null -> property.nbtSerializer.read(propertyTag)
                        property.field.isFinal -> continue // assume no tag and final field => keep existing value
                        else -> defaultValue(property.field.type)
                    }
                    property.field.set(instance, newValue)
                }
            } catch (e: Exception) {
                throw DeserializationException("Error deserializing property ${property.name} in ${mirror.simpleName}", e)
            }
        }
    }

    private fun defaultValue(type: TypeMirror): Any? {
        return when(type) {
            Mirror.types.boolean -> false
            Mirror.types.byte -> 0.toByte()
            Mirror.types.short -> 0.toShort()
            Mirror.types.int -> 0
            Mirror.types.long -> 0L
            Mirror.types.float -> 0f
            Mirror.types.double -> 0.0
            Mirror.types.char -> 0.toChar()
            else -> null
        }
    }

    override fun applyTag(tag: NbtCompound, instance: T, vararg markers: Class<out Annotation>) {
        applyTagImpl(tag, instance) { property -> markers.any { it in property.markerClasses } }
    }

    override fun applyTag(tag: NbtCompound, instance: T, predicate: Predicate<Collection<Annotation>>) {
        applyTagImpl(tag, instance) { property -> predicate.test(property.markers) }
    }

    companion object {
        private val logger = LibLibScribe.makeLogger<SimpleSerializer<*>>()
    }
}
