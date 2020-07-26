package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.facade.value.RMValue
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.math.vec
import org.lwjgl.opengl.GL11
import java.awt.Color

class PastryDropShadowLayer(
    x: Int, y: Int, width: Int, height: Int,
    radius: Int, color: Color = Color(0f, 0f, 0f, 0.5f)
): GuiLayer(x, y, width, height) {
    private val spriteLayer = SpriteLayer(PastryTexture.shadowSprite, 0, 0, 0, 0)

    var color_rm: RMValue<Color> = rmValue(color) { _, _ ->
    }
    var color: Color by color_rm

    var radius_rm: RMValueInt = rmInt(radius) { _, _ ->
        this.markLayoutDirty()
    }
    var radius: Int by radius_rm

    init {
        spriteLayer.tint_im.set { color }
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