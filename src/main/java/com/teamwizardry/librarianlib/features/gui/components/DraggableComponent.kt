package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.Hook
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

open class DraggableComponent(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    private var mightDrag = false
    private var isDragging = false

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        mightDrag = mouseOver
    }

    @Hook
    private fun mouseUp(e: GuiComponentEvents.MouseUpEvent) {
        mightDrag = false
    }

    @Hook
    private fun mouseMove(e: GuiComponentEvents.MouseMoveEvent) {

    }

    override fun draw(partialTicks: Float) {
        // nop
    }
}
