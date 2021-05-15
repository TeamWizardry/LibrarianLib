package com.teamwizardry.librarianlib.testcore.content.utils

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.testcore.util.Action
import com.teamwizardry.librarianlib.testcore.util.TestContext
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack

public class TestScreenConfig private constructor() {
    public constructor(configure: TestScreenConfig.() -> Unit) : this() {
        this.configure()
    }

    public var title: String = ""
    /**
     * The size of the GUI pane. The origin point of the GUI will be placed in the top-left corner of a centered
     * rectangle of the given size.
     */
    public var size: Vec2d = vec(0, 0)

    /**
     * The scaling factor of the GUI pane.
     */
    public var scale: Int = 1

    /**
     * Whether the screen should be closed when pressing the escape key
     */
    public var closeOnEsc: Boolean = true

    /**
     * Whether the screen should pause the game while it is open
     */
    public var pausesGame: Boolean = true

    /**
     * Called when the screen first opens and when the game window is resized
     */
    public val init: Action<ScreenContext> = Action()

    /**
     * Called when the screen is about to close itself.
     */
    public val onClose: Action<ScreenContext> = Action<ScreenContext>()

    /**
     * Called each frame to draw the screen
     */
    public val draw: Action<DrawContext> = Action<DrawContext>()

    /**
     * Called each client tick while the screen is open
     */
    public val tick: Action<ScreenContext> = Action<ScreenContext>()

    public val mouseClicked: Action<MouseButtonContext> = Action<MouseButtonContext>()

    public val mouseReleased: Action<MouseButtonContext> = Action<MouseButtonContext>()

    public val mouseScrolled: Action<MouseScrollContext> = Action<MouseScrollContext>()

    public val mouseMoved: Action<MouseMovedContext> = Action<MouseMovedContext>()

    public val mouseDragged: Action<MouseDraggedContext> = Action<MouseDraggedContext>()

    public val charTyped: Action<CharContext> = Action<CharContext>()

    public val keyPressed: Action<KeyContext> = Action<KeyContext>()

    public val keyReleased: Action<KeyContext> = Action<KeyContext>()

    @Suppress("NOTHING_TO_INLINE")
    public data class DrawContext(val screen: TestScreen, val matrix: MatrixStack, val mousePos: Vec2d, val partialTicks: Float): TestContext() {

        public fun drawHorizontalLine(left: Int, right: Int, y: Int, color: Int) {
            screen.drawHorizontalLine(matrix, left, right, y, color)
        }
        public inline fun drawHorizontalLine(left: Int, right: Int, y: Int, color: UInt) {
            screen.drawHorizontalLine(matrix, left, right, y, color.toInt())
        }

        public fun drawVerticalLine(x: Int, top: Int, bottom: Int, color: Int) {
            screen.drawVerticalLine(matrix, x, top, bottom, color)
        }
        public inline fun drawVerticalLine(x: Int, top: Int, bottom: Int, color: UInt) {
            screen.drawVerticalLine(matrix, x, top, bottom, color.toInt())
        }

        public fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: Int) {
            screen.fillGradient(matrix, minX, minY, maxX, maxY, topColor, bottomColor)
        }
        public inline fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: UInt) {
            fillGradient(minX, minY, maxX, maxY, topColor, bottomColor.toInt())
        }

        public fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: Int) {
            DrawableHelper.fill(matrix, minX, minY, maxX, maxY, color)
        }
        public inline fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: UInt) {
            fill(minX, minY, maxX, maxY, color.toInt())
        }

        public fun drawSprite(x: Int, y: Int, z: Int, width: Int, height: Int, sprite: Sprite) {
            DrawableHelper.drawSprite(matrix, x, y, z, width, height, sprite)
        }

        public fun drawTexture(matrices: MatrixStack, x: Int, y: Int, u: Int, v: Int, width: Int, height: Int) {
            screen.drawTexture(matrices, x, y, u, v, width, height)
        }

        public fun drawTexture(
            matrices: MatrixStack,
            x: Int,
            y: Int,
            z: Int,
            u: Float,
            v: Float,
            width: Int,
            height: Int,
            textureHeight: Int,
            textureWidth: Int
        ) {
            DrawableHelper.drawTexture(matrices, x, y, z, u, v, width, height, textureHeight, textureWidth)
        }

        public fun drawTexture(
            matrices: MatrixStack,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            u: Float,
            v: Float,
            regionWidth: Int,
            regionHeight: Int,
            textureWidth: Int,
            textureHeight: Int
        ) {
            DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight)
        }

        public fun drawTexture(
            matrices: MatrixStack,
            x: Int,
            y: Int,
            u: Float,
            v: Float,
            width: Int,
            height: Int,
            textureWidth: Int,
            textureHeight: Int
        ) {
            DrawableHelper.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight)
        }
    }
    public data class ScreenContext(val screen: TestScreen): TestContext()
    public data class MouseButtonContext(val screen: TestScreen, val mousePos: Vec2d, val button: Int): TestContext()
    public data class MouseScrollContext(val screen: TestScreen, val mousePos: Vec2d, val amount: Double): TestContext()
    public data class MouseMovedContext(val screen: TestScreen, val mousePos: Vec2d): TestContext()
    public data class MouseDraggedContext(val screen: TestScreen, val startPos: Vec2d, val delta: Vec2d, val button: Int): TestContext()
    public data class CharContext(val screen: TestScreen, val character: Char, val modifiers: Int): TestContext()
    public data class KeyContext(val screen: TestScreen, val key: Int, val scanCode: Int, val modifiers: Int): TestContext()
}