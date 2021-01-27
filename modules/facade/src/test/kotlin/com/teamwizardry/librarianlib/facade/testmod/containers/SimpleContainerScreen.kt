package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.math.Align2d
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

class SimpleContainerScreen(
    container: SimpleContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<SimpleContainer>(container, inventory, title) {

    init {
        main.size = vec(90, 30)

        val button = PastryButton("Click me!", 0, 0)
        button.hook<PastryButton.ClickEvent> {
            val position = vec(button.mousePos.xi, button.mousePos.yi)
            player.sendStatusMessage(StringTextComponent("[GUI] mouse click event: $position"), false)
            sendMessage("buttonClick", position)
        }

        main.add(
            StackLayout.build(0, 0)
                .align(Align2d.CENTER)
                .size(main.size)
                .add(button)
                .build()
        )
    }
}