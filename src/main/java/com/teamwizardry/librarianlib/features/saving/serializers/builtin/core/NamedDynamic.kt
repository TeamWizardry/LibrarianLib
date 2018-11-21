package com.teamwizardry.librarianlib.features.saving.serializers.builtin.core

import com.google.common.collect.HashBiMap
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.autoregister.AMPRegister
import com.teamwizardry.librarianlib.features.autoregister.AnnotationMarkerProcessor
import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister
import com.teamwizardry.librarianlib.features.helpers.castOrDefault
import com.teamwizardry.librarianlib.features.kotlin.readVarInt
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import com.teamwizardry.librarianlib.features.kotlin.writeVarInt
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.NamedDynamic
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import java.util.*

/**
 * TODO: Document file RegistryDynamic
 *
 * Created by TheCodeWarrior
 */
@SerializerFactoryRegister
object SerializeNamedDynamicFactory : SerializerFactory("NamedDynamic") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
        if (type.clazz.isAnnotationPresent(NamedDynamic::class.java)) {
            return SerializerFactoryMatch.WRAPPER
        }
        return SerializerFactoryMatch.NONE
    }

    override fun create(type: FieldType): Serializer<*> {
        return SerializeDynamic(type)
    }

    class SerializeDynamic(type: FieldType) : Serializer<Any>(type) {

        private val alreadyWarnedNames = mutableSetOf<String>()
        private val alreadyWarnedIds = mutableSetOf<Int>()

        val defaultSerializer: Serializer<Any> by SerializerRegistry.lazy(type, SerializeNamedDynamicFactory)
        val registry = NamedDynamicRegistryManager.getRegistries(type.clazz)//.first()

        val serializersByClass = mutableMapOf<Class<*>, Serializer<Any>>().withRealDefault {
            SerializerRegistry.getOrCreate(FieldType.create(it), SerializeNamedDynamicFactory)
        }

        val serializersByName = mutableMapOf<String, Serializer<Any>>().withRealDefault { key ->
            val clazz = registry.mapNotNull { it.get(key) }
            if (clazz.isNotEmpty()) {
                return@withRealDefault serializersByClass[clazz.first()]
            } else {
                if (key !in alreadyWarnedNames) {
                    alreadyWarnedNames.add(key)
                    LibrarianLog.warn("Attempt to locate the class associated with the name `$key` with the base type" +
                            " `${registry.joinToString { it.baseType.toString() }}` failed. Attempting with the default serializer. This is a major" +
                            " problem and will likely cause strange bugs and crashes. This warning will only display" +
                            " once per name.")
                }
                return@withRealDefault defaultSerializer
            }
        }

        val serializersById = mutableMapOf<Int, Serializer<Any>>().withRealDefault { key ->
            val clazz = registry.mapNotNull { it.getByID(key) }
            if (clazz.isNotEmpty()) {
                return@withRealDefault serializersByClass[clazz.first()]
            } else {
                if (key !in alreadyWarnedIds) {
                    alreadyWarnedIds.add(key)
                    LibrarianLog.warn("Attempt to locate the class associated with the id `$key` with the base type" +
                            " `${registry.joinToString { it.baseType.toString() }}` failed. Attempting with the default serializer. This is a major" +
                            " problem and will likely cause strange bugs and crashes. This warning will only display" +
                            " once per id.")
                }
                return@withRealDefault defaultSerializer
            }
        }

        fun notFoundError(clazz: Class<*>) = IllegalArgumentException("Could not find entry for class `$clazz` with base" +
                " type `${registry.joinToString { it.baseType.toString() }}`. Please annotate all decedents of the base type that need to be serialized" +
                " with @NamedDynamic")

        override fun getDefault(): Any {
            return defaultSerializer.getDefault()
        }

        override fun readNBT(nbt: NBTBase, existing: Any?, syncing: Boolean): Any {
            val wrapper = nbt.castOrDefault(NBTTagCompound::class.java)

            val type = wrapper.getString("name")

            val ser = serializersByName[type]

            return ser.read(wrapper.getTag("data") ?: nbt, existing, syncing)
        }

        override fun writeNBT(value: Any, syncing: Boolean): NBTBase {
            val wrapper = NBTTagCompound()
            val name = registry.mapNotNull { it.get(value.javaClass) }.firstOrNull()
                    ?: throw notFoundError(value.javaClass)
            wrapper.setString("name", name)

            wrapper.setTag("data", serializersByClass[value.javaClass].write(value, syncing))

            return wrapper
        }

        override fun readBytes(buf: ByteBuf, existing: Any?, syncing: Boolean): Any {
            val id = buf.readVarInt()

            return serializersById[id].read(buf, existing, syncing)
        }

        override fun writeBytes(buf: ByteBuf, value: Any, syncing: Boolean) {
            val id = registry.mapNotNull { it.getID(value.javaClass) }.firstOrNull()
                    ?: throw notFoundError(value.javaClass)

            buf.writeVarInt(id)

            serializersByClass[value.javaClass].write(buf, value, syncing)
        }
    }
}

