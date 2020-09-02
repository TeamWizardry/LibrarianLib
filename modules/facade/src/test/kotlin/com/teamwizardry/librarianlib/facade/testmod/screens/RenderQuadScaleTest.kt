package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.RenderMode
import com.teamwizardry.librarianlib.facade.layers.ArcLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.testmod.FacadeTestScreen
import com.teamwizardry.librarianlib.math.Easing
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.util.text.ITextComponent
import java.awt.Color
import kotlin.math.PI

class RenderQuadScaleTest(title: ITextComponent): FacadeScreen(title) {
    init {
        val bg = RectLayer(Color.WHITE, 0, 0, 200, 100)
        main.size = bg.size
        main.add(bg)
        val outer1 = GuiLayer(10, 10, 8, 8)
        val inner1 = ArcLayer(Color.BLACK, 0, 0, 8, 8)
        inner1.startAngle = PI / 4
        inner1.endAngle = PI * 1.5
        outer1.add(inner1)

        val outer2 = GuiLayer(110, 10, 8, 8)
        outer2.scale = 10.0
        val inner2 = ArcLayer(Color.BLACK, 0, 0, 8, 8)
        inner2.startAngle = PI / 4
        inner2.endAngle = PI * 1.5
        outer2.add(inner2)
        outer2.renderMode = RenderMode.RENDER_TO_QUAD

        main.add(outer1, outer2)
    }
}