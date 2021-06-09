package com.teamwizardry.librarianlib.scribe.nbt

import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.NbtString
import net.minecraft.nbt.NbtElement
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

    override fun deserialize(tag: NbtElement, existing: T?): T {
        val id = Identifier(tag.expectType<NbtString>("id").asString())
        return registry.get(id) ?: throw DeserializationException("No entry in $name with id $id")
    }

    override fun serialize(value: T): NbtElement {
        val id = registry.getId(value)
        return NbtString.of(id.toString())
    }
}
