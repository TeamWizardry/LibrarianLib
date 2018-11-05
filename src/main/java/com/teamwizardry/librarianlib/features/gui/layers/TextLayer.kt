package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import java.awt.Color
import net.minecraft.client.gui.FontRenderer.getFormatFromString
import net.minecraft.client.renderer.GlStateManager

class TextLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    var fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRenderer

    /**
     * The text to draw
     */
    val text_im: IMValue<String> = IMValue("")
    /**
     * The color of the text
     */
    val color_im: IMValue<Color> = IMValue(Color.BLACK)

    // options that change the layout
    var text: String by text_im
    var wrap: Boolean = true
    var unicode: Boolean = false
    var enableUnicodeBidi: Boolean = true
    var align: Align2d = Align2d.LEFT_TOP

    // visual options
    var color: Color by color_im
    var shadow: Boolean = false
    var fitToText: Boolean = false

    private val layout = TextLayout()

    override fun draw(partialTicks: Float) {
        updateLayout()

        if(fitToText) {
            if(wrap) {
                this.size = vec(size.x, layout.bounds.height)
            } else {
                this.size = layout.bounds.size
            }
        }

        GlStateManager.pushMatrix()

        when(layout.align.x) {
            Align2d.X.LEFT -> {}
            Align2d.X.CENTER -> GlStateManager.translate(size.x/2, 0.0, 0.0)
            Align2d.X.RIGHT -> GlStateManager.translate(size.x, 0.0, 0.0)
        }
        when(layout.align.y) {
            Align2d.Y.TOP -> {}
            Align2d.Y.CENTER -> GlStateManager.translate(0.0, (size.y - layout.bounds.height)/2, 0.0)
            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, size.y - layout.bounds.height, 0.0)
        }

        layout.render(color.rgb, shadow)

        GlStateManager.popMatrix()
    }

    private fun updateLayout() {
        layout.fontRenderer = fontRenderer
        layout.text = text
        layout.wrapWidth = if(wrap) size.xi else Int.MAX_VALUE
        layout.lineSpacing = 0
        layout.align = align
        layout.unicode = unicode
        layout.enableUnicodeBidi = enableUnicodeBidi
        layout.genIfNeeded()
    }

}