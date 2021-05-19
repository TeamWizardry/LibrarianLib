package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.value.IMValueInt
import com.teamwizardry.librarianlib.mosaic.Sprite
import java.awt.Color

/**
 * Displays a sprite
 */
public class SpriteLayer(public var sprite: Sprite?, x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    public constructor(sprite: Sprite?, x: Int, y: Int): this(sprite, x, y, sprite?.width ?: 16, sprite?.height ?: 16)
    public constructor(sprite: Sprite?): this(sprite, 0, 0)
    public constructor(): this(null, 0, 0)

    public var tint_im: IMValue<Color> = imValue(Color.WHITE)
    public var tint: Color by tint_im
    public var animationFrame_im: IMValueInt = imInt(0)
    public var animationFrame: Int by animationFrame_im

    override fun draw(context: GuiDrawContext) {
        val sp = sprite ?: return

        sp.draw(context.transform, 0f, 0f, size.xi.toFloat(), size.yi.toFloat(), animationFrame, tint)
    }
}
