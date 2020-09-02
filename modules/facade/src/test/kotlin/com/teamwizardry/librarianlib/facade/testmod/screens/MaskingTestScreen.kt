package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.MaskMode
import com.teamwizardry.librarianlib.facade.layer.supporting.RenderMode
import com.teamwizardry.librarianlib.facade.layers.ArcLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import net.minecraft.util.text.ITextComponent
import java.awt.Color
import kotlin.math.PI

class MaskingTestScreen(title: ITextComponent): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 250, 100)
        main.size = bg.size
//        main.add(bg)

        val outer1 = GuiLayer(10, 10, 80, 80)
        val inner1 = RectLayer(Color(0f, 1f, 0f, 0.5f), 0, 0, 80, 80)
        val mask1 = SpriteLayer(spiral, 40, 40, 120, 120)
        mask1.anchor = vec(0.5, 0.5)
        mask1.rotation_rm.animate(0.0, PI * 2, 40f).repeatForever()
        outer1.add(mask1, inner1)

        val outer2 = GuiLayer(160, 10, 80, 80)
        val inner2 = RectLayer(Color.GREEN, 0, 0, 80, 80)
        val mask2 = SpriteLayer(spiral, 40, 40, 120, 120)
        mask2.anchor = vec(0.5, 0.5)
        mask2.rotation_rm.animate(0.0, PI * 2, 40f).repeatForever()
        outer2.add(inner2)
        outer2.maskLayer = mask2
        outer2.maskMode = MaskMode.LUMA_ON_BLACK

        main.add(outer1, outer2)
    }

    companion object {
        val spiral: Sprite = Mosaic("librarianlib-facade-test:textures/spiral_mask.png".toRl(), 128, 128).getSprite("")
    }
}