package com.teamwizardry.librarianlib.mirage.bridge

import com.teamwizardry.librarianlib.mirage.Mirage
import net.minecraft.resources.*
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.Identifier
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.function.Predicate

public object MirageMixinBridge {
    /**
     * A resource pack that will be added as a last resort to all [FallbackResourceManager]s
     */
    public val resourcePack: IResourcePack = MirageResourcePack()

    private val clientFallback: IResourceManager =
        FallbackResourceManager(ResourcePackType.CLIENT_RESOURCES, "librarianlib")
    private val dataFallback: IResourceManager =
        FallbackResourceManager(ResourcePackType.SERVER_DATA, "librarianlib")

    /**
     * If no "real" resources exist for a namespace, the [SimpleReloadableResourceManager] will just fail,
     * completely ignoring all the other [FallbackResourceManager]s that we've injected our resources into. To solve
     * this, we inject into the `SimpleReloadableResourceManager`'s failure conditions, falling back on our *own*
     * `FallbackResourceManager` first.
     */
    public fun fallbackManager(type: ResourcePackType): IResourceManager {
        return when(type) {
            ResourcePackType.CLIENT_RESOURCES -> clientFallback
            ResourcePackType.SERVER_DATA -> dataFallback
        }
    }
}

private class MirageResourcePack : IResourcePack {
    // the FallbackResourceManager type can be null, but we can treat the parameters as not nullable because we
    // don't add the resource pack when it's null.

    override fun getResourceStream(type: ResourcePackType, location: Identifier): InputStream {
        return Mirage.resourceManager(type).mixinBridge.getResourceStream(location)
            ?: throw FileNotFoundException("Virtual resource $location not found")
    }

    override fun resourceExists(type: ResourcePackType, location: Identifier): Boolean {
        return Mirage.resourceManager(type).mixinBridge.resourceExists(location)
    }

    override fun getAllIdentifiers(
        type: ResourcePackType,
        namespaceIn: String,
        pathIn: String,
        maxDepthIn: Int,
        filterIn: Predicate<String>
    ): Collection<Identifier> {
        return Mirage.resourceManager(type).mixinBridge.getAllIdentifiers(
            namespaceIn,
            pathIn,
            maxDepthIn,
            filterIn
        )
    }

    override fun getName(): String = "LibrarianLib Mirage Resources"

    // The LibrarianLib virtual resource pack is added using ASM, so this is never normally called
    override fun getResourceNamespaces(type: ResourcePackType): Set<String> = setOf()

    override fun getRootResourceStream(fileName: String): InputStream {
        return javaClass.getResourceAsStream("/assets/librarianlib/mirage/root_resources/$fileName")
    }

    override fun <T : Any?> getMetadata(deserializer: IMetadataSectionSerializer<T>): T? {
        return ResourcePack.getResourceMetadata(deserializer, getRootResourceStream("pack.mcmeta"))
    }

    override fun close() {
        // nop
    }
}
