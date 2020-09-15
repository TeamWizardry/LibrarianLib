package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.ExperimentalPastryAPI
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.math.vec

@ExperimentalPastryAPI
public class PastryTabPane: GuiLayer {
    private val background = SpriteLayer(PastryTexture.tabsBody)

    public val pages: List<PastryTabPage>
        get() = children.filterIsInstance<PastryTabPage>().filter { it.isVisible }

    public constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)
    public constructor(posX: Int, posY: Int): super(posX, posY)
    public constructor(): super()

    init {
        this.add(background)
    }

    override fun layoutChildren() {
        pages.forEach {
            it.frame = this.bounds
        }
        children.forEach { it.runLayout() }
        var x = 1.0
        val maxTabHeight = pages.map { it.tab.height }.maxOrNull() ?: 0.0
        pages.forEach {
            it.tab.pos = vec(x, maxTabHeight - it.tab.height)
            x += it.tab.width + 1
            it.contents.frame = it.bounds.offset(0, maxTabHeight, 0, 0)
        }
        background.frame = this.bounds.offset(0, maxTabHeight - 1, 0, 0)
    }
}