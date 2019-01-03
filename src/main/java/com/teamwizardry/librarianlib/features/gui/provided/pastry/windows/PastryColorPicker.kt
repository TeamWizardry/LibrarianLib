package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.layers.RectLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layout.Flexbox
import com.teamwizardry.librarianlib.features.gui.layout.flex
import com.teamwizardry.librarianlib.features.gui.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

class PastryColorPicker: PastryWindow(100, 75, PastryWindow.Style.PANEL, true) {
    private val flexbox = Flexbox(0, 0, 0, 0)
    private val gradient = GradientComponent()
    private val hueComponent = HueComponent()
    private val colorWell = ColorWellComponent()

    private var _hue: Float = 0f
    var hue: Float
        get() = _hue
        set(value) {
            _hue = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _saturation: Float = 0f
    var saturation: Float
        get() = _saturation
        set(value) {
            _saturation = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _brightness: Float = 0f
    var brightness: Float
        get() = _brightness
        set(value) {
            _brightness = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _color: Color = Color.white
    var color: Color
        get() = _color
        set(value) {
            _color = value
            val hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)
            _hue = hsb[0]
            _saturation = hsb[1]
            _brightness = hsb[2]
        }

    init {
        maxContentSize = vec(2000, 2000)
        minContentSize = vec(10, 10)

        content.add(flexbox)
        flexbox.add(gradient, hueComponent, colorWell)
        flexbox.spacing = 2
        gradient.flex.config(
            flexGrow = 3, minSize = 4
        )
        hueComponent.flex.config(
            flexBasis = 10, flexGrow = 0, flexShrink = 0
        )
        colorWell.flex.config(
            flexBasis = 32, flexGrow = 0, flexShrink = 0, alignSelf = Flexbox.Align.START
        )
    }

    override fun layoutChildren() {
        super.layoutChildren()
        flexbox.frame = content.bounds.shrink(2.0)
    }

    private inner class GradientComponent: GuiComponent(0, 0, 0, 0) {
        val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        val square = ColorSquare()

        init {
            add(background, square)
            square.BUS.hook<GuiComponentEvents.MouseDownEvent> {
                if(it.button == EnumMouseButton.LEFT) updateSB()
            }
            square.BUS.hook<GuiComponentEvents.MouseDragEvent> {
                if(EnumMouseButton.LEFT in square.pressedButtons) updateSB()
            }
        }

        private fun updateSB() {
            if(square.width == 0.0 || square.height == 0.0 || !square.mouseOver) return
            val fraction = square.mousePos / square.size
            if(fraction.x in 0.0 .. 1.0) saturation = fraction.x.toFloat()
            if(fraction.y in 0.0 .. 1.0) brightness = 1-fraction.y.toFloat()
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = bounds
            square.frame = bounds
            square.pos += vec(1, 1)
            square.size -= vec(2, 2)
        }

        inner class ColorSquare: GuiComponent(0, 0, 0, 0) {
            override fun draw(partialTicks: Float) {
                super.draw(partialTicks)

                val color = Color(Color.HSBtoRGB(hue, 1f, 1f))

                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer

                GlStateManager.disableTexture2D()

                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(0, 0).color(Color.WHITE).endVertex()
                vb.pos(0, height).color(Color.WHITE).endVertex()
                vb.pos(width, height).color(color).endVertex()
                vb.pos(width, 0).color(color).endVertex()
                tessellator.draw()

                GlStateManager.blendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO)

                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
                vb.pos(0, 0).color(Color.WHITE).endVertex()
                vb.pos(0, height).color(Color.BLACK).endVertex()
                vb.pos(width, height).color(Color.BLACK).endVertex()
                vb.pos(width, 0).color(Color.WHITE).endVertex()
                tessellator.draw()

                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

                GlStateManager.enableTexture2D()
            }
        }
    }

    private inner class HueComponent: GuiComponent(0, 0, 0, 0) {
        private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        private val sprite = SpriteLayer(hueSprite, 0, 0)

        init {
            Minecraft().textureManager.bindTexture(hueLoc)
            Minecraft().textureManager.getTexture(hueLoc).setBlurMipmap(false, false)

            add(background, sprite.componentWrapper())

            sprite.BUS.hook<GuiComponentEvents.MouseDownEvent> { updateH() }
            sprite.BUS.hook<GuiComponentEvents.MouseDragEvent> { updateH() }
        }

        fun updateH() {
            if(sprite.height == 0.0 || !sprite.componentWrapper().mouseOver) return
            val fraction = sprite.componentWrapper().mousePos.y / sprite.height
            if(fraction in 0.0 .. 1.0) hue = 1-fraction.toFloat()
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = bounds
            sprite.frame = bounds
            sprite.pos += vec(1, 1)
            sprite.size -= vec(2, 2)
        }
    }

    private inner class ColorWellComponent: GuiComponent(0, 0, 0, 16) {
        private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        val colorRect = RectLayer(Color.white, 0, 0, 0, 0)

        init {
            add(background, colorRect)
            colorRect.color_im { color }
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = this.bounds
            colorRect.frame = this.bounds.shrink(1.0)
        }
    }

    companion object {
        val hueLoc = "librarianlib:textures/gui/pastry/colorpicker_hue.png".toRl()
        val hueSprite = Sprite(hueLoc)
    }

    class ColorChangeEvent(): Event()
}