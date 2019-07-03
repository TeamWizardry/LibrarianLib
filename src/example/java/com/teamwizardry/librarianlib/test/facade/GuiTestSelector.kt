package com.teamwizardry.librarianlib.test.facade

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.facade.tests.*
import com.teamwizardry.librarianlib.test.facade.pastry.GuiTestPastry
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
        ListItem.Gui("Layer opacity") { GuiTestOpacity() },
        ListItem.Gui("GUI auto-scale") { GuiTestAutoSizeScale() },
        ListItem.Gui("Sprite") { GuiTestSprite() },
        ListItem.Gui("Mouse Clicks") { GuiTestClickEvents() },
        ListItem.Gui("MouseOver flags") { GuiTestMouseOverFlags() },
        ListItem.Gui("Provided Book") { GuiTestProvidedBook() },
        ListItem.Gui("Layout") { GuiTestLayout() },
        ListItem.Gui("Implicit Animation") { GuiTestImplicitAnimation() },
        ListItem.Gui("Value Animation") { GuiTestIMRMValueAnimation() },
        ListItem.Gui("Contents Bounds") { GuiTestGetContentBounds() },
        ListItem.Gui("Coordinate Conversion") { GuiTestCoordinateConversion() },
        ListItem.Gui("Arc") { GuiTestArc() },
        ListItem.Gui("Keyframe Builder") { GuiTestValueKeyframeBuilder() },
        ListItem.Gui("Text") { GuiTestTextLayer() },
        ListItem.Gui("Text Formatting") { GuiTestTextFormatting() },
        ListItem.Gui("Fluid Gauge") { GuiTestFluidGauge() },
        ListItem.Gui("Pastry") { GuiTestPastry() },
        ListItem.Gui("Text Field") { GuiTestTextField() },
        ListItem.Gui("Mid-frame texture upload order") { GuiTestMidFrameTexUpload() },

        ListItem.Gui("<fix for commas in diffs>") { throw RuntimeException("How was this called?") }
    ).dropLast(1)

    init {
        main.size = vec(300, 200)
        val background = RectLayer(Color.GRAY, 0, 0, 300, 200)
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
                    }
                } catch (e: Exception) {
                    LibrarianLog.error(e, "The safety net caught an error initializing GUI")
                    Minecraft.getMinecraft().displayGuiScreen(GuiSafetyNetError(e))
                }
            }
            scrollComponent.add(text)
        }
        scrollComponent.BUS.hook<GuiComponentEvents.MouseWheelEvent> {
            if(it.amount < 0) {
                scrollComponent.contentsOffset = scrollComponent.contentsOffset.setY(
                    max(-scrollAmount, scrollComponent.contentsOffset.y - 12)
                )
            } else {
                scrollComponent.contentsOffset = scrollComponent.contentsOffset.setY(
                    min(0.0, scrollComponent.contentsOffset.y + 12)
                )
            }
        }
        main.add(background, scrollComponent)
    }

    sealed class ListItem(val name: String) {
        class Gui(name: String, val create: () -> GuiBase): ListItem(name)
    }
}
