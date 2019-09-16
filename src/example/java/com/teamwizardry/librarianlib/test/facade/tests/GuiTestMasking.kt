package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.component.supporting.MaskMode
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.layers.MaskLayer
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.copy
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.PI
import kotlin.math.sqrt

/**
 * Created by TheCodeWarrior
 */
class GuiTestMasking : GuiBase() {
    init {
        main.size = vec(0, 100)

        val masked = createTestLayer(1.0f)
        masked.opacity = 0.5

        masked.add(createMask(MaskLayer()))
        masked.maskMode = MaskMode.LUMA_ON_BLACK

        masked.pos = vec(-110, 0)

        val mask = createMask(GuiLayer(10, 0, 100, 100))
        mask.clipToBounds = true

        main.add(masked, mask)
    }

    fun createMask(parent: GuiLayer): GuiLayer {
        val sprite = Sprite(ResourceLocation("librarianlibtest:textures/gui/spiral_mask.png"))
        sprite.tex.enableBlending()
        val mask = SpriteLayer(sprite, 50, 50)
        mask.size = vec(100 * sqrt(2.0), 100 * sqrt(2.0))
        mask.rotation_rm.animate(-PI * 2, 60f).repeat(-1)
        mask.anchor = vec(0.5, 0.5)
        parent.add(mask)
        return parent
    }

    fun createTestLayer(colorAlpha: Float): GuiLayer {
        val outer = GuiLayer(0, 0, 100, 100)
        val background = RectLayer(Color.red.copy(alpha = colorAlpha), 0, 0, 75, 75)
        val foreground = RectLayer(Color.blue.copy(alpha = colorAlpha), 25, 25, 75, 75)
        val clipped = RectLayer(Color.green.copy(alpha = colorAlpha), 10, 25, 30, 100)

        outer.add(background, foreground)
        foreground.add(clipped)
        foreground.clipToBounds = true

        foreground.cornerRadius_rm.animate(0.0, 20.0, 40f, Easing.easeInOutCubic).repeat(-1).reverseOnRepeat()
        clipped.pos_rm.animate(vec(10, 25), vec(10, -50), 30f, Easing.easeInOutCubic).repeat(-1).reverseOnRepeat()

        return outer
    }
}
