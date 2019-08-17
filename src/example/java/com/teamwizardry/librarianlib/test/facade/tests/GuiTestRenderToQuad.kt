package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.component.supporting.MaskMode
import com.teamwizardry.librarianlib.features.facade.component.supporting.RenderMode
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.layers.ArcLayer
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
class GuiTestRenderToQuad : GuiBase() {
    init {
        main.size = vec(100, 100)

        val background = RectLayer(Color.WHITE, 0, 0, 100, 100)
        val test = GuiLayer(0, 0, 20, 20)
        test.scale = 5.0
        test.renderMode = RenderMode.RENDER_TO_QUAD
        main.add(background, test)

//        val arc = ArcLayer(Color.BLACK, 10, 10, 6, 6)
        val arc = ArcLayer(Color.BLACK, 10, 10, 6, 6)

        arc.endAngle_im.animateKeyframes(0.0)
            .add(60f, PI*2, Easing.easeInOutSine)
            .add(60f, PI*2)
            .finish().repeatCount = -1
        arc.startAngle_im.animateKeyframes(0.0)
            .add(60f, 0.0)
            .add(60f, PI*2, Easing.easeInOutSine)
            .finish().repeatCount = -1

        test.add(arc)
    }
}
