package com.teamwizardry.librarianlib.features.neogui.layers

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.neogui.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.text.TextLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

class TextLayerMC(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    var fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRenderer

    /**
     * The text to draw
     */
    val text_im: IMValue<String> = IMValue("")
    /**
     * The colorPrimary of the text
     */
    val color_im: IMValue<Color> = IMValue(Color.BLACK)

    // options that change the layout
    var text: String by text_im
    var wrap: Boolean = true
    var unicode: Boolean = false
    var enableUnicodeBidi: Boolean = true
    var align: Align2d = Align2d.TOP_LEFT
    var maxLines: Int = Int.MAX_VALUE
    var lineSpacing: Int = 0
    var truncate: Boolean = false

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
            Align2d.Y.CENTER -> GlStateManager.translate(0.0, ((size.y - layout.bounds.height)/2).toInt() + 1.0, 0.0)
            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, size.y - layout.bounds.height, 0.0)
        }

        layout.render(color.rgb, shadow)

        GlStateManager.popMatrix()
    }

    private fun updateLayout() {
        layout.fontRenderer = fontRenderer
        layout.text = text
        layout.wrapWidth = if(wrap) size.xi else Int.MAX_VALUE
        layout.lineSpacing = lineSpacing
        layout.align = align
        layout.unicode = unicode
        layout.enableUnicodeBidi = enableUnicodeBidi
        layout.maxLines = maxLines
        if(truncate) {
            layout.maxWidth = size.xi
            layout.truncationText = " ..."
        } else {
            layout.maxWidth = Int.MAX_VALUE
            layout.truncationText = null
        }
        layout.genIfNeeded()
    }

    fun fitToText() {
        updateLayout()

        if(wrap) {
            this.size = vec(size.x, layout.bounds.height)
        } else {
            this.size = layout.bounds.size
        }
    }

}