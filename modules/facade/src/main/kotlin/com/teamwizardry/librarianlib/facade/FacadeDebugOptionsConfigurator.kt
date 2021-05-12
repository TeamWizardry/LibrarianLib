package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layer.FacadeDebugOptions
import com.teamwizardry.librarianlib.facade.provided.VanillaTooltipRenderer
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.reflect.KMutableProperty0

/**
 * A screen for modifying Facade's [debug options][FacadeDebugOptions]
 */
internal class FacadeDebugOptionsConfigurator(private val options: FacadeDebugOptions) {
    var isOpen: Boolean = false

    private val itemHeight: Int = Client.textRenderer.fontHeight + 1

    private val rows: List<OptionRow>
    private var width: Int = 0
    private val height: Int
    private var mousePos: Vec2d = vec(0, 0)

    init {
        rows = listOf(
            OptionRow.BooleanRow(
                "Debug Bounding Boxes:", """
                    Draw lines showing each layer's bounding box. The lines are white when `mouseOver` is true and \
                    are magenta otherwise.
                    
                    Shortcut: F3+B
                """, "§2Enabled", "§cDisabled", options::showDebugBoundingBox
            ),
            OptionRow.BooleanRow(
                "Show Clipped Bounding Boxes:", """
                    Draw a red bounding box around layers that are clipping to their bounds.
                """, "§2Enabled", "§cDisabled", options::showClippedBoundingBoxes
            ),
            OptionRow.BooleanRow(
                "Highlight Layout:", """
                    Draw a translucent overlay on layers that had their layout updated on this frame.
                """, "§2Enabled", "§cDisabled", options::highlightLayout
            ),
            OptionRow.BooleanRow(
                "Highlight Fractional Scale:", """
                    Draw a red bounding box around layers that have a fractional scale on either the X or Y axes. 
                    
                    Fractional scales can cause rendering artifacts. Note that this only detects literal scaling, so \
                    it won't detect sprite layers with sprites of mismatched sizes.
                """, "§2Enabled", "§cDisabled", options::highlightFractionalScale
            ),
            OptionRow.BooleanRow(
                "Make Stencil Masks Visible:", """
                    Enables color output when drawing stencil masks, rendering them visible. Normally drawing the mask 
                    doesn't touch the color buffer so it isn't visible, but it can be useful to see exactly the calls
                    that the mask is using.
                """, "§2Enabled", "§cDisabled", options::makeStencilMasksVisible
            ),
            OptionRow.BooleanRow(
                "Show GUI Scale Basis:", """
                    Shows the 320x240 bounding box used by Minecraft to calculate automatic GUI scale.
                """, "§2Enabled", "§cDisabled", options::showGuiScaleBasis
            ),
        )

        height = itemHeight * rows.size
    }

    fun shortcutKeyPressed(keyCode: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_B -> options.showDebugBoundingBox = !options.showDebugBoundingBox
            else -> return false
        }
        return true
    }

    fun mouseMoved(xPos: Double, yPos: Double) {
        mousePos = vec(xPos, yPos)
    }

    fun mouseClicked(xPos: Double, yPos: Double, button: Int) {
        val left = (Client.window.scaledWidth - width) / 2
        val top = (Client.window.scaledHeight - height) / 2

        val relX = (xPos - left).toInt()
        val relY = (yPos - top).toInt()

        val hoveredIndex = if (relX < 0 || relX > width) -1 else relY / itemHeight

        rows.getOrNull(hoveredIndex)?.also { row ->
            row.click()
        }
    }

    fun render(matrixStack: MatrixStack) {
        rows.forEach { it.computeStateText() }
        width = rows.maxOf { it.labelWidth + 2 + it.stateWidth }
        val left = (Client.window.scaledWidth - width) / 2
        val top = (Client.window.scaledHeight - height) / 2
        val maxLabelWidth = rows.maxOf { it.labelWidth }

        DrawableHelper.fill(matrixStack,
            left - 6, top - 6,
            left + width + 6, top + height + 6,
            Color.lightGray.rgb
        )
        DrawableHelper.fill(matrixStack,
            left - 5, top - 5,
            left + width + 5, top + height + 5,
            Color.black.rgb
        )

        val relX = mousePos.x - left
        val relY = mousePos.y - top

        val hoveredIndex = if (relX < 0 || relX > width) -1 else (relY / itemHeight).toInt()

        for (i in rows.indices) {
            val row = rows[i]
            val rowY = top + i * itemHeight
            if (i == hoveredIndex) {
                DrawableHelper.fill(matrixStack,
                    left - 1, rowY - 1,
                    left + width + 1, rowY + Client.textRenderer.fontHeight,
                    Color.darkGray.rgb
                )
            }

            Client.textRenderer.drawWithShadow(matrixStack,
                row.label,
                left.toFloat() + (maxLabelWidth - row.labelWidth),
                rowY.toFloat(),
                Color.WHITE.rgb
            )

            Client.textRenderer.drawWithShadow(matrixStack,
                row.stateText,
                left.toFloat() + maxLabelWidth + 2,
                rowY.toFloat(),
                Color.WHITE.rgb
            )
        }

        rows.getOrNull(hoveredIndex)?.also { row ->
            VanillaTooltipRenderer.renderTooltip(
                matrixStack,
                row.tooltip,
                left - 12,
                top + hoveredIndex * itemHeight + Client.textRenderer.fontHeight + 16
            )
        }
    }

    private sealed class OptionRow(val label: String, tooltip: String) {
        val tooltip: String = tooltip.trimIndent().replace("\\\n", "")

        var stateText: String = ""

        val labelWidth: Int
            get() = Client.textRenderer.getWidth(label)
        val stateWidth: Int
            get() = Client.textRenderer.getWidth(stateText)

        abstract fun click()
        abstract fun computeStateText()

        class BooleanRow(
            label: String,
            tooltip: String,
            val trueValue: String,
            val falseValue: String,
            val property: KMutableProperty0<Boolean>
        ): OptionRow(label, tooltip) {
            override fun click() {
                property.set(!property.get())
            }

            override fun computeStateText() {
                stateText = if (property.get()) trueValue else falseValue
            }
        }

    }
}