package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
open class PastryTabPage() : GuiComponent() {
    constructor(labelText: String) : this() {
        val label = PastryLabel(labelText)
        tab.contents.add(label)
        tab.contents.size = label.size
    }

    var selected: Boolean = false
        set(value) {
            field = value
            contents.isVisible = value
            tab.background.sprite = if(value) PastryTexture.tabsButtonPressed else PastryTexture.tabsButton
            if(value)
                siblings.forEach {
                    it.selected = false
                }
        }

    val tab: Tab = Tab()
    val contents: GuiComponent = GuiComponent()
    val siblings: List<PastryTabPage>
        get() = parent?.children?.filterIsInstance<PastryTabPage>()?.filter { it !== this } ?: emptyList()

    init {
        add(tab, contents)
    }

    inner class Tab : GuiComponent() {
        internal val background = SpriteLayer(PastryTexture.tabsButton)
        val contents: GuiLayer = GuiLayer(0, 0, 25, 12)

        init {
            add(background, contents)
            cursor = LibCursor.POINT
        }

        override fun layoutChildren() {
            val margin = vec(4, 3)
            this.size = contents.frame.size + margin * 2
            contents.pos = margin
            background.frame = this.bounds
        }

        @Hook
        fun click(e: GuiComponentEvents.MouseClickEvent) {
            this@PastryTabPage.selected = true
        }
    }
}