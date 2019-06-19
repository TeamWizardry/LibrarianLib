package com.teamwizardry.librarianlib.test.facade.pastry.tests

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryButton
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryScrollPane
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.Sprite
import com.teamwizardry.librarianlib.test.facade.pastry.PastryTestBase
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.util.ResourceLocation

@UseExperimental(ExperimentalBitfont::class)
class PastryTestScroll: PastryTestBase() {
    init {
        val size = vec(150, 150)
        val scroll = PastryScrollPane(0, 0, size.xi + 2, size.yi + 2)
        val contentBackground = SpriteLayer(Sprite(ResourceLocation("textures/blocks/dirt.png")))
        scroll.content.add(contentBackground)

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