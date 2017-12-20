package com.teamwizardry.librarianlib.features.worlddata

import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

/**
 * TODO: Document file ChunkDataRegistry
 *
 * Created by TheCodeWarrior
 */
object WorldDataRegistry {
    init {
        WorldData
    }

    private val registry = mutableMapOf<Class<*>, WorldDataRegistryItem>()
    private val classes = mutableMapOf<ResourceLocation, Class<*>>()

    @JvmStatic
    fun <T : WorldData> register(name: ResourceLocation, clazz: Class<T>, constructor: (container: WorldDataContainer) -> T, applyTo: (world: World) -> Boolean) {
        if (name in classes) {
            throw IllegalArgumentException("Duplicate chunk data name $name")
        }

        registry.put(clazz, WorldDataRegistryItem(name, clazz, constructor, applyTo))
        classes.put(name, clazz)
    }

    @JvmStatic
    fun get(clazz: Class<*>): WorldDataRegistryItem? {
        return registry[clazz]
    }

    @JvmStatic
    fun get(name: ResourceLocation): WorldDataRegistryItem? {
        return registry[classes[name] ?: return null]
    }

    @JvmStatic
    fun getApplicable(world: World): List<WorldDataRegistryItem> {
        return registry.values.filter { it.applyTo(world) }
    }
}

data class WorldDataRegistryItem(val name: ResourceLocation, val clazz: Class<*>, val constructor: (container: WorldDataContainer) -> WorldData, val applyTo: (world: World) -> Boolean)
