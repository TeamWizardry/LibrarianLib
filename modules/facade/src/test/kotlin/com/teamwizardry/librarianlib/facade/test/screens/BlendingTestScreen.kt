package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.math.Easing
import net.minecraft.text.Text
import java.awt.Color

class BlendingTestScreen(title: Text): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.BLACK, 0, 0, 200, 100)
        main.size = bg.size
        main.add(bg)

        main.add(RectLayer(Color.RED, 10, 10, 80, 80))
        main.add(RectLayer(Color.BLUE, 110, 10, 80, 80))

        val diff = GuiLayer(0, 0, 100, 100)
        diff.pos_rm.animate(vec(100, 0), 40f, Easing.easeInOutQuart).reverseOnRepeat().repeat()
        diff.blendMode = DefaultRenderStates.Blend.ADDITIVE
        diff.add(RectLayer(Color.GREEN, 10, 10, 80, 80))

        main.add(diff)
    }
}