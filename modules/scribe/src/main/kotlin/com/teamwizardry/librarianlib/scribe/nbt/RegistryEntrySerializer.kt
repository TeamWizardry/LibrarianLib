package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.lang.IllegalArgumentException

public class RegistryEntrySerializer<T : Any>(private val registry: Registry<T>, type: TypeMirror) : NbtSerializer<T>(type) {
    public val name: Identifier

    init {
        @Suppress("UNCHECKED_CAST")
        val registries = Registry.REGISTRIES as Registry<Registry<*>>
        name = registries.getId(registry) ?: throw IllegalArgumentException("Couldn't find registry")
    }

    override fun deserialize(tag: Tag, existing: T?): T {
        val id = Identifier(tag.expectType<StringTag>("id").asString())
        return registry.get(id) ?: throw DeserializationException("No entry in $name with id $id")
    }

    override fun serialize(value: T): Tag {
        val id = registry.getId(value)
        return StringTag.of(id.toString())
    }
}
