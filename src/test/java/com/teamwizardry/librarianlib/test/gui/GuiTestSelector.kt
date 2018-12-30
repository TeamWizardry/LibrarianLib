package com.teamwizardry.librarianlib.test.gui

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.gui.tests.*
import net.minecraft.client.Minecraft
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Created by TheCodeWarrior
 */
class GuiTestSelector : GuiBase() {

    val items = listOf(
        ListItem("Rect") { GuiTestRect() },
        ListItem("Move") { GuiTestResizeMove() },
        ListItem("Scale") { GuiTestScale() },
        ListItem("Scissor") { GuiTestScissor() },
        ListItem("Stencil") { GuiTestStencil() },
        ListItem("Stencil Sprite") { GuiTestStencilSprite() },
        ListItem("Stencil mouseOver") { GuiTestClippedMouseOver() },
        ListItem("GUI auto-scale") { GuiTestAutoSizeScale() },
        ListItem("Sprite") { GuiTestSprite() },
        ListItem("Mouse Clicks") { GuiTestClickEvents() },
        ListItem("MouseOver flags") { GuiTestMouseOverFlags() },
        ListItem("Provided Book") { GuiTestProvidedBook() },
        ListItem("Layout") { GuiTestLayout() },
        ListItem("Implicit Animation") { GuiTestImplicitAnimation() },
        ListItem("Value Animation") { GuiTestIMRMValueAnimation() },
        ListItem("Animation Await") { GuiTestAnimationAwait() },
        ListItem("Contents Bounds") { GuiTestGetContentBounds() },
        ListItem("Coordinate Conversion") { GuiTestCoordinateConversion() },
        ListItem("Arc") { GuiTestArc() },
        ListItem("Keyframe Builder") { GuiTestValueKeyframeBuilder() },
        ListItem("Text") { GuiTestTextLayer() },
        ListItem("MCTiny Text") { GuiTestMCTiny() },
        ListItem("Pastry") { GuiTestPastry() },
        ListItem("Text Field") { GuiTestTextField() },

        ListItem("<fix for commas in diffs>") { throw RuntimeException("How was this called?") }
    ).dropLast(1)

    init {
        main.size = vec(300, 200)
        val background = ColorLayer(Color.GRAY, 0, 0, 300, 200)
        val height = 12 * items.size
        val scrollComponent = GuiComponent(10, 10, 280, 180)
        val scrollAmount = max(0, height-scrollComponent.size.yi).toDouble()
        scrollComponent.clipToBounds = true
        items.forEachIndexed { i, item ->
            val text = ComponentText(0, 12*i)
            text.size = vec(280, 12)
            text.text = item.name
            text.BUS.hook<GuiComponentEvents.MouseClickEvent> {
                Minecraft.getMinecraft().displayGuiScreen(item.create())
            }
            scrollComponent.add(text)
        }
        scrollComponent.BUS.hook<GuiComponentEvents.MouseWheelEvent> {
            when(it.direction) {
                GuiComponentEvents.MouseWheelDirection.UP ->
                    scrollComponent.contentsOffset = scrollComponent.contentsOffset.setY(
                        max(-scrollAmount, scrollComponent.contentsOffset.y - 12)
                    )
                GuiComponentEvents.MouseWheelDirection.DOWN ->
                    scrollComponent.contentsOffset = scrollComponent.contentsOffset.setY(
                        min(0.0, scrollComponent.contentsOffset.y + 12)
                    )
            }
        }
        main.add(background, scrollComponent)
    }

    data class ListItem(val name: String, val create: () -> GuiBase)
}
