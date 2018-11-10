package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.FixedSizeComponent
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

class PastryCheckbox(posX: Int, posY: Int): PastryToggle(posX, posY, 7, 7) {
    private val background = SpriteLayer(PastryTexture.checkboxOff, 0, 0, 7, 7)
    private val checked = SpriteLayer(PastryTexture.checkboxOn, 0, 0, 7, 7)
//    private val checkedMask = GuiLayer(0, 0, 0, 0)

    override fun stateChanged(state: Boolean) {
    }

    override fun visualStateChanged(visualState: Boolean) {
        checked.isVisible = visualState
    }

    init {
//        switchOnMask.clipToBounds = true
//        switchOnMask.add(switchOn)
        checked.isVisible = false
        this.add(background, checked)
    }
}