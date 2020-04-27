package com.teamwizardry.librarianlib.testbase.objects

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.SimpleCoordinateSpace2D
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

class TestScreen(val config: TestScreenConfig): Screen(StringTextComponent(config.title)) {

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

    override fun removed() {
        config.onRemoved.run(screenContext)
        super.removed()
    }

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        RenderSystem.translated(left, top, 0.0)
        RenderSystem.scaled(config.scale.toDouble(), config.scale.toDouble(), 1.0)

        config.draw.run(TestScreenConfig.DrawContext(this, vec(mouseX - left, mouseY - top) / config.scale, partialTicks))

        RenderSystem.scaled(1/config.scale.toDouble(), 1/config.scale.toDouble(), 1.0)
        RenderSystem.translated(-left, -top, 0.0)
        super.render(mouseX, mouseY, partialTicks)
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

    public override fun renderComponentHoverEffect(p_renderComponentHoverEffect_1_: ITextComponent, p_renderComponentHoverEffect_2_: Int, p_renderComponentHoverEffect_3_: Int) {
        super.renderComponentHoverEffect(p_renderComponentHoverEffect_1_, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_)
    }

    public override fun renderTooltip(p_renderTooltip_1_: ItemStack, p_renderTooltip_2_: Int, p_renderTooltip_3_: Int) {
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_)
    }

    public override fun vLine(p_vLine_1_: Int, p_vLine_2_: Int, p_vLine_3_: Int, p_vLine_4_: Int) {
        super.vLine(p_vLine_1_, p_vLine_2_, p_vLine_3_, p_vLine_4_)
    }

    public override fun hLine(p_hLine_1_: Int, p_hLine_2_: Int, p_hLine_3_: Int, p_hLine_4_: Int) {
        super.hLine(p_hLine_1_, p_hLine_2_, p_hLine_3_, p_hLine_4_)
    }

    public override fun fillGradient(p_fillGradient_1_: Int, p_fillGradient_2_: Int, p_fillGradient_3_: Int, p_fillGradient_4_: Int, p_fillGradient_5_: Int, p_fillGradient_6_: Int) {
        super.fillGradient(p_fillGradient_1_, p_fillGradient_2_, p_fillGradient_3_, p_fillGradient_4_, p_fillGradient_5_, p_fillGradient_6_)
    }

}