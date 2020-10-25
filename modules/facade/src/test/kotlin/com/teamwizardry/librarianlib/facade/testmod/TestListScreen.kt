package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.StringTextComponent
import java.awt.Color
import kotlin.math.max

class TestListScreen(name: String, val tests: List<FacadeTestEntry>): Screen(StringTextComponent(name)) {
    val itemHeight: Int = Client.fontRenderer.FONT_HEIGHT + 1
    val border: Int = 3

    val size: Vec2d = vec(300, 20 * itemHeight)
    var guiPos: Vec2d = vec(0, 0)
    var scrollAmount: Double = 0.0

    val scrollMax: Double = max(0.0, tests.size * itemHeight + size.y)

    override fun init() {
        super.init()
        guiPos = (vec(width, height) - this.size) / 2
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)

        fill(
            guiPos.xi - border, guiPos.yi - border,
            guiPos.xi + size.xi + border, guiPos.yi + size.yi + border,
            Color.lightGray.rgb
        )

        val relX = mouseX - guiPos.xi
        val relY = mouseY - guiPos.yi

        val scrollCount = (scrollAmount / itemHeight).toInt()
        val hoveredIndex = if(relX < 0 || relX > size.x) -1 else relY / itemHeight + scrollCount

        for(i in scrollCount until tests.size) {
            if(i == hoveredIndex) {
                fill(
                    guiPos.xi, guiPos.yi + i * itemHeight,
                    guiPos.xi + size.xi, guiPos.yi + i * itemHeight + Client.fontRenderer.FONT_HEIGHT,
                    Color.gray.rgb
                )
            }

            Client.fontRenderer.drawString(tests[i].name, guiPos.xf, guiPos.yf + i * itemHeight, Color(0, 0, 0, 0).rgb)
        }
    }

    override fun mouseScrolled(x: Double, y: Double, delta: Double): Boolean {
        scrollAmount = (scrollAmount + delta).clamp(0.0, scrollMax)
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val relX = (mouseX - guiPos.x).toInt()
        val relY = (mouseY - guiPos.y).toInt()

        val scrollCount = (scrollAmount / itemHeight).toInt()
        val hoveredIndex = if(relX < 0 || relX > size.x) -1 else relY / itemHeight + scrollCount

        tests.getOrNull(hoveredIndex)?.also { test ->
            Client.displayGuiScreen(test.create())
        }

        return false
    }
}