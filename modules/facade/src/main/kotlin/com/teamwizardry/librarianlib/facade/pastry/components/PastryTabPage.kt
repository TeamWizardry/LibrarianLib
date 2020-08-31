package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.math.vec

open class PastryTabPage() : GuiLayer() {
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
    val contents: GuiLayer = GuiLayer()
    val siblings: List<PastryTabPage>
        get() = parent?.children?.filterIsInstance<PastryTabPage>()?.filter { it !== this } ?: emptyList()

    init {
        add(tab, contents)
    }

    inner class Tab : GuiLayer() {
        internal val background = SpriteLayer(PastryTexture.tabsButton)
        val contents: GuiLayer = GuiLayer(0, 0, 25, 12)

        init {
            add(background, contents)
            propagatesMouseOver = false
        }

        override fun layoutChildren() {
            val margin = vec(4, 3)
            this.size = contents.frame.size + margin * 2
            contents.pos = margin
            background.frame = this.bounds
        }

        @Hook
        fun click(e: GuiLayerEvents.MouseClick) {
            this@PastryTabPage.selected = true
        }
    }
}