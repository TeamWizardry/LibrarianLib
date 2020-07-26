package com.teamwizardry.librarianlib.facade.testmod.screens.pastry.tests

import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.components.PastryScrollPane
import com.teamwizardry.librarianlib.facade.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.facade.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.facade.testmod.screens.pastry.PastryTestBase
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.mosaic.Mosaic
import com.teamwizardry.librarianlib.mosaic.Sprite
import net.minecraft.util.ResourceLocation

class PastryTestScroll: PastryTestBase() {
    init {
        val size = vec(150, 150)
        val scroll = PastryScrollPane(0, 0, size.xi + 2, size.yi + 2)
        val contentBackground = SpriteLayer(Mosaic(ResourceLocation("textures/blocks/dirt.png"), 16, 16).getSprite(""))
        scroll.content.add(contentBackground)
        scroll.showHorizontalScrollbar = null
        scroll.showVerticalScrollbar = null

        val sizeDropdown = PastryDropdown<Vec2d>(3, 0, 0) {
            scroll.content.size = it
            contentBackground.size = it
        }
        sizeDropdown.items.addAll(
            listOf(
                "Zero" to vec(0, 0),
                "Small" to size * 0.75,
                "Exact" to size,
                "Wide" to size * vec(1.5, 1),
                "Wide + Short" to size * vec(1.5, 0.75),
                "Tall" to size * vec(1, 1.5),
                "Tall + Narrow" to size * vec(0.75, 1.5),
                "Big" to size * vec(1.5, 1.5),
                "Huge" to size * vec(2.5, 2.5)
            ).map {
                DropdownTextItem(it.second, "${it.first} (${it.second.x}, ${it.second.y})")
            }
        )
        sizeDropdown.sizeToFit()
        sizeDropdown.select(size)
        this.stack.add(sizeDropdown)
        this.stack.add(scroll)
    }
}