package com.teamwizardry.librarianlib.features.gui.provided.book.structure

import net.minecraft.util.ResourceLocation
import java.util.*

object StructureCacheRegistry {

    private val structures = HashSet<RenderableStructure>()

    fun addStructure(name: String): RenderableStructure? {
        val structure = RenderableStructure(ResourceLocation(name), null)
        if (structure.blockInfos().isEmpty()) return null
        structures.add(structure)

        return structure
    }

    fun getStructureOrAdd(name: String): RenderableStructure? {
        val trueName = ResourceLocation(name)
        return structures.firstOrNull { it.name == trueName }
                ?: addStructure(name)
    }
}
