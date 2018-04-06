package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.BoundingBox2D
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import java.awt.Color

class ComponentText @JvmOverloads constructor(posX: Int, posY: Int, var horizontal: ComponentText.TextAlignH = ComponentText.TextAlignH.LEFT, var vertical: ComponentText.TextAlignV = ComponentText.TextAlignV.TOP) : GuiComponent(posX, posY) {

    /**
     * The text to draw
     */
    val text = Option<ComponentText, String>("-NULL TEXT-")
    /**
     * The color of the text
     */
    val color = Option<ComponentText, Color>(Color.BLACK)
    /**
     * The wrap width in pixels, -1 for no wrapping
     */
    val wrap = Option<ComponentText, Int>(-1)
    /**
     * Whether to set the font renderer's unicode and bidi flags
     */
    val unicode = Option<ComponentText, Boolean>(false)
    /**
     * Whether to set the bidirectional flag to true when unicode is enabled
     */
    val enableUnicodeBidi = Option<ComponentText, Boolean>(true)
    /**
     * Whether to render a shadow behind the text
     */
    val shadow = Option<ComponentText, Boolean>(false)

    /**
     * Set the text value and unset the function
     */
    fun `val`(str: String): ComponentText {
        text.setValue(str)
        text.noFunc()
        return this
    }

    /**
     * Set the callback to create the text for

     * @param func
     * *
     * @return
     */
    fun func(func: (ComponentText) -> String): ComponentText {
        text.func(func)
        return this
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val fr = Minecraft.getMinecraft().fontRenderer

        val fullText = text.getValue(this)
        val colorHex = color.getValue(this).rgb
        val enableFlags = unicode.getValue(this)
        val dropShadow = shadow.getValue(this)

        if (enableFlags) {
            if(enableUnicodeBidi.getValue(this))
                fr.bidiFlag = true
            fr.unicodeFlag = true
        }

        val x = 0
        var y = 0

        val lines: List<String>

        val wrap = this.wrap.getValue(this)
        if (wrap == -1) {
            lines = listOf(fullText)
        } else {
            lines = fr.listFormattedStringToWidth(fullText, wrap)
        }


        val height = lines.size * fr.FONT_HEIGHT
        if (vertical == TextAlignV.MIDDLE) {
            y -= height / 2
        } else if (vertical == TextAlignV.BOTTOM) {
            y -= height
        }

        for ((i, line) in lines.withIndex()) {
            var lineX = x
            val lineY = y + i * fr.FONT_HEIGHT

            val textWidth = fr.getStringWidth(line)
            if (horizontal == TextAlignH.CENTER) {
                lineX -= textWidth / 2
            } else if (horizontal == TextAlignH.RIGHT) {
                lineX -= textWidth
            }

            fr.drawString(line, lineX.toFloat(), lineY.toFloat(), colorHex, dropShadow)
        }

        if (enableFlags) {
            if(enableUnicodeBidi.getValue(this))
                fr.bidiFlag = false
            fr.unicodeFlag = false
        }
    }

    fun sizeToText() {
        this.size = contentSize.size
    }

    val contentSize: BoundingBox2D
        get() {
            val wrap = this.wrap.getValue(this)

            val size: Vec2d

            val fr = Minecraft.getMinecraft().fontRenderer

            val enableFlags = unicode.getValue(this)

            if (enableFlags) {
                if(enableUnicodeBidi.getValue(this))
                    fr.bidiFlag = true
                fr.unicodeFlag = true
            }

            if (wrap == -1) {
                size = vec(fr.getStringWidth(text.getValue(this)), fr.FONT_HEIGHT)
            } else {
                val wrapped = fr.listFormattedStringToWidth(text.getValue(this), wrap)
                size = vec(wrap, wrapped.size * fr.FONT_HEIGHT)
            }

            if (enableFlags) {
                if(enableUnicodeBidi.getValue(this))
                    fr.bidiFlag = false
                fr.unicodeFlag = false
            }

            return BoundingBox2D(Vec2d.ZERO, size)
        }

    enum class TextAlignH {
        LEFT, CENTER, RIGHT
    }

    enum class TextAlignV {
        TOP, MIDDLE, BOTTOM
    }

}
