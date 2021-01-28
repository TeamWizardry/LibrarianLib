package com.teamwizardry.librarianlib.mirage

import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import net.minecraft.resources.FallbackResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourcePack
import net.minecraft.resources.ResourcePack
import net.minecraft.resources.ResourcePackType
import net.minecraft.resources.SimpleReloadableResourceManager
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.LanguageMap
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.concurrent.read
import kotlin.concurrent.write

public class VirtualResourceManager(public val type: ResourcePackType) {
    public val mixinBridge: MixinBridge = MixinBridge()
    private val fallback = FallbackResourceManager(type, "librarianlib")
    private val files = mutableMapOf<ResourceLocation, ByteArray>().synchronized()
    private val generators = mutableMapOf<ResourceLocation, Supplier<ByteArray>>().synchronized()
    private val packs = mutableListOf<VirtualResourcePack>().synchronized()
    private val lock = ReentrantReadWriteLock()

    public fun remove(location: ResourceLocation): Boolean {
        lock.write {
            var removed = false
            removed =
                removeFile(location) || removed // can't do removeFile || removeGenerator because of short-circuiting
            removed = removeGenerator(location) || removed
            return removed
        }
    }

    public fun removeFile(location: ResourceLocation): Boolean {
        lock.write {
            if (files.remove(location) != null) {
                logger.debug("Removed file $location")
                return true
            }
            return false
        }
    }

    public fun removeGenerator(location: ResourceLocation): Boolean {
        lock.write {
            if (generators.remove(location) != null) {
                logger.debug("Removed generator $location")
                return true
            }
            return false
        }
    }

    public fun add(location: ResourceLocation, text: String) {
        addRaw(location, text.toByteArray())
    }

    public fun addRaw(location: ResourceLocation, data: ByteArray) {
        lock.write {
            files[location] = data
            logger.debug("Added resource $location")
        }
    }

    /**
     * Note: the passed generator may be run on another thread
     */
    public fun add(location: ResourceLocation, generator: Supplier<String>) {
        addRaw(location) {
            generator.get().toByteArray()
        }
    }

    /**
     * Note: the passed generator may be run on another thread
     */
    public fun addRaw(location: ResourceLocation, generator: Supplier<ByteArray>) {
        lock.write {
            generators[location] = generator
            logger.debug("Added resource generator $location")
        }
    }

    private inline fun <T> read(callback: (VirtualResourceManager) -> T): T = lock.read { callback(this) }
    private inline fun <T> write(callback: (VirtualResourceManager) -> T): T = lock.write { callback(this) }

    public inner class MixinBridge {

        public fun getResourceStream(location: ResourceLocation): InputStream? {
            return read {
                files[location]?.also {
                    return@read ByteArrayInputStream(it)
                }
                generators[location]?.also {
                    return@read ByteArrayInputStream(it.get())
                }
                for (pack in packs) {
                    pack.getStream(location)?.also {
                        return@read it
                    }
                }
                return@read null
            }
        }

        public fun getAllResourceLocations(
            namespaceIn: String,
            pathIn: String,
            maxDepth: Int,
            filter: Predicate<String>
        ): Collection<ResourceLocation> {
            return read {
                val locations = mutableSetOf<ResourceLocation>()

                locations.addAll(files.keys)
                locations.addAll(generators.keys)
                packs.forEach {
                    locations.addAll(it.listResources(pathIn, maxDepth))
                }

                locations.filter { location ->
                    when {
                        !location.path.startsWith(pathIn) -> false
                        location.path.removePrefix(pathIn).count { it == '/' } > maxDepth -> false
                        location.path.endsWith(".mcmeta") -> false
                        else -> filter.test(location.path.split('/').last())
                    }
                }.sortedBy { it.path }
            }
        }

        public fun resourceExists(location: ResourceLocation): Boolean {
            return read {
                location in files || location in generators || packs.any { location in it }
            }
        }
    }

    public companion object {
        private val logger = LibrarianLibMirageModule.makeLogger<VirtualResourceManager>()
    }
}