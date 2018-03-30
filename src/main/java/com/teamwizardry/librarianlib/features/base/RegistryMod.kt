package com.teamwizardry.librarianlib.features.base

import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.RegistryNamespaced


class RegistryMod<T : Any> : Iterable<RegistryMod.RegistryEntry<ResourceLocation, T>> {
    private val registry = RegistryNamespaced<ResourceLocation, T>()
    private var lastId = 0

    fun register(id: ResourceLocation, value: T) = value.apply { registry.register(lastId++, id, this) }
    fun getObjectByName(id: ResourceLocation): T? = registry.getObject(id)
    fun getObjectById(id: Int): T? = registry.getObjectById(id)
    fun getIdFromObject(value: T): ResourceLocation? = registry.getNameForObject(value)
    override fun iterator() = RegistryEntry.iterateOverRegistry(registry)

    data class RegistryEntry<out K, out V>(val id: Int, val key: K, val value: V) {
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
