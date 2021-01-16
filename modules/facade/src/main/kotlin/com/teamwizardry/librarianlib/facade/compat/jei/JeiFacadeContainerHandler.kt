package com.teamwizardry.librarianlib.facade.compat.jei

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.layers.JeiIngredientLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.ScreenSpace
import mezz.jei.api.gui.handlers.IGuiClickableArea
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import net.minecraft.client.renderer.Rectangle2d

internal object JeiFacadeContainerHandler: IGuiContainerHandler<FacadeContainerScreen<*>> {
    override fun getGuiExtraAreas(containerScreen: FacadeContainerScreen<*>): List<Rectangle2d> {
        return containerScreen.jei.exclusionAreas.map {
            val frame = it.convertRectTo(it.bounds, ScreenSpace)
            Rectangle2d(frame.xi, frame.yi, frame.widthi, frame.heighti)
        }
    }

    override fun getIngredientUnderMouse(
        containerScreen: FacadeContainerScreen<*>,
        mouseX: Double,
        mouseY: Double
    ): Any? {
        val hit = containerScreen.facade.hitTest(mouseX, mouseY)
        if(containerScreen.isMouseMasked(mouseX, mouseY) && !hit.isOverVanilla)
            return null
        val layer = generateSequence(hit.layer) { it.parent }.find { it is JeiIngredientLayer } ?: return null
        val pos = layer.convertPointFrom(vec(mouseX, mouseY), ScreenSpace)
        return (layer as? JeiIngredientLayer)?.getJeiIngredient(pos)
    }

    override fun getGuiClickableAreas(
        containerScreen: FacadeContainerScreen<*>,
        mouseX: Double,
        mouseY: Double
    ): MutableCollection<IGuiClickableArea> {
        return super.getGuiClickableAreas(containerScreen, mouseX, mouseY)
    }
}