package com.teamwizardry.librarianlib.common.base

import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.RegistryNamespaced


/**
 * Created by Elad on 2/12/2017.
 */
abstract class RegistryMod<T> {
    private val registry = RegistryNamespaced<ResourceLocation, T>()
    private var lastId = 0

    fun register(id: ResourceLocation, oath: T): T {
        registry.register(lastId++, id, oath)
        return oath
    }

    fun getObjectByName(id: ResourceLocation): T? {
        return registry.getObject(id)
    }

    fun getObjectById(id: Int): T? {
        return registry.getObjectById(id)
    }

    fun getIdFromObject(oath: T): ResourceLocation? {
        return registry.getNameForObject(oath)
    }

    operator fun iterator(): Iterator<RegistryEntry<ResourceLocation, T>> {
        return RegistryEntry.iterateOverRegistry(registry)
    }

    data class RegistryEntry<out K, V>(val id: Int, val key: K, val value: V) {
        companion object {
            fun <K, V> iterateOverRegistry(registry: RegistryNamespaced<K, V>): Iterator<RegistryEntry<K, V>> {
                return object : Iterator<RegistryEntry<K, V>> {
                    private var underlying = registry.iterator()

                    override fun hasNext(): Boolean {
                        return underlying.hasNext()
                    }

                    override fun next(): RegistryEntry<K, V> {
                        val next = underlying.next()
                        val key = registry.getNameForObject(next)!!

                        return RegistryEntry(registry.getIDForObject(next), key, next)
                    }
                }
            }
        }
    }

}
