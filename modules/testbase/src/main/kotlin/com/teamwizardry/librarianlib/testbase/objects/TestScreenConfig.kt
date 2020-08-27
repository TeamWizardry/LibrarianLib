package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.sided.ClientSupplier
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemGroup
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class TestScreenConfig(val id: String, val name: String, activatorItemGroup: ItemGroup): TestConfig() {
    constructor(id: String, name: String, activatorItemGroup: ItemGroup, block: TestScreenConfig.() -> Unit): this(id, name, activatorItemGroup) {
        this.block()
    }

    override var description: String?
        get() = super.description
        set(value) {
            super.description = value
            activatorItem.config.description = value
        }

    var customScreen: ClientSupplier<Screen>? = null

    /**
     * Use an entirely custom screen
     */
    inline fun customScreen(crossinline client: () -> Screen) {
        customScreen = ClientSupplier { client() }
    }

    /**
     * The title of the screen, used for accessibility through screen readers
     */
    var title: String = name

    /**
     * The size of the GUI pane. The origin point of the GUI will be placed in the top-left corner of a centered
     * rectangle of the given size.
     */
    var size: Vec2d = vec(0, 0)

    /**
     * The scaling factor of the GUI pane.
     */
    var scale: Int = 1

    /**
     * Whether the screen should be closed when pressing the escape key
     */
    var closeOnEsc: Boolean = true

    /**
     * Whether the screen should pause the game while it is open
     */
    var pausesGame: Boolean = true

    /**
     * Called when the screen first opens and when the game window is resized
     */
    val init = ClientAction<ScreenContext>()

    /**
     * Called when the screen is about to close itself.
     */
    val onClose = ClientAction<ScreenContext>()

    /**
     * Called before the screen has been removed from the [Minecraft] instance. Similar to [onClose] except it runs
     * even when the screen is being replaced by another screen
     */
    val onRemoved = ClientAction<ScreenContext>()

    /**
     * Called each frame to draw the screen
     */
    val draw = ClientAction<DrawContext>()

    /**
     * Called each client tick while the screen is open
     */
    val tick = ClientAction<ScreenContext>()

    val mouseClicked = ClientAction<MouseButtonContext>()

    val mouseReleased = ClientAction<MouseButtonContext>()

    val mouseScrolled = ClientAction<MouseScrollContext>()

    val mouseMoved = ClientAction<MouseMovedContext>()

    val mouseDragged = ClientAction<MouseDraggedContext>()

    val charTyped = ClientAction<CharContext>()

    val keyPressed = ClientAction<KeyContext>()

    val keyReleased = ClientAction<KeyContext>()

    private val lazies = mutableListOf<() -> Unit>()

    /**
     * Run the passed configuration block lazily the first time the screen is used.
     */
    fun lazyConfig(block: () -> Unit) {
        lazies.add(block)
    }

    @Suppress("NOTHING_TO_INLINE")
    @OnlyIn(Dist.CLIENT)
    data class DrawContext(val screen: TestScreen, val mousePos: Vec2d, val partialTicks: Float): TestContext() {
        fun hLine(left: Int, right: Int, y: Int, color: Int) =
            screen.hLine(left, right, y, color)
        inline fun hLine(left: Int, right: Int, y: Int, color: UInt) =
            hLine(left, right, y, color.toInt())

        fun vLine(x: Int, top: Int, bottom: Int, color: Int) =
            screen.vLine(x,  top, bottom, color)
        inline fun vLine(x: Int, top: Int, bottom: Int, color: UInt) =
            vLine(x,  top, bottom, color.toInt())

        fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: Int) =
            screen.fillGradient(minX, minY, maxX, maxY, topColor, bottomColor)
        inline fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: UInt) =
            fillGradient(minX, minY, maxX, maxY, topColor, bottomColor.toInt())

        fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: Int) =
            AbstractGui.fill(minX, minY, maxX, maxY, color)
        inline fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: UInt) =
            fill(minX, minY, maxX, maxY, color.toInt())

        fun drawCenteredString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) =
            screen.drawCenteredString(fr, text, x, y, color)
        inline fun drawCenteredString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) =
            drawCenteredString(fr, text, x, y, color.toInt())

        fun drawRightAlignedString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) =
            screen.drawRightAlignedString(fr, text, x, y, color)
        inline fun drawRightAlignedString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) =
            drawRightAlignedString(fr, text, x, y, color.toInt())

        fun drawString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) =
            screen.drawString(fr, text, x, y, color)
        inline fun drawString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) =
            drawString(fr, text, x, y, color.toInt())

        fun blit(x: Int, y: Int, z: Int, width: Int, height: Int, sprite: TextureAtlasSprite) =
            AbstractGui.blit(x, y, z, width, height, sprite)

        fun blit(p_blit_1_: Int, p_blit_2_: Int, p_blit_3_: Int, p_blit_4_: Int, p_blit_5_: Int, p_blit_6_: Int) =
            screen.blit(p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_)

        fun blit(x: Int, y: Int, z: Int, p_blit_3_: Float, p_blit_4_: Float, width: Int, height: Int, p_blit_7_: Int, p_blit_8_: Int) =
            AbstractGui.blit(x, y, z, p_blit_3_, p_blit_4_, width, height, p_blit_7_, p_blit_8_)

        fun blit(p_blit_0_: Int, p_blit_1_: Int, p_blit_2_: Int, p_blit_3_: Int, p_blit_4_: Float, p_blit_5_: Float, p_blit_6_: Int, p_blit_7_: Int, p_blit_8_: Int, p_blit_9_: Int) =
            AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_, p_blit_8_, p_blit_9_)

        fun blit(p_blit_0_: Int, p_blit_1_: Int, p_blit_2_: Float, p_blit_3_: Float, p_blit_4_: Int, p_blit_5_: Int, p_blit_6_: Int, p_blit_7_: Int) =
            AbstractGui.blit(p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_)

    }
    @OnlyIn(Dist.CLIENT)
    data class ScreenContext(val screen: TestScreen): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class MouseButtonContext(val screen: TestScreen, val mousePos: Vec2d, val button: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class MouseScrollContext(val screen: TestScreen, val mousePos: Vec2d, val amount: Double): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class MouseMovedContext(val screen: TestScreen, val mousePos: Vec2d): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class MouseDraggedContext(val screen: TestScreen, val startPos: Vec2d, val delta: Vec2d, val button: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class CharContext(val screen: TestScreen, val character: Char, val modifiers: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    data class KeyContext(val screen: TestScreen, val key: Int, val scanCode: Int, val modifiers: Int): TestContext()


    @OnlyIn(Dist.CLIENT)
    fun activate() {
        lazies.forEach { it() }
        lazies.clear()
        Client.displayGuiScreen(customScreen?.get() ?: TestScreen(this))
    }

    var activatorItem = TestItem(TestItemConfig(this.id + "_screen", this.name + " Screen", activatorItemGroup) {
        client {
            rightClick {
                activate()
            }
        }
    })
}