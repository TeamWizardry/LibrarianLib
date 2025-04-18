package com.teamwizardry.librarianlib.testcore.resources

import net.minecraft.resource.InputSupplier
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackInfo
import net.minecraft.resource.ResourcePackSource
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.PathUtil
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.Optional

internal object RuntimeResources : ResourcePack {
    val translations = mutableMapOf<String, String>()
    val namespaces = ResourceType.entries.associateWith { mutableSetOf<String>() }
    val resources = sortedMapOf<Path, String>()

    fun addTranslation(key: String, text: String) {
        translations[key] = text
    }

    fun addAsset(idAndContent: Pair<Identifier, String>) {
        addAsset(idAndContent.first, idAndContent.second)
    }

    fun addAsset(id: Identifier, content: String) {
        namespaces.getValue(ResourceType.CLIENT_RESOURCES).add(id.namespace)
        resources[Path.of("/assets", id.namespace, id.path)] = content
    }

    private fun String.toInputSupplier(): InputSupplier<InputStream> = InputSupplier {
        IOUtils.toInputStream(this, Charset.forName("UTF-8"))
    }

    override fun openRoot(vararg segments: String): InputSupplier<InputStream>? {
        val fullPath = Path.of("/", *segments)
        return resources[fullPath]?.toInputSupplier()
    }

    override fun open(type: ResourceType, id: Identifier): InputSupplier<InputStream>? {
        val fullPath = Path.of("/", type.directory, id.namespace, id.path)
        return resources[fullPath]?.toInputSupplier()
    }

    override fun findResources(
        type: ResourceType,
        namespace: String,
        prefix: String,
        consumer: ResourcePack.ResultConsumer
    ) {
        val namespaceRoot = Path.of("/", type.directory, namespace)
        val searchPrefix = namespaceRoot.resolve(prefix)
        resources.forEach { (assetPath, value) ->
            if (assetPath.startsWith(searchPrefix)) {
                val id = Identifier.tryParse(namespace, namespaceRoot.relativize(assetPath).toString())
                consumer.accept(id, value.toInputSupplier())
            }
        }
    }

    override fun getNamespaces(type: ResourceType): Set<String> {
        return namespaces.getValue(type)
    }

    override fun <T : Any> parseMetadata(metaReader: ResourceMetadataReader<T>): T? {
        return null
    }

    override fun getInfo(): ResourcePackInfo {
        return ResourcePackInfo(
            "testcore_generated", // id
            Text.literal("TestCore generated assets"), // title
            ResourcePackSource.BUILTIN,
            Optional.empty()
        )
    }

    override fun close() {
    }
}