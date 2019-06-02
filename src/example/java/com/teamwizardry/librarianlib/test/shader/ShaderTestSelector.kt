package com.teamwizardry.librarianlib.test.shader

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.layers.ColorLayer
import com.teamwizardry.librarianlib.features.facade.provided.GuiSafetyNetError
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.test.shader.test.SimpleShaderTest
import com.teamwizardry.librarianlib.test.shader.test.TimeUniformTest
import net.minecraft.client.Minecraft
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Created by TheCodeWarrior
 */
class ShaderTestSelector : GuiBase() {

    val items = listOf(
        ListItem("Simple") { SimpleShaderTest() },
        ListItem("Time Uniform") { TimeUniformTest() },
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
                try {
                    Minecraft.getMinecraft().displayGuiScreen(item.create())
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

    class ListItem(val name: String, val create: () -> GuiBase)
}
