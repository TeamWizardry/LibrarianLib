package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.features.gui.value.RMValueInt
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.fastCos
import com.teamwizardry.librarianlib.features.kotlin.fastSin
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.step
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI

class DropShadowLayer(
    x: Int, y: Int, width: Int, height: Int,
    radius: Int, color: Color = Color(0f, 0f, 0f, 0.5f)
): GuiLayer(x, y, width, height) {
    private val spriteLayer = SpriteLayer(PastryTexture.shadowSprite, 0, 0, 0, 0)

    var color_rm: RMValue<Color> = RMValue(color) { _, _ ->
        this.setNeedsLayout()
    }
    var color: Color by color_rm

    var radius_rm: RMValueInt = RMValueInt(radius) { _, _ ->
        this.setNeedsLayout()
    }
    var radius: Int by radius_rm

    init {
        spriteLayer.BUS.hook<GuiLayerEvents.PreDrawEvent> {
            GlStateManager.alphaFunc(GL11.GL_GREATER, 1 / 256f)
        }
        spriteLayer.tint_im { color }
        add(spriteLayer)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        val borderSize = radius * 2 // the blur really starts to drop off about halfway
        spriteLayer.pos = vec(-borderSize, -borderSize)
        spriteLayer.scale = borderSize.toDouble() / PastryTexture.shadowFadeSize
        spriteLayer.size = (this.size + vec(borderSize, borderSize) * 2) / spriteLayer.scale
    }
}