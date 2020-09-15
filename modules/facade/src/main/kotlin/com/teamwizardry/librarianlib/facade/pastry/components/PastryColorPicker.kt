package com.teamwizardry.librarianlib.facade.pastry.components

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.client.renderer.IRenderTypeBuffer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color

public class PastryColorPicker: GuiLayer() {
    private val gradient = GradientComponent()
    private val hueComponent = HueComponent()
    private val colorWell = ColorWellComponent()

    private var _hue: Float = 0f
    public var hue: Float
        get() = _hue
        set(value) {
            _hue = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _saturation: Float = 0f
    public var saturation: Float
        get() = _saturation
        set(value) {
            _saturation = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _brightness: Float = 0f
    public var brightness: Float
        get() = _brightness
        set(value) {
            _brightness = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent())
        }

    private var _color: Color = Color.white
    public var color: Color
        get() = _color
        set(value) {
            _color = value
            val hsb = Color.RGBtoHSB(color.red, color.green, color.blue, null)
            _hue = hsb[0]
            _saturation = hsb[1]
            _brightness = hsb[2]
        }

    public class ColorChangeEvent(): Event()

    init {
        this.yoga()
        gradient.yoga()
            .flexGrow(3)
            .minWidth.px(4)
            .marginRight.px(2)
        hueComponent.yoga()
            .flex(0, 0)
            .flexBasis.px(10)
            .marginRight.px(2)
        colorWell.yoga()
            .flex(0, 0)
            .flexBasis.px(32)
            .alignSelf.start()
        this.add(gradient, hueComponent, colorWell)
    }

    private inner class GradientComponent: GuiLayer(0, 0, 0, 0) {
        val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        val square = ColorSquare()
        var draggingFromInside = false

        init {
            add(background, square)
            square.BUS.hook<GuiLayerEvents.MouseDown> {
                if (square.mouseOver && it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    draggingFromInside = true
                    updateSB()
                }
            }
            square.BUS.hook<GuiLayerEvents.MouseDown> {
                if (it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    draggingFromInside = false
                }
            }
            square.BUS.hook<GuiLayerEvents.MouseDrag> {
                if (draggingFromInside && it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    updateSB()
                }
            }
        }

        private fun updateSB() {
            if (square.width == 0.0 || square.height == 0.0 || !square.mouseOver) return
            val fraction = square.mousePos / square.size
            if (fraction.x in 0.0..1.0) saturation = fraction.x.toFloat()
            if (fraction.y in 0.0..1.0) brightness = 1 - fraction.y.toFloat()
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = bounds
            square.frame = bounds
            square.pos += vec(1, 1)
            square.size -= vec(2, 2)
        }

        inner class ColorSquare: GuiLayer(0, 0, 0, 0) {
            override fun draw(context: GuiDrawContext) {
                super.draw(context)

                val color = Color(Color.HSBtoRGB(hue, 1f, 1f))

                val minX = 0.0
                val minY = 0.0
                val maxX = size.xi.toDouble()
                val maxY = size.yi.toDouble()

                val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
                val vb = buffer.getBuffer(flatRenderType)

                vb.pos2d(context.matrix, minX, minY).color(Color.WHITE).endVertex()
                vb.pos2d(context.matrix, minX, maxY).color(Color.WHITE).endVertex()
                vb.pos2d(context.matrix, maxX, maxY).color(color).endVertex()
                vb.pos2d(context.matrix, maxX, minY).color(color).endVertex()
                buffer.finish()

                RenderSystem.blendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO)
                vb.pos2d(context.matrix, minX, minY).color(Color.WHITE).endVertex()
                vb.pos2d(context.matrix, minX, maxY).color(Color.BLACK).endVertex()
                vb.pos2d(context.matrix, maxX, maxY).color(Color.BLACK).endVertex()
                vb.pos2d(context.matrix, maxX, minY).color(Color.WHITE).endVertex()
                buffer.finish()

                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            }
        }
    }

    private inner class HueComponent: GuiLayer(0, 0, 0, 0) {
        private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        private val sprite = SpriteLayer(hueSprite)

        init {
            Client.minecraft.textureManager.bindTexture(hueLoc)
            Client.minecraft.textureManager.getTexture(hueLoc)?.setBlurMipmap(false, false)

            add(background, sprite)

            sprite.BUS.hook<GuiLayerEvents.MouseDown> { updateH() }
            sprite.BUS.hook<GuiLayerEvents.MouseDrag> { updateH() }
        }

        fun updateH() {
            if (sprite.height == 0.0 || !sprite.mouseOver) return
            val fraction = sprite.mousePos.y / sprite.height
            if (fraction in 0.0..1.0) hue = 1 - fraction.toFloat()
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = bounds
            sprite.frame = bounds
            sprite.pos += vec(1, 1)
            sprite.size -= vec(2, 2)
        }
    }

    private inner class ColorWellComponent: GuiLayer(0, 0, 0, 16) {
        private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 0, 0)
        val colorRect = RectLayer(Color.white, 0, 0, 0, 0)

        init {
            add(background, colorRect)
            colorRect.color_im.set { color }
        }

        override fun layoutChildren() {
            super.layoutChildren()
            background.frame = this.bounds
            colorRect.frame = this.bounds.shrink(1.0)
        }
    }

    private companion object {
        val hueLoc = loc("librarianlib:facade/textures/pastry/colorpicker_hue.png")
        val hueSprite = Mosaic(hueLoc, 8, 256).getSprite("")
        val flatRenderType = SimpleRenderTypes.flat(GL11.GL_QUADS)
    }
}
