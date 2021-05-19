package com.teamwizardry.librarianlib.facade.test.containers.base

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeView
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import kotlin.math.max

class TestView(
    container: TestContainerSelectorController,
    inventory: PlayerInventory,
    title: Text
): FacadeView<TestContainerSelectorController>(container, inventory, title) {

    private val stack = StackLayout.build(0, 0)
        .width(300)
        .build()

    init {
        main.size = vec(260, 210)

        main.add(PastryBackground(PastryBackgroundStyle.LIGHT_INSET, 4, 4, 252, 202))
        val scrollWrapper = GuiLayer(5, 5, 250, 200)
        scrollWrapper.clipToBounds = true
        scrollWrapper.add(stack)

        scrollWrapper.hook<GuiLayerEvents.LayoutChildren> {
            if(stack.y > 0)
                stack.y = 0.0
            val maxScroll = max(0.0, stack.height - scrollWrapper.height)
            if(stack.y < -maxScroll) {
                stack.y = -maxScroll
            }
        }
        scrollWrapper.hook<GuiLayerEvents.MouseScroll> {
            stack.y += it.delta.y
        }

        val button = PastryButton("Click me!", 0, 0)
        button.hook<PastryButton.ClickEvent> {
            val position = vec(button.mousePos.xi, button.mousePos.yi)
            player.sendMessage(LiteralText("[GUI] mouse click event: $position"), false)
            sendMessage("buttonClick", position)
        }

        main.add(scrollWrapper)

        selectGroup(container.containerSet.root)
    }

    fun selectGroup(group: TestContainerSet.Entry.Group) {
        stack.forEachChild { it.removeFromParent() }
        for(entry in group.entries) {
            stack.add(PastryButton(entry.name, 0, 0, 250) {
                when(entry) {
                    is TestContainerSet.Entry.Container -> {
                        sendMessage("selectType", entry.type.id)
                    }
                    is TestContainerSet.Entry.Group -> {
                        selectGroup(entry)
                    }
                }
            })
        }
        stack.fitToLength()
    }

}