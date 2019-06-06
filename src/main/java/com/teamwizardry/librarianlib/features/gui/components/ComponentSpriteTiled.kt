package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.DrawingUtil
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@Deprecated("Use a ComponentSprite and specify caps in the mcmeta file")
open class ComponentSpriteTiled @JvmOverloads constructor(protected var main: Sprite, borderSize: Int, x: Int, y: Int, width: Int = main.width, height: Int = main.height) : GuiComponent(x, y, width, height) {

    var depth = Option<ComponentSpriteTiled, Boolean>(true)
    var color = Option<ComponentSpriteTiled, Color>(Color.WHITE)

    protected var borderSize = 3

    private val capped: ISprite = object: ISprite by main {
        override val minUCap: Float = borderSize / main.width.toFloat()
        override val minVCap: Float = borderSize / main.height.toFloat()
        override val maxUCap: Float = borderSize / main.width.toFloat()
        override val pinBottom: Boolean = false
        override val pinRight: Boolean = false
        override val maxVCap: Float = borderSize / main.height.toFloat()
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val alwaysTop = !depth.getValue(this)

        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_ALWAYS)

        color.getValue(this).glColor()
        main.tex.bind()
        draw(0f, 0f, size.xi, size.yi)

        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_LESS)
    }

    fun draw(x: Float, y: Float, width: Int, height: Int) {
        capped.draw(animator.time.toInt(), x, y, width.toFloat(), height.toFloat())
    }

}
