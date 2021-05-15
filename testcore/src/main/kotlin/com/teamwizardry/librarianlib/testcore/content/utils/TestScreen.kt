package com.teamwizardry.librarianlib.testcore.content.utils

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Style

public open class TestScreen(public val config: TestScreenConfig): Screen(LiteralText(config.title)) {
    public constructor(configure: TestScreenConfig.() -> Unit) : this(TestScreenConfig(configure))

    override fun shouldCloseOnEsc(): Boolean = config.closeOnEsc
    override fun isPauseScreen(): Boolean = config.pausesGame

    private val screenContext = TestScreenConfig.ScreenContext(this)
    private var left = 0.0
    private var top = 0.0

    override fun onClose() {
        config.onClose.run(screenContext)
        super.onClose()
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

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        RenderSystem.translated(left, top, 0.0)
        RenderSystem.scaled(config.scale.toDouble(), config.scale.toDouble(), 1.0)

        config.draw.run(TestScreenConfig.DrawContext(this, matrixStack, vec(mouseX - left, mouseY - top) / config.scale, partialTicks))

        RenderSystem.scaled(1 / config.scale.toDouble(), 1 / config.scale.toDouble(), 1.0)
        RenderSystem.translated(-left, -top, 0.0)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
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

    override fun mouseScrolled(x: Double, y: Double, amount: Double): Boolean {
        config.mouseScrolled.run(TestScreenConfig.MouseScrollContext(this, vec(x - left, y - top) / config.scale, amount))
        return super.mouseScrolled(x, y, amount)
    }

    override fun mouseMoved(x: Double, y: Double) {
        config.mouseMoved.run(TestScreenConfig.MouseMovedContext(this, vec(x - left, y - top) / config.scale))
        super.mouseMoved(x, y)
    }

    override fun mouseDragged(startX: Double, startY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        config.mouseDragged.run(TestScreenConfig.MouseDraggedContext(this, vec(startX - left, startY - top) / config.scale, vec(deltaX, deltaY) / config.scale, button))
        return super.mouseDragged(startX, startY, button, deltaX, deltaY)
    }

    // make these rendering helpers public

    public override fun drawHorizontalLine(matrices: MatrixStack, x1: Int, x2: Int, y: Int, color: Int) {
        super.drawHorizontalLine(matrices, x1, x2, y, color)
    }

    public override fun drawVerticalLine(matrices: MatrixStack, x: Int, y1: Int, y2: Int, color: Int) {
        super.drawVerticalLine(matrices, x, y1, y2, color)
    }

    public override fun fillGradient(
        matrices: MatrixStack,
        xStart: Int,
        yStart: Int,
        xEnd: Int,
        yEnd: Int,
        colorStart: Int,
        colorEnd: Int
    ) {
        super.fillGradient(matrices, xStart, yStart, xEnd, yEnd, colorStart, colorEnd)
    }

    public override fun renderTooltip(matrices: MatrixStack, stack: ItemStack, x: Int, y: Int) {
        super.renderTooltip(matrices, stack, x, y)
    }

    public override fun renderTextHoverEffect(matrices: MatrixStack, style: Style?, x: Int, y: Int) {
        super.renderTextHoverEffect(matrices, style, x, y)
    }
}