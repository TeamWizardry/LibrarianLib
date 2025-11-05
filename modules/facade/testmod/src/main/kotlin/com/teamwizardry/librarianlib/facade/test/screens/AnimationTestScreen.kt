package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.awt.Color

class AnimationTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 100)
        main.size = bg.size
        main.add(bg)

        val dirt = Mosaic(Identifier("minecraft:textures/block/dirt.png"), 16, 16).getSprite("")
        val stone = Mosaic(Identifier("minecraft:textures/block/stone.png"), 16, 16).getSprite("")
        val sandstone = Mosaic(Identifier("minecraft:textures/block/sandstone.png"), 16, 16).getSprite("")

        val simpleAnimation = SpriteLayer(dirt)

        val from = vec(32, 32)
        val to = vec(32, 64)
        val toFromCurrent = vec(64, 64)
        simpleAnimation.pos = from

        // - left click to animate from (32,32) -> (32,64), waiting 0.5s
        // - right click to animate from the current value to (64,64), waiting 0.5s. This should use the "current value"
        // 0.5s from now, not the current value at the time the layer was clicked
        simpleAnimation.BUS.hook<GuiLayerEvents.MouseDown> {
            if (simpleAnimation.mouseOver) {
                when (it.button) {
                    0 -> simpleAnimation.pos_rm.animate(from, to, 40f, Easing.linear)
                    1 -> simpleAnimation.pos_rm.animate(toFromCurrent, 40f, Easing.easeOutQuad, 10f)
                }
            }
        }

        main.add(simpleAnimation)

        val keyframeAnimation = SpriteLayer(dirt)

        keyframeAnimation.pos = vec(96, 32)

        keyframeAnimation.BUS.hook<GuiLayerEvents.MouseDown> {
            if (keyframeAnimation.mouseOver) {
                keyframeAnimation.pos_rm.animateKeyframes(vec(96, 32))
                    .add(10f, Easing.easeOutBounce, vec(128, 32)).onKeyframe(Runnable { keyframeAnimation.sprite = stone })
                    .hold(20f)
                    .jump(vec(96, 64)).onKeyframe(Runnable { keyframeAnimation.sprite = sandstone })
                    .add(10f, Easing.easeOutQuad, vec(96, 32))
                    .onComplete(Runnable { keyframeAnimation.sprite = dirt })
            }
        }

        main.add(keyframeAnimation)
    }
}