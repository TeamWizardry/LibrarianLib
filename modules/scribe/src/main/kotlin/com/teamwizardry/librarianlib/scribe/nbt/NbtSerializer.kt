package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import dev.thecodewarrior.prism.Prism
import dev.thecodewarrior.prism.SerializationException
import dev.thecodewarrior.prism.Serializer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

public typealias NbtPrism = Prism<NbtSerializer<*>>

public abstract class NbtSerializer<T: Any>: Serializer<T> {
    public constructor(type: TypeMirror): super(type)
    public constructor(): super()

    protected abstract fun deserialize(tag: Tag, existing: T?): T
    protected abstract fun serialize(value: T): Tag

    @Suppress("UNCHECKED_CAST")
    public fun read(tag: Tag, existing: Any?): Any {
        try {
            return deserialize(tag, existing as T?)
        } catch (e: Exception) {
            throw DeserializationException("Error deserializing $type", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    public fun write(value: Any): Tag {
        try {
            return serialize(value as T)
        } catch (e: Exception) {
            throw SerializationException("Error serializing $type", e)
        }
    }

    /**
     * Throws an exception if [tag] is null or it isn't the passed type, using [name] as the property name in the
     * message. The message will be one of:
     * - `Missing <<name>>`
     * - `Unexpected type <<actual type>> for <<name>>`
     */
    public fun<T: Tag> expectType(tag: Tag?, type: Class<T>, name: String): T {
        if(tag == null)
            throw DeserializationException("Missing $name")
        if(!type.isAssignableFrom(tag.javaClass))
            throw DeserializationException("Unexpected type ${tag.reader.crashReportName} for $name")
        @Suppress("UNCHECKED_CAST")
        return tag as T
    }

    /**
     * Throws an exception if this tag is null or it isn't the passed type, using [name] as the property name in the
     * message. The message will be one of:
     * - `Missing <<name>>`
     * - `Unexpected type <<actual type>> for <<name>>`
     */
    public inline fun<reified T: Tag> Tag?.expectType(name: String): T {
        return expectType(this, T::class.java, name)
    }

    /**
     * Throws an exception if [key] is missing from [compound] or it isn't the passed type.
     */
    public fun<T: Tag> expect(compound: CompoundTag, key: String, type: Class<T>): T {
        val tag = compound[key] ?: throw DeserializationException("Missing `$key`")
        if(!type.isAssignableFrom(tag.javaClass))
            throw DeserializationException("Unexpected type ${tag.reader.crashReportName} for `$key`")
        @Suppress("UNCHECKED_CAST")
        return tag as T
    }

    /**
     * Throws an exception if [key] is missing from this tag or it isn't the passed type
     */
    public inline fun<reified T: Tag> CompoundTag.expect(key: String): T {
        return expect(this, key, T::class.java)
    }
}