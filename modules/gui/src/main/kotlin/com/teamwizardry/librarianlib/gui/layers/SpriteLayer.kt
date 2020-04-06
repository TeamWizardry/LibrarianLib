package com.teamwizardry.librarianlib.gui.layers

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.IMValueInt
import com.teamwizardry.librarianlib.sprites.ISprite
import java.awt.Color

/**
 * Displays a sprite
 */
class SpriteLayer(var sprite: ISprite?, x: Int, y: Int, width: Int, height: Int) : GuiLayer(x, y, width, height) {
    constructor(sprite: ISprite?, x: Int, y: Int): this(sprite, x, y, sprite?.width ?: 16, sprite?.height ?: 16)
    constructor(sprite: ISprite?): this(sprite, 0, 0)
    constructor(): this(null, 0, 0)

    var tint_im: IMValue<Color> = IMValue(Color.WHITE)
    var tint: Color by tint_im
    var animationFrame_im: IMValueInt = IMValueInt(0)
    var animationFrame: Int by animationFrame_im

    override fun draw(context: GuiDrawContext) {
        val sp = sprite ?: return

        val tint = tint
        GlStateManager.color4f(tint.red/255f, tint.green/255f, tint.blue/255f, tint.alpha/255f)

        sp.bind()
        sp.draw(animationFrame % sp.frameCount, 0f, 0f, size.xi.toFloat(), size.yi.toFloat())
    }

    override fun debugInfo(): MutableList<String> {
        val list = super.debugInfo()
        list.add("sprite = $sprite")
        list.add("tint = $tint, frame = $animationFrame")
        return list
    }
}
