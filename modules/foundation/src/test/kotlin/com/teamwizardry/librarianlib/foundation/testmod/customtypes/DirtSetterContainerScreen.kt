package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.facade.layers.StackLayout.Companion.build
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.math.Align2d
import com.teamwizardry.librarianlib.math.vec

class DirtSetterContainerScreen(
    container: DirtSetterContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<DirtSetterContainer>(container, inventory, title) {
    init {
        main.size = vec(100, 50)
        main.add(PastryBackground(0, 0, 100, 50))

        val plusOne = PastryButton("Set Y+1 to dirt") {
            container.sendMessage("setToDirt", 1)
        }
        val zero = PastryButton("Set Y+0 to dirt") {
            container.sendMessage("setToDirt", 0)
        }
        val minusOne = PastryButton("Set Y-1 to dirt") {
            container.sendMessage("setToDirt", -1)
        }

        main.add(
            build()
                .align(Align2d.CENTER)
                .size(main.size)
                .spacing(1)
                .add(plusOne, zero, minusOne)
                .build()
        )
    }
}