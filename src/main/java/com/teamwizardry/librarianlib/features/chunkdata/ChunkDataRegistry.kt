package com.teamwizardry.librarianlib.features.chunkdata

import net.minecraft.util.ResourceLocation
import net.minecraft.world.chunk.Chunk

/**
 * TODO: Document file ChunkDataRegistry
 *
 * Created by TheCodeWarrior
 */
object ChunkDataRegistry {
    init { ChunkWorldData }
    private val registry = mutableMapOf<Class<*>, ChunkDataRegistryItem>()
    private val classes = mutableMapOf<ResourceLocation, Class<*>>()

    @JvmStatic
    fun <T : ChunkData> register(name: ResourceLocation, clazz: Class<T>, constructor: (chunk: Chunk) -> T, applyTo: (chunk: Chunk) -> Boolean) {
        if(name in classes) {
            throw IllegalArgumentException("Duplicate chunk data name $name")
        }

        registry.put(clazz, ChunkDataRegistryItem(name, clazz, constructor, applyTo))
        classes.put(name, clazz)
    }

    @JvmStatic
    fun get(clazz: Class<*>): ChunkDataRegistryItem? {
        return registry[clazz]
    }

    @JvmStatic
    fun get(name: ResourceLocation): ChunkDataRegistryItem? {
        return registry[classes[name] ?: return null]
    }

    @JvmStatic
    fun getApplicable(chunk: Chunk): List<ChunkDataRegistryItem> {
        return registry.values.filter { it.applyTo(chunk) }
    }
}

data class ChunkDataRegistryItem(val name: ResourceLocation, val clazz: Class<*>, val constructor: (chunk: Chunk) -> ChunkData, val applyTo: (chunk: Chunk) -> Boolean)
