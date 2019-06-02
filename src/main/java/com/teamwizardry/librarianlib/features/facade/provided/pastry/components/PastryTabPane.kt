package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
class PastryTabPane(type: BackgroundTexture, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    constructor(posX: Int, posY: Int, width: Int, height: Int) : this(BackgroundTexture.DEFAULT, posX, posY, width, height)

    private val sprite = SpriteLayer(type.background, -2, -2, 0, 0)

    val tabs = LinkedHashMap<GuiComponent, GuiComponent>()

    init {
        this.add(sprite)
    }

    fun addTab(tabName: String): GuiComponent {
        val tabButton = PastryButton(tabName, 0, 0, 75)

        val tab = GuiComponent(0, tabButton.heighti + 10, widthi, heighti - tabButton.heighti)
        tab.isVisible = false

        tabs[tabButton] = tab
        add(tabButton, tab)


        tabButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            for (entryTab in tabs.values)
                entryTab.isVisible = false
            tab.isVisible = true
        }

        for ((i, entryButton) in tabs.keys.withIndex()) {
            entryButton.width = width / tabs.size.toDouble()
            entryButton.pos = vec(i * entryButton.width, entryButton.y)
        }
        return tab
    }

    override fun layoutChildren() {
        sprite.size = this.size + vec(4, 4)
    }
}