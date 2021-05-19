package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeView
import com.teamwizardry.librarianlib.facade.container.layers.SlotGridLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.math.Easing
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class JeiExclusionAreasScreen(
    container: JeiExclusionAreasController,
    inventory: PlayerInventory,
    title: Text
): FacadeView<JeiExclusionAreasController>(container, inventory, title) {

    init {
        val stack = StackLayout.build(5, 5)
            .vertical()
            .alignCenterX()
            .spacing(4)
            .add(SlotGridLayer(0, 0, container.contentsSlots.all, 1))
            .add(SlotGridLayer(0, 0, container.playerSlots.main, 9))
            .add(SlotGridLayer(0, 0, container.playerSlots.hotbar, 9))
            .fit()
            .build()
        main.size = stack.size + vec(10, 10)
        main.add(stack)

        val exclusion = GuiLayer(0, 0, 30, 40)
        val min = vec(main.width - exclusion.width, 10)
        val max = vec(main.width, main.height - exclusion.height - 10)
        exclusion.pos_rm.animateKeyframes(min)
            .add(10f, Easing.easeOutBounce, vec(max.x, min.y))
            .add(15f, Easing.easeInOutQuint, max)
            .add(10f, Easing.easeInQuad, vec(min.x, max.y))
            .add(10f, Easing.linear, min)
            .hold(10f)
            .repeat()
        main.add(exclusion)
        background.addShapeLayers(exclusion)
        jei.addExclusionArea(exclusion)
    }
}