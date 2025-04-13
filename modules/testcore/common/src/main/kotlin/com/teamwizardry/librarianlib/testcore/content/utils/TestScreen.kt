package com.teamwizardry.librarianlib.testcore.content.utils

import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

public open class TestScreen(public val config: TestScreenConfig): Screen(Text.literal(config.title)) {
    public constructor(configure: TestScreenConfig.() -> Unit) : this(TestScreenConfig(configure))

    override fun shouldCloseOnEsc(): Boolean = config.closeOnEsc
    override fun shouldPause(): Boolean = config.pausesGame

    private val screenContext = TestScreenConfig.ScreenContext(this)
    private var left = 0.0
    private var top = 0.0

    override fun close() {
        config.onClose.run(screenContext)
        super.close()
    }

    override fun init() {
        this.left = (width - config.size.x * config.scale) / 2
        this.top = (height - config.size.y * config.scale) / 2
        config.init.run(screenContext)
        super.init()
    }

    override fun tick() {
        config.tick.run(screenContext)
        super.tick()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        context.matrices.push()
        context.matrices.translate(left, top, 0.0)
        context.matrices.scale(config.scale.toFloat(), config.scale.toFloat(), 1f)

        config.draw.run(TestScreenConfig.RenderContext(this, context, vec(mouseX - left, mouseY - top) / config.scale, delta))

        context.matrices.pop()
    }

    override fun charTyped(character: Char, modifiers: Int): Boolean {
        config.charTyped.run(TestScreenConfig.CharContext(this, character, modifiers))
        return super.charTyped(character, modifiers)
    }

    override fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        config.keyPressed.run(TestScreenConfig.KeyContext(this, key, scanCode, modifiers))
        return super.keyPressed(key, scanCode, modifiers)
    }

    override fun keyReleased(key: Int, scanCode: Int, modifiers: Int): Boolean {
        config.keyReleased.run(TestScreenConfig.KeyContext(this, key, scanCode, modifiers))
        return super.keyReleased(key, scanCode, modifiers)
    }

    override fun mouseClicked(x: Double, y: Double, button: Int): Boolean {
        config.mouseClicked.run(TestScreenConfig.MouseButtonContext(this, vec(x - left, y - top) / config.scale, button))
        return super.mouseClicked(x, y, button)
    }

    override fun mouseReleased(x: Double, y: Double, button: Int): Boolean {
        config.mouseReleased.run(TestScreenConfig.MouseButtonContext(this, vec(x - left, y - top) / config.scale, button))
        return super.mouseReleased(x, y, button)
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        config.mouseScrolled.run(
            TestScreenConfig.MouseScrollContext(
                this,
                vec(mouseX - left, mouseX - top) / config.scale,
                verticalAmount,
                horizontalAmount
            )
        )
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun mouseMoved(x: Double, y: Double) {
        config.mouseMoved.run(TestScreenConfig.MouseMovedContext(this, vec(x - left, y - top) / config.scale))
        super.mouseMoved(x, y)
    }

    override fun mouseDragged(startX: Double, startY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        config.mouseDragged.run(TestScreenConfig.MouseDraggedContext(this, vec(startX - left, startY - top) / config.scale, vec(deltaX, deltaY) / config.scale, button))
        return super.mouseDragged(startX, startY, button, deltaX, deltaY)
    }
}