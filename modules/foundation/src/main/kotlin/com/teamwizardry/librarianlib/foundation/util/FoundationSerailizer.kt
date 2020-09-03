package com.teamwizardry.librarianlib.foundation.util

import com.teamwizardry.librarianlib.foundation.LibrarianLibFoundationModule
import com.teamwizardry.librarianlib.prism.NBTPrism
import com.teamwizardry.librarianlib.prism.nbt.NBTSerializer
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.FieldMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.PrismException
import net.minecraft.nbt.CompoundNBT
import java.util.function.Predicate

interface FoundationSerializer {
    /**
     * Create a tag containing the fields marked with any of the passed [markers] types.
     */
    fun createTag(instance: Any, vararg markers: Class<out Annotation>): CompoundNBT

    /**
     * Create a tag containing the fields with marker annotations that succeed in the passed filter. If possible, prefer
     * using the version of [createTag] that accepts marker classes, since it can be more optimized.
     */
    fun createTag(instance: Any, predicate: Predicate<Collection<Annotation>>): CompoundNBT

    /**
     * Applies a tag containing the fields marked with any of the passed [markers] type.
     */
    fun applyTag(tag: CompoundNBT, instance: Any, vararg markers: Class<out Annotation>)

    /**
     * Applies a tag containing the fields with marker annotations that succeed in the passed filter. If possible,
     * prefer using the version of [applyTag] that accepts marker classes, since it can be more optimized.
     */
    fun applyTag(tag: CompoundNBT, instance: Any, predicate: Predicate<Collection<Annotation>>)

    companion object {
        private val typeMap = mutableMapOf<Class<*>, FoundationSerializerImpl>()

        @JvmStatic
        fun get(clazz: Class<*>): FoundationSerializer {
            return typeMap.getOrPut(clazz) { FoundationSerializerImpl(clazz) }
        }
    }
}

/**
 * Any annotations with this meta-annotation will be treated as markers for Foundation's serialization system.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class SerializationMarker

/**
 * Sets a custom name for a property
 */
@Target(AnnotationTarget.FIELD)
annotation class PropertyName(val name: String)

private class FoundationSerializerImpl(val clazz: Class<*>): FoundationSerializer {
    val mirror = Mirror.reflectClass(clazz)
    val properties = mutableListOf<Property>()

    init {
        logger.debug("FoundationSerialization: Scanning $mirror")

        val finalErrors = mutableListOf<FieldMirror>()
        val noSerializerErrors = mutableMapOf<FieldMirror, PrismException>()

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        fun Annotation.annotationType() = (this as java.lang.annotation.Annotation).annotationType()

        val nameMap = mutableMapOf<String, MutableList<FieldMirror>>()
        mirror.fields.forEach { field ->
            val name = field.getAnnotation<PropertyName>()?.name ?: field.name

            val markers = field.annotations.filter { it.annotationType().isAnnotationPresent(SerializationMarker::class.java) }
            if (markers.isEmpty()) return@forEach
            logger.debug("Found marked field $field")

            nameMap.getOrPut(name) { mutableListOf() }.add(field)
            if (field.isFinal)
                finalErrors.add(field)

            val serializer = try {
                NBTPrism[field.type]
            } catch (e: PrismException) {
                noSerializerErrors[field] = e
                logger.error("Error getting serializer for $field", e)
                null
            }

            if (!field.isFinal && serializer != null) {
                properties.add(Property(name, markers, markers.map { it.annotationType() }.toSet(), field,
                    serializer.value))
                logger.debug("Successfully added field $field")
            }
        }
        logger.debug("Found ${properties.size} fields")

        val duplicateErrors = nameMap.filterValues { it.size > 1 }

        if (finalErrors.isNotEmpty() || duplicateErrors.isNotEmpty() || noSerializerErrors.isNotEmpty()) {
            var message = "Problems serializing class $mirror:"
            if (finalErrors.isNotEmpty()) {
                message += "\nFinal fields not supported:\n" + finalErrors.joinToString("\n") { "- $it" }
            }
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
            throw InvalidSerializedClassException(message)
        }
    }

    private data class Property(val name: String, val markers: List<Annotation>, val markerClasses: Set<Class<out Annotation>>,
        val field: FieldMirror, val nbtSerializer: NBTSerializer<*>)

    private inline fun createTagImpl(instance: Any, test: (Property) -> Boolean): CompoundNBT {
        val tag = CompoundNBT()
        properties.forEach { property ->
            try {
                if (test(property)) {
                    val value = property.field.get<Any?>(instance)
                    if (value != null) {
                        tag.put(property.name, property.nbtSerializer.write(value))
                    }
                }
            } catch (e: Exception) {
                throw FoundationSerializerException("Error serializing property ${property.name} in ${mirror.simpleName}")
            }
        }
        return tag
    }

    override fun createTag(instance: Any, vararg markers: Class<out Annotation>): CompoundNBT {
        return createTagImpl(instance) { property -> markers.any { it in property.markerClasses } }
    }

    override fun createTag(instance: Any, predicate: Predicate<Collection<Annotation>>): CompoundNBT {
        return createTagImpl(instance) { property -> predicate.test(property.markers) }
    }

    private inline fun applyTagImpl(tag: CompoundNBT, instance: Any, test: (Property) -> Boolean) {
        properties.forEach { property ->
            try {
                if (test(property)) {
                    val existingValue = property.field.get<Any?>(instance)
                    val newValue = tag.get(property.name)?.let {
                        property.nbtSerializer.read(it, existingValue)
                    } ?: defaultValue(property.field.type)
                    property.field.set(instance, newValue)
                }
            } catch (e: Exception) {
                throw FoundationSerializerException("Error deserializing property ${property.name} in ${mirror.simpleName}")
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

    override fun applyTag(tag: CompoundNBT, instance: Any, vararg markers: Class<out Annotation>) {
        applyTagImpl(tag, instance) { property -> markers.any { it in property.markerClasses } }
    }

    override fun applyTag(tag: CompoundNBT, instance: Any, predicate: Predicate<Collection<Annotation>>) {
        applyTagImpl(tag, instance) { property -> predicate.test(property.markers) }
    }

    companion object {
        private val logger = LibrarianLibFoundationModule.makeLogger<FoundationSerializer>()
    }
}
