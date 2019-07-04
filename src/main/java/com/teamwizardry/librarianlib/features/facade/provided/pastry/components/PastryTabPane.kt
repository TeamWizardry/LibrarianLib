package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

@ExperimentalBitfont
@ExperimentalPastryAPI
class PastryTabPane : GuiComponent {
    private val background = SpriteLayer(PastryTexture.tabsBody)

    val pages: List<PastryTabPage>
        get() = children.filterIsInstance<PastryTabPage>().filter { it.isVisible }

    constructor(): super()
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    init {
        this.add(background)
        this.listenToChildrenNeedsLayout = true
    }

    override fun layoutChildren() {
        pages.forEach {
            it.frame = this.bounds
        }
        children.forEach { it.runLayoutIfNeeded() }
        var x = 1.0
        val maxTabHeight = pages.map { it.tab.height }.max() ?: 0.0
        pages.forEach {
            it.tab.pos = vec(x, maxTabHeight-it.tab.height)
            x += it.tab.width + 1
            it.contents.frame = it.bounds.offset(0, maxTabHeight, 0, 0)
        }
        background.frame = this.bounds.offset(0, maxTabHeight-1, 0, 0)
    }
}