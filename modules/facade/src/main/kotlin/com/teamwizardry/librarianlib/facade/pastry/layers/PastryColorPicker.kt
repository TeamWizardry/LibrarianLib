package com.teamwizardry.librarianlib.facade.pastry.layers

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.*
import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

public class PastryColorPicker : GuiLayer(0, 0, 80, 50) {
    private val gradient = GradientLayer()
    private val hueLayer = HueLayer()
    private val colorWell = ColorWellLayer()

    private var _hue: Float = 0f
    public var hue: Float
        get() = _hue
        set(value) {
            _hue = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent(color))
        }

    private var _saturation: Float = 0f
    public var saturation: Float
        get() = _saturation
        set(value) {
            _saturation = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent(color))
        }

    private var _brightness: Float = 0f
    public var brightness: Float
        get() = _brightness
        set(value) {
            _brightness = value
            _color = Color(Color.HSBtoRGB(hue, saturation, brightness))
            BUS.fire(ColorChangeEvent(color))
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

    public class ColorChangeEvent(public val color: Color) : Event()

    init {
        this.add(gradient, hueLayer, colorWell)
    }

    override fun layoutChildren() {
        colorWell.size = vec(16, 16)
        colorWell.pos = vec(this.width - colorWell.width, 0)
        hueLayer.size = vec(10, this.height)
        hueLayer.pos = vec(colorWell.x - hueLayer.width - 2, 0)
        gradient.pos = vec(0, 0)
        gradient.size = vec(max(4.0, hueLayer.x - 2), this.height)
    }

    private inner class GradientLayer : GuiLayer(0, 0, 0, 0) {
        val background = PastryBackground(PastryBackgroundStyle.LIGHT_INSET, 0, 0, 0, 0)
        val square = ColorSquare()
        var dragging = false

        init {
            add(background, square)
            square.BUS.hook<GuiLayerEvents.MouseDown> {
                if (square.mouseOver && it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    dragging = true
                    updateSB()
                }
            }
            square.BUS.hook<GuiLayerEvents.MouseUp> {
                if(it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    dragging = false
                }
            }
            square.BUS.hook<GuiLayerEvents.MouseMove> {
                if (dragging) {
                    updateSB()
                }
            }
        }

        private fun updateSB() {
            if (square.width == 0.0 || square.height == 0.0) return
            val fraction = square.mousePos / square.size
            saturation = fraction.x.clamp(0.0, 1.0).toFloat()
            brightness = (1 - fraction.y).clamp(0.0, 1.0).toFloat()
        }

        override fun layoutChildren() {
            background.frame = bounds
            square.frame = bounds
            square.pos += vec(1, 1)
            square.size -= vec(2, 2)
        }

        inner class ColorSquare : GuiLayer(0, 0, 0, 0) {
            override fun draw(context: GuiDrawContext) {
                super.draw(context)

                val minX = 0.0
                val minY = 0.0
                val maxX = size.xi.toDouble()
                val maxY = size.yi.toDouble()

                val buffer = VertexConsumerProvider.getImpl(Client.tessellator.buffer)

                ColorPickerShader.hue.set(hue)

                val vb = buffer.getBuffer(colorPickerRenderType)
                // u/v is saturation/brightness
                vb.pos2d(context.transform, minX, minY).tex(0, 1).endVertex()
                vb.pos2d(context.transform, minX, maxY).tex(0, 0).endVertex()
                vb.pos2d(context.transform, maxX, maxY).tex(1, 0).endVertex()
                vb.pos2d(context.transform, maxX, minY).tex(1, 1).endVertex()
                buffer.finish()
            }
        }
    }

    private inner class HueLayer : GuiLayer(0, 0, 0, 0) {
        private val background = PastryBackground(PastryBackgroundStyle.LIGHT_INSET, 0, 0, 0, 0)
        private val sprite = SpriteLayer(hueSprite)
        private var dragging = false

        init {
            Client.minecraft.textureManager.bindTexture(hueLoc)
            Client.minecraft.textureManager.getTexture(hueLoc)?.setBlurMipmap(false, false)

            add(background, sprite)

            sprite.BUS.hook<GuiLayerEvents.MouseDown> {
                if(sprite.mouseOver && it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    dragging = true
                    updateH()
                }
            }
            sprite.BUS.hook<GuiLayerEvents.MouseUp> {
                if(it.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    dragging = false
                }
            }
            sprite.BUS.hook<GuiLayerEvents.MouseMove> {
                if(dragging) {
                    updateH()
                }
            }
        }

        fun updateH() {
            if (sprite.height == 0.0) return
            val fraction = sprite.mousePos.y / sprite.height
            hue = (1 - fraction).clamp(0.0, 1.0).toFloat()
        }

        override fun layoutChildren() {
            background.frame = bounds
            sprite.frame = bounds
            sprite.pos += vec(1, 1)
            sprite.size -= vec(2, 2)
        }
    }

    private inner class ColorWellLayer : GuiLayer(0, 0, 0, 0) {
        private val background = PastryBackground(PastryBackgroundStyle.LIGHT_INSET, 0, 0, 0, 0)
        val colorRect = RectLayer(Color.white, 0, 0, 0, 0)

        init {
            add(background, colorRect)
            colorRect.color_im.set { color }
        }

        override fun layoutChildren() {
            background.frame = this.bounds
            colorRect.frame = this.bounds.shrink(1.0)
        }
    }

    /**
     * draws the saturation/brightness box
     */
    private object ColorPickerShader :
        Shader("color_picker", null, Identifier("librarianlib:facade/shaders/color_picker.frag")) {
        val hue = GLSL.glFloat()

        override fun setupState() {
            RenderSystem.enableBlend()
        }

        override fun teardownState() {
            RenderSystem.disableBlend()
        }
    }

    private companion object {
        val hueLoc = loc("librarianlib:facade/textures/pastry/colorpicker_hue.png")
        val hueSprite = Mosaic(hueLoc, 8, 256).getSprite("")

        private val colorPickerRenderType: RenderType = run {
            val renderState = RenderType.State.getBuilder()
                .build(false)

            mixinCast<IMutableRenderTypeState>(renderState).addState(ColorPickerShader.renderState)

            SimpleRenderTypes.makeType("librarianlib.facade.color_picker",
                DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, false, false, renderState
            )
        }
    }
}
