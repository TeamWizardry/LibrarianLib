package com.teamwizardry.librarianlib.gui.provided.pastry.layers

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.gui.value.RMValue
import com.teamwizardry.librarianlib.gui.value.RMValueInt
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

class PastryDropShadowLayer(
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