object NamedDynamicRegistryManager {
    private val registries = mutableMapOf<Class<*>, NamedDynamicRegistry>()

    @Deprecated("Switch to getRegistries")
    fun getRegistry(clazz: Class<*>): NamedDynamicRegistry {
        return registries.getOrPut(getUpperBound(clazz)) { NamedDynamicRegistry(clazz) }
    }

    fun getRegistries(clazz: Class<*>): List<NamedDynamicRegistry> {
        return getUpperBounds(clazz).map { registries.getOrPut(it) { NamedDynamicRegistry(it) } }
    }

    /*
    fun getRegistries(clazz: Class<*>): List<NamedDynamicRegistry> {
        var l = sequenceOf<NamedDynamicRegistry>()
        if (clazz.isEnum) clazz.enumConstants.forEach {
            val c = it::class.java
            if (c != clazz) l += getRegistries(c)
        }
        l += getUpperBounds(clazz).map { registries.getOrPut(it) { NamedDynamicRegistry(it) } }
        return l.toList()
    }
     */

    /**
     * Always use the class highest up the hierarchy that is annotated with @NamedDynamic. This is the "base type"
     */
    private fun getUpperBound(clazz: Class<*>): Class<*> {
        var current = clazz
        var bound = clazz
        while (true) {
            if (current.isAnnotationPresent(NamedDynamic::class.java))
                bound = current
            if (current == clazz.superclass)
                break
            current = clazz.superclass ?: break
        }
        return bound
    }

    /**
     * Similar to [getUpperBound] but handles interfaces as well
     */
    private fun getUpperBounds(clazz: Class<*>): List<Class<*>> {
        val stack: Deque<Class<*>> = LinkedList<Class<*>>()
        stack.push(clazz)
        val bounds = hashSetOf<Class<*>>()
        var current: Class<*>
        while (stack.isNotEmpty()) {
            current = stack.pop()
            val ifaces = current.interfaces.filter { it.isAnnotationPresent(NamedDynamic::class.java) }
            val parent = current.superclass
            if (parent?.isAnnotationPresent(NamedDynamic::class.java) == true) stack.push(parent)
            else if (ifaces.isEmpty()) bounds.add(current)
            ifaces.forEach { stack.push(it) }
        }

        return bounds.toList()
    }

    class NamedDynamicRegistry(val baseType: Class<*>) {
        private val map = HashBiMap.create<Class<*>, String>()
        private val ids = HashBiMap.create<Class<*>, Int>()
        private var nextID = 0

        fun add(clazz: Class<*>, name: String) {
            if (clazz in map)
                throw IllegalArgumentException("Duplicate type registration for `$clazz`!")
            map[clazz] = name
            ids[clazz] = nextID++
        }

        fun get(clazz: Class<*>): String? {
            return map[clazz]
        }

        fun get(name: String): Class<*>? {
            return map.inverse()[name]
        }

        fun getID(clazz: Class<*>): Int? {
            return ids[clazz]
        }

        fun getByID(id: Int): Class<*>? {
            return ids.inverse()[id]
        }

        operator fun contains(clazz: Class<*>) = clazz in map
        operator fun contains(name: String) = name in map.inverse()
        operator fun contains(id: Int) = id in ids.inverse()
    }
}

@AMPRegister
object NamedDynamicRegisterProcessor : AnnotationMarkerProcessor<NamedDynamic, Any>(NamedDynamic::class.java) {
    override fun process(clazz: Class<Any>, annotation: NamedDynamic) {
        NamedDynamicRegistryManager.getRegistries(clazz).forEach {
            it.add(clazz, annotation.resourceLocation)
            if (clazz.isEnum) clazz.enumConstants.forEach { e ->
                val c = e::class.java
                if (c != clazz) it.add(c, annotation.resourceLocation + "\$$e")
            }
        }
    }
}
