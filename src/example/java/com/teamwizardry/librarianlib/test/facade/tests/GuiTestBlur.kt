package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.provided.filter.GaussianBlurFilter
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestBlur : GuiBase() {
    init {
        main.size = vec(100, 100)

        val normal = GuiLayer(-75, 0, 100, 100)
        normal.add(RectLayer(Color(1f, 0f, 0f, 0.5f), 0, 0, 75, 75))
        val normalBlue = RectLayer(Color(0f, 0f, 1f, 0.5f), 25, 25, 75, 75)
        normal.add(normalBlue)

        val flat = GuiLayer(75, 0, 100, 100)
        flat.add(RectLayer(Color.RED, 0, 0, 75, 75))
        val flatBlue = RectLayer(Color.BLUE, 25, 25, 75, 75)
        flat.add(flatBlue)
        flat.layerFilter = GaussianBlurFilter(3)

        main.add(normal, flat)

        main.hook<GuiLayerEvents.PreFrameEvent> {
            val y = 100 * (ClientTickHandler.ticks % 40.0) / 40.0
            normalBlue.y = y
            flatBlue.y = y
        }
    }
}
