package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeView
import com.teamwizardry.librarianlib.facade.container.layers.SlotGridLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryCheckbox
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import java.awt.Color

class OcclusionScreen(
    container: OcclusionController,
    inventory: PlayerInventory,
    title: Text
): FacadeView<OcclusionController>(container, inventory, title) {

    init {
        val checkbox = PastryCheckbox(0, 0)
        val checkboxRow = StackLayout.build(5, 5)
            .horizontal()
            .alignLeft()
            .alignCenterY()
            .add(checkbox)
            .add(PastryLabel("Show occluder"))
            .fit()
            .build()
        main.add(checkboxRow)
        val storageSlots = SlotGridLayer(0, 0, container.contentsSlots.all, 5)
        val stack = StackLayout.build(5, checkboxRow.frame.maxY.toInt() + 2)
            .vertical()
            .alignCenterX()
            .spacing(4)
            .add(storageSlots)
            .add(SlotGridLayer(0, 0, container.playerSlots.main, 9))
            .add(SlotGridLayer(0, 0, container.playerSlots.hotbar, 9))
            .fit()
            .build()
        main.size = stack.frame.max + vec(5, 5)
        main.add(stack)
        main.runLayout()

        val storageSlotsFrame = storageSlots.convertFrameTo(mainOverlay)
        val occluder = RectLayer(Color(1f, 0f, 0f, 0.2f))
        occluder.frame = storageSlotsFrame.shrink(3.0)
        occluder.width -= 18 * 2
        occluder.isVisible_im.set { checkbox.state }
        mainOverlay.add(occluder)
    }
}