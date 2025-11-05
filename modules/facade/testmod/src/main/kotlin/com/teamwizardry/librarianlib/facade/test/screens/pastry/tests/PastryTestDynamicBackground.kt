package com.teamwizardry.librarianlib.facade.test.screens.pastry.tests

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryDynamicBackground
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.facade.pastry.layers.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.facade.pastry.layers.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.facade.test.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.math.Vec2d
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

class PastryTestDynamicBackground: PastryTestBase() {
    init {
        val background = DynamicBackgroundTestLayer()

        val dropdown: PastryDropdown<PastryBackgroundStyle>
        val stacks = PastryBackgroundStyle.values().toList()

        val dropdownWidth = PastryBackgroundStyle.values().map {
            PastryLabel(0, 0, it.name).widthi
        }.maxOrNull() ?: 50
        dropdown = PastryDropdown(0, 0, dropdownWidth + 15) {
            background.background.style = it
        }
        dropdown.items.addAll(stacks.map {
            DropdownTextItem(it, it.name)
        })
        dropdown.select(PastryBackgroundStyle.VANILLA)

        this.stack.add(dropdown, PastryButton("Reset", 0, 0) { background.reset() }, background)
    }

    class DynamicBackgroundTestLayer(): GuiLayer(0, 0, 175, 150) {
        var dragStart: Vec2d? = null

        var colorBG = RectLayer(Color.MAGENTA, widthi, heighti)
        var background = PastryDynamicBackground()

        init {
            this.add(colorBG, background)
        }

        fun reset() {
            background.forEachChild { it.removeFromParent() }
            background.shapeLayers.clear()
        }

        @Hook
        fun mouseDown(e: GuiLayerEvents.MouseDown) {
            if(!mouseOver) return
            dragStart = mousePos.round()
        }

        @Hook
        fun mouseUp(e: GuiLayerEvents.MouseUp) {
            dragStart?.also { dragStart ->
                val dragEnd = mousePos.round()
                val dragMin = vec(min(dragStart.x, dragEnd.x), min(dragStart.y, dragEnd.y))
                val dragMax = vec(max(dragStart.x, dragEnd.x), max(dragStart.y, dragEnd.y))
                val dragSize = dragMax - dragMin
                if(dragSize != vec(0, 0)) {
                    val layer = GuiLayer()
                    layer.pos = dragMin
                    layer.size = dragSize
                    background.add(layer)
                    background.addShapeLayers(layer)
                }
            }
            dragStart = null
        }
    }
}