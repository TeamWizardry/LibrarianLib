package com.teamwizardry.librarianlib.virtualresources

import com.teamwizardry.librarianlib.core.util.kotlin.synchronized
import net.minecraft.resources.FallbackResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourcePack
import net.minecraft.resources.ResourcePack
import net.minecraft.resources.ResourcePackType
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.function.Predicate

class VirtualResources internal constructor(val type: ResourcePackType) {
    internal val fallback = FallbackResourceManager(type)
    internal val files = mutableMapOf<ResourceLocation, ByteArray>().synchronized()
    internal val generators = mutableMapOf<ResourceLocation, () -> ByteArray>().synchronized()
    internal val packs = mutableListOf<VirtualResourcePack>().synchronized()

    fun remove(location: ResourceLocation): Boolean {
        var removed = false
        removed = removeFile(location) || removed // can't do removeFile || removeGenerator because of short-circuiting
        removed = removeGenerator(location) || removed
        return removed
    }


    fun removeFile(location: ResourceLocation): Boolean {
        if(files.remove(location) != null) {
            logger.debug("Removed file $location")
            return true
        }
        return false
    }

    fun removeGenerator(location: ResourceLocation): Boolean {
        if(generators.remove(location) != null) {
            logger.debug("Removed generator $location")
            return true
        }
        return false
    }

    fun add(location: ResourceLocation, text: String) {
        addRaw(location, text.toByteArray())
    }

    fun addRaw(location: ResourceLocation, data: ByteArray) {
        files[location] = data
        logger.debug("Added resource $location")
    }

    /**
     * Note: the passed generator may be run on another thread
     */
    fun add(location: ResourceLocation, generator: () -> String) {
        addRaw(location) {
            generator().toByteArray()
        }
    }

    /**
     * Note: the passed generator may be run on another thread
     */
    fun addRaw(location: ResourceLocation, generator: () -> ByteArray) {
        generators[location] = generator
        logger.debug("Added resource generator $location")
    }

    companion object {
        @OnlyIn(Dist.CLIENT)
        @JvmField
        val client = VirtualResources(ResourcePackType.CLIENT_RESOURCES)
        @JvmField
        val data = VirtualResources(ResourcePackType.SERVER_DATA)

        @JvmStatic
        fun resources(type: ResourcePackType): VirtualResources = when(type) {
            ResourcePackType.CLIENT_RESOURCES -> client
            ResourcePackType.SERVER_DATA -> data
        }

        @JvmStatic
        fun `inject-asm`(pack: FallbackResourceManager) {
            pack.resourcePacks.add(Pack)
        }

        @JvmStatic
        fun `fallbackManager-asm`(manager: IResourceManager?, type: ResourcePackType): IResourceManager? {
            if(manager != null) return manager
            return resources(type).fallback
        }
    }

    private object Pack: IResourcePack {

        override fun getResourceStream(type: ResourcePackType, location: ResourceLocation): InputStream {
            val resources = resources(type)
            resources.files[location]?.also {
                return ByteArrayInputStream(it)
            }
            resources.generators[location]?.also {
                return ByteArrayInputStream(it())
            }
            resources.packs.forEach { pack ->
                pack.getStream(location)?.also {
                    return it
                }
            }
            throw FileNotFoundException("Virtual resource $location not found")
        }

        override fun getAllResourceLocations(type: ResourcePackType, pathIn: String, maxDepth: Int, filter: Predicate<String>): Collection<ResourceLocation> {
            val resources = resources(type)

            val locations = mutableSetOf<ResourceLocation>()

            locations.addAll(resources.files.keys)
            locations.addAll(resources.generators.keys)
            resources.packs.forEach {
                locations.addAll(it.listResources(pathIn, maxDepth))
            }

            return locations.filter {
                when {
                    !it.path.startsWith(pathIn) -> false
                    it.path.removePrefix(pathIn).count { it == '/' } > maxDepth -> false
                    it.path.endsWith(".mcmeta") -> false
                    else -> filter.test(it.path.split('/').last())
                }
            }.sortedBy { it.path }
        }

        override fun resourceExists(type: ResourcePackType, location: ResourceLocation): Boolean {
            val resources = resources(type)
            return location in resources.files || location in resources.generators || resources.packs.any { location in it }
        }

        override fun getName(): String = "LibrarianLib Virtual Resources"

        // The LibrarianLib virtual resource pack is added using ASM, so this is never normally called
        override fun getResourceNamespaces(type: ResourcePackType): Set<String> = setOf()

        override fun getRootResourceStream(fileName: String): InputStream {
            return javaClass.getResourceAsStream("/assets/librarianlib-virtualresources/root_resources/$fileName")
        }

        override fun <T: Any?> getMetadata(deserializer: IMetadataSectionSerializer<T>): T? {
            return ResourcePack.getResourceMetadata(deserializer, getRootResourceStream("pack.mcmeta"))
        }

        override fun close() {
            // nop
        }
    }
}