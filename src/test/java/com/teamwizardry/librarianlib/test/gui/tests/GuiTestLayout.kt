package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.height
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.width
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class GuiTestLayout : GuiBase() {
    init {
        main.size = vec(100, 100)

        val mainList = CustomComponent(0, 0, 75, 75)
        main.add(mainList)
        mainList.add(
            ComponentRect(0, 0, 0, 0).also { it.color = Color.RED },
            ComponentRect(0, 0, 0, 0).also { it.color = Color.GREEN }
        )

        val subListBox = ComponentRect(0, 0, 0, 0)
        subListBox.color = Color.BLACK
        mainList.add(subListBox)

        val subList = CustomComponent(0, 0, 0, 0)
        subListBox.add(subList)

        subListBox.BUS.hook(GuiLayerEvents.LayoutChildren::class.java) {
            subList.pos = vec(2, 2)
            subList.size = subListBox.size - vec(4, 4)
        }

        subList.add(
            ComponentRect(0, 0, 0, 0).also { it.color = Color.CYAN },
            ComponentRect(0, 0, 0, 0).also { it.color = Color.MAGENTA },
            ComponentRect(0, 0, 0, 0).also { it.color = Color.YELLOW }
        )


        val addRemoveBox = ComponentRect(0, 0, 0, 0).also { it.color = Color.BLUE }

        val sizeButton = ComponentRect(100, 0, 10, 10)
        main.add(sizeButton)
        sizeButton.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            fun rand() = ThreadLocalRandom.current().nextDouble(-10.0, 10.0)
            mainList.size += vec(rand(), rand())
        }

        val addRemoveButton = ComponentRect(100, 20, 10, 10)
        main.add(addRemoveButton)
        addRemoveButton.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if(addRemoveBox.parent == null) {
                mainList.add(addRemoveBox)
            } else {
                addRemoveBox.invalidate()
            }
        }

        val markLayoutButton = ComponentRect(100, 40, 10, 10)
        main.add(markLayoutButton)
        markLayoutButton.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            mainList.setNeedsLayout()
        }
    }

    class CustomComponent(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
        override fun layoutChildren() {
            val count = children.size
            val heightPer = this.height / count
            children.forEachIndexed { i, child ->
                child.pos = vec(0, heightPer * i)
                child.size = vec(this.width, heightPer)
            }
        }
    }
}
