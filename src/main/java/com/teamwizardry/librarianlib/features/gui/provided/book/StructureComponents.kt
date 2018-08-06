package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.saving.Dyn
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import com.teamwizardry.librarianlib.features.structure.dynamic.STRUCTURE_REGISTRY
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
class ComponentRenderableStructure(book: IBookGui, x: Int, y: Int, width: Int, height: Int, val structure: RenderableStructure?, subtext: TranslationHolder?) : ComponentStructurePage(book, x, y, width, height, subtext, structure) {

    private lateinit var bookmark: ComponentMaterialsBar

    override fun init(any: Any?) {
        bookmark = ComponentMaterialsBar(book, book.bookMarkIndex++, any as? RenderableStructure, null)
        add(bookmark)
    }

    override fun render(time: Int) {
        if (structure != null) {
            GlStateManager.translate(-structure.perfectCenter.x - 0.5, -structure.perfectCenter.y - 0.5, -structure.perfectCenter.z - 0.5)
            structure.draw()
        }
    }

    override fun preShift() {
        // NO-OP
    }

    override fun failed(): Boolean {
        return structure?.perfectCenter == null
    }
}

class ComponentDynamicStructure(book: IBookGui, x: Int, y: Int, width: Int, height: Int, val structure: ResourceLocation, subtext: TranslationHolder?) : ComponentStructurePage(book, x, y, width, height, subtext, STRUCTURE_REGISTRY.getObjectByName(structure)) {

    private val builtin = STRUCTURE_REGISTRY.getObjectByName(structure)

    private lateinit var bookmark: ComponentMaterialsBar

    override fun init(any: Any?) {
        bookmark = ComponentMaterialsBar(book, book.bookMarkIndex++, null, any as? DynamicStructure)
        add(bookmark)
    }

    override fun render(time: Int) {
        builtin?.renderOnPage(time)

    }

    override fun preShift() {
        if (builtin != null)
            GlStateManager.translate(0.0, -(builtin.ySize * 0.75), 0.0)
    }

    override fun failed(): Boolean {
        return builtin == null
    }
}
