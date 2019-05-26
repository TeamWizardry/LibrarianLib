package com.teamwizardry.librarianlib.test.neogui

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.components.ComponentText
import com.teamwizardry.librarianlib.features.neogui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.neogui.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.neogui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.neogui.tests.*
import com.teamwizardry.librarianlib.test.neogui.tests.windows.*
import net.minecraft.client.Minecraft
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Created by TheCodeWarrior
 */
class GuiTestSelector : GuiBase() {

    val items = listOf(
        ListItem.Gui("Rect") { GuiTestRect() },
        ListItem.Gui("Move") { GuiTestResizeMove() },
        ListItem.Gui("Scale") { GuiTestScale() },
        ListItem.Gui("Scissor") { GuiTestScissor() },
        ListItem.Gui("Stencil") { GuiTestStencil() },
        ListItem.Gui("Stencil Sprite") { GuiTestStencilSprite() },
        ListItem.Gui("Stencil mouseOver") { GuiTestClippedMouseOver() },
        ListItem.Gui("GUI auto-scale") { GuiTestAutoSizeScale() },
        ListItem.Gui("Sprite") { GuiTestSprite() },
        ListItem.Gui("Mouse Clicks") { GuiTestClickEvents() },
        ListItem.Gui("MouseOver flags") { GuiTestMouseOverFlags() },
        ListItem.Gui("Provided Book") { GuiTestProvidedBook() },
        ListItem.Gui("Layout") { GuiTestLayout() },
        ListItem.Gui("Implicit Animation") { GuiTestImplicitAnimation() },
        ListItem.Gui("Value Animation") { GuiTestIMRMValueAnimation() },
        ListItem.Gui("Animation Await") { GuiTestAnimationAwait() },
        ListItem.Gui("Contents Bounds") { GuiTestGetContentBounds() },
        ListItem.Gui("Coordinate Conversion") { GuiTestCoordinateConversion() },
        ListItem.Gui("Arc") { GuiTestArc() },
        ListItem.Gui("Keyframe Builder") { GuiTestValueKeyframeBuilder() },
        ListItem.Gui("Text") { GuiTestTextLayer() },
        ListItem.Gui("MCTiny Text") { GuiTestMCTiny() },
        ListItem.Gui("Pastry") { GuiTestPastry() },
        ListItem.Gui("Text Field") { GuiTestTextField() },

        ListItem.Window("Single Window") { GuiTestSingleWindow() },
        ListItem.Window("Multi Window") { GuiTestMultiWindow() },
        ListItem.Window("Pastry Window Base") { GuiTestPastryWindowBase() },
        ListItem.Window("Multi Pastry Window Base") { GuiTestMultiPastryWindowBase() },
        ListItem.Window("Pastry Window") { GuiTestPastryWindow() },
        ListItem.Window("Pastry Window Example") { GuiTestPastryWindowExample() },
        ListItem.Window("Pastry Color Picker") { GuiTestPastryColorPicker() },

        ListItem.Window("Flexbox Layout") { GuiTestFlexBox() },

        ListItem.Gui("<fix for commas in diffs>") { throw RuntimeException("How was this called?") }
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
                try {
                    when(item) {
                        is ListItem.Gui -> Minecraft.getMinecraft().displayGuiScreen(item.create())
                        is ListItem.Window -> item.create().open()
                    }
                } catch (e: Exception) {
                    LibrarianLog.error(e, "The safety net caught an error initializing GUI")
                    Minecraft.getMinecraft().displayGuiScreen(GuiSafetyNetError(e))
                }
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

    sealed class ListItem(val name: String) {
        class Gui(name: String, val create: () -> GuiBase): ListItem(name)
        class Window(name: String, val create: () -> GuiWindow): ListItem(name)
    }
}
