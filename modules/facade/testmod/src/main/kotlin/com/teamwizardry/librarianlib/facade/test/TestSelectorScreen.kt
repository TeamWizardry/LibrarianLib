package com.teamwizardry.librarianlib.facade.test

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import java.awt.Color
import kotlin.math.max

class TestSelectorScreen(name: String, val selector: TestSelector, val onSelect: (String) -> Unit): Screen(Text.literal(name)) {
    val entries: List<SelectorEntry> = selector.entries

    val itemHeight: Int = Client.textRenderer.fontHeight + 1
    val indentWidth: Int = 6
    val border: Int = 3

    val size: Vec2d = vec(300, 20 * itemHeight)
    var guiPos: Vec2d = vec(0, 0)
    var scrollAmount: Double = 0.0

    val scrollMax: Double get() = max(0.0, entries.size * itemHeight + size.y)

    override fun init() {
        super.init()
        guiPos = (vec(width, height) - this.size) / 2
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        context.fill(
            guiPos.xi - border, guiPos.yi - border,
            guiPos.xi + size.xi + border, guiPos.yi + size.yi + border,
            Color.lightGray.rgb
        )

        val relX = mouseX - guiPos.xi
        val relY = mouseY - guiPos.yi

        val scrollCount = (scrollAmount / itemHeight).toInt()
        val hoveredIndex = if(relX < 0 || relX > size.x) -1 else relY / itemHeight + scrollCount

        for(i in scrollCount until entries.size) {
            val entry = entries[i]
            if(i == hoveredIndex && entry.screen != null) {
                context.fill(
                    guiPos.xi, guiPos.yi + i * itemHeight,
                    guiPos.xi + size.xi, guiPos.yi + i * itemHeight + Client.textRenderer.fontHeight,
                    Color.gray.rgb
                )
            }

            context.drawText(
                Client.textRenderer,
                entry.path.name,
                guiPos.xi + indentWidth * entry.path.depth,
                guiPos.yi + i * itemHeight,
                Color(0, 0, 0, 0).rgb,
                false
            )
        }
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        scrollAmount = (scrollAmount + verticalAmount).clamp(0.0, scrollMax)
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val relX = (mouseX - guiPos.x).toInt()
        val relY = (mouseY - guiPos.y).toInt()

        val scrollCount = (scrollAmount / itemHeight).toInt()
        val hoveredIndex = if(relX < 0 || relX > size.x) -1 else relY / itemHeight + scrollCount

        val entry = entries.getOrNull(hoveredIndex)

        val start = System.nanoTime()
        val screen = entry?.create()
        if(screen != null) {
            logger.debug("Creating `{}` screen took {} ms", entry.path, (System.nanoTime() - start) / 1_000_000)
            onSelect(entry.path.toString())
            logger.debug("Sent path `{}` to be recorded for reopening", entry.path)
            Client.openScreen(screen)
        }

        return false
    }

    companion object {
        private val logger = LibLibFacadeTest.logManager.makeLogger<TestSelectorScreen>()
    }
}
