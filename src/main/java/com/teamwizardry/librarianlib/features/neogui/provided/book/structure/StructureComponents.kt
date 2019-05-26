package com.teamwizardry.librarianlib.features.neogui.provided.book.structure

import com.teamwizardry.librarianlib.features.neogui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.neogui.provided.book.helper.TranslationHolder
import com.teamwizardry.librarianlib.features.structure.dynamic.STRUCTURE_REGISTRY
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

/**
 * Property of Demoniaque.
 * All rights reserved.
 */
class ComponentRenderableStructure(book: IBookGui, x: Int, y: Int, width: Int, height: Int, val structure: RenderableStructure?, subtext: TranslationHolder?) : ComponentStructurePage(book, x, y, width, height, subtext) {

    override fun render(time: Int) {
        if (structure != null) {
            GlStateManager.translate(-structure.perfectCenter.x - 0.5, -structure.perfectCenter.y - 0.5, -structure.perfectCenter.z - 0.5)
            GlStateManager.color(1f, 1f, 1f)
            structure.draw()
            GlStateManager.color(1f, 1f, 1f, 1f)
        }
    }

    override fun preShift() {
        // NO-OP
    }

    override fun failed(): Boolean {
        return structure?.perfectCenter == null
    }

    override fun copy(): ComponentStructurePage {
        val new = ComponentRenderableStructure(book, pos.xi, pos.yi, size.xi, size.yi, structure, subtext)
        new.rotVec = rotVec
        new.panVec = panVec
        new.prevPos = prevPos
        return new
    }
}

class ComponentDynamicStructure(book: IBookGui, x: Int, y: Int, width: Int, height: Int, val structure: ResourceLocation, subtext: TranslationHolder?) : ComponentStructurePage(book, x, y, width, height, subtext) {

    private val builtin = STRUCTURE_REGISTRY.getObjectByName(structure)

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

    override fun copy(): ComponentStructurePage {
        val new = ComponentDynamicStructure(book, pos.xi, pos.yi, size.xi, size.yi, structure, subtext)
        new.rotVec = rotVec
        new.panVec = panVec
        new.prevPos = prevPos
        return new
    }
}
