package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.client.sprite.TextWrapper
import com.teamwizardry.librarianlib.client.util.Color
import com.teamwizardry.librarianlib.common.util.event.EventBus
import com.teamwizardry.librarianlib.common.util.event.EventCancelable
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import java.util.*

open class ComponentMarkup(posX: Int, posY: Int, width: Int, height: Int) : GuiComponent<ComponentMarkup>(posX, posY, width, height) {

    class ElementClickEvent(val element: MarkupElement) : EventCancelable()

    val start = Option<ComponentMarkup, Int>(0)
    val end = Option<ComponentMarkup, Int>(Integer.MAX_VALUE)

    internal var elements: MutableList<MarkupElement> = ArrayList()

    init {
        BUS.hook(MouseOffsetEvent::class.java) { event ->
            event.offset = event.offset.sub(0.0, start.getValue(this).toDouble())
        }
        BUS.hook(MouseClickEvent::class.java) { event ->
            if (!event.component.mouseOver)
                return@hook
            for (element in elements) {
                if (element.isMouseOver(pos.xi, pos.yi)) {
                    if (element.BUS.hasHooks(ElementClickEvent::class.java) &&
                            !element.BUS.fire(ElementClickEvent(element)).isCanceled()) {
                        event.cancel()
                        break
                    }
                }
            }
        }
    }

    fun create(text: String): MarkupElement {
        var x = 0
        var y = 0
        if (elements.size > 0) {
            val prev = elements[elements.size - 1]
            x = prev.endX()
            y = prev.endY()
        }
        val element = MarkupElement(y, x, size.xi, text)
        elements.add(element)
        return element
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val start = this.start.getValue(this)
        val end = this.end.getValue(this)
        GlStateManager.translate(0f, (-start).toFloat(), 0f)
        for (element in elements) {
            if (element.posY >= start && element.posY <= end ||
                    element.posY + element.height() >= start && element.posY + element.height() <= end ||
                    element.posY <= start && element.posY + element.height() >= end)
                element.render(element.isMouseOver(mousePos.xi, mousePos.yi))
        }
        GlStateManager.translate(0f, start.toFloat(), 0f)
    }

    class MarkupElement(var posY: Int, var firstLineOffset: Int, width: Int, text: String) {
        val BUS = EventBus()

        val format = Option<Boolean, String>("")
        val color = Option<Boolean, Color>(Color.BLACK)
        val dropShadow = Option<Boolean, Boolean>(false)
        var lines: MutableList<String> = ArrayList()
        private val lengths: IntArray

        init {
            TextWrapper.wrap(Minecraft.getMinecraft().fontRendererObj, lines, text, firstLineOffset, width)
            lengths = IntArray(lines.size)
            for (i in lengths.indices) {
                lengths[i] = Minecraft.getMinecraft().fontRendererObj.getStringWidth(lines[i])
            }
        }

        fun render(hover: Boolean) {
            var i = 0
            for (line in lines) {
                drawLine(line, if (i == 0) firstLineOffset else 0, posY + i * Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, hover)
                i++
            }
        }

        protected fun drawLine(line: String, x: Int, y: Int, hover: Boolean) {
            Minecraft.getMinecraft().fontRendererObj.drawString(format.getValue(hover) + line, x.toFloat(), y.toFloat(), color.getValue(hover).hexARGB(), dropShadow.getValue(hover))
        }

        fun isMouseOver(x: Int, y: Int): Boolean {
            var yNew = y
            yNew -= posY
            val height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT
            for (i in lengths.indices) {
                val xPos = if (i == 0) firstLineOffset else 0
                if (yNew >= i * height && yNew < (i + 1) * height &&
                        x >= xPos && x < xPos + lengths[i]) {
                    return true
                }
            }
            return false
        }

        fun endX(): Int {
            return (if (lengths.size == 1) firstLineOffset else 0) + lengths[lengths.size - 1]
        }

        fun endY(): Int {
            return posY + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * (lines.size - 1)
        }

        fun height(): Int {
            return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * lines.size
        }
    }

}
