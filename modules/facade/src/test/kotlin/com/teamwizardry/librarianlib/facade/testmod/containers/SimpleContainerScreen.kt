package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.math.Align2d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class SimpleContainerScreen(
    container: SimpleContainer,
    inventory: PlayerInventory,
    title: Text
): FacadeContainerScreen<SimpleContainer>(container, inventory, title) {

    init {
        main.size = vec(90, 30)

        val button = PastryButton("Click me!", 0, 0)
        button.hook<PastryButton.ClickEvent> {
            val position = vec(button.mousePos.xi, button.mousePos.yi)
            player.sendMessage(LiteralText("[GUI] mouse click event: $position"), false)
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