package com.teamwizardry.librarianlib.features.neogui.provided.book.structure

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.util.ResourceLocation

object StructureCacheRegistry {

    private val structures = mutableMapOf<ResourceLocation, RenderableStructure?>()

    private var toLoad = mutableListOf<String>()
    private var hasPassedInit = false

    fun passInit() {
        if (hasPassedInit) return

        hasPassedInit = true

        for (structure in toLoad)
            addStructure(structure)

        toLoad.clear()

        ClientRunnable.registerReloadHandler {
            for (key in structures.keys)
                addStructure(key.toString())
        }
    }

    fun addStructure(name: String): RenderableStructure? {
        val structure = RenderableStructure(ResourceLocation(name), null)
        if (structure.blockInfos().isEmpty()) return null
        structures[structure.name] = structure

        return structure
    }

    fun getStructureOrAdd(name: String): RenderableStructure? {
        val trueName = ResourceLocation(name)
        return structures.getOrPut(trueName) { addStructure(name) }
    }

    fun getPromise(name: String): () -> RenderableStructure? {
        if (hasPassedInit) {
            val structure = getStructureOrAdd(name)
            return { structure }
        }
        toLoad.add(name)
        return { getStructureOrAdd(name) }
    }
}
