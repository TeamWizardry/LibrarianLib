package com.teamwizardry.librarianlib.testbase.objects

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemGroup
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

public class TestScreenConfig(public val id: String, public val name: String, activatorItemGroup: ItemGroup): TestConfig() {
    public constructor(id: String, name: String, activatorItemGroup: ItemGroup, block: TestScreenConfig.() -> Unit): this(id, name, activatorItemGroup) {
        this.block()
    }

    override var description: String?
        get() = super.description
        set(value) {
            super.description = value
            activatorItem.config.description = value
        }

    public var customScreen: ClientMetaSupplier<ScreenFactory>? = null

    /**
     * Use an entirely custom screen
     */
    public inline fun customScreen(crossinline client: () -> Screen) {
        customScreen = ClientMetaSupplier { ScreenFactory { client() } }
    }

    /**
     * The title of the screen, used for accessibility through screen readers
     */
    public var title: String = name

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
    public val init: ClientAction<ScreenContext> = ClientAction()

    /**
     * Called when the screen is about to close itself.
     */
    public val onClose: ClientAction<ScreenContext> = ClientAction<ScreenContext>()

    /**
     * Called each frame to draw the screen
     */
    public val draw: ClientAction<DrawContext> = ClientAction<DrawContext>()

    /**
     * Called each client tick while the screen is open
     */
    public val tick: ClientAction<ScreenContext> = ClientAction<ScreenContext>()

    public val mouseClicked: ClientAction<MouseButtonContext> = ClientAction<MouseButtonContext>()

    public val mouseReleased: ClientAction<MouseButtonContext> = ClientAction<MouseButtonContext>()

    public val mouseScrolled: ClientAction<MouseScrollContext> = ClientAction<MouseScrollContext>()

    public val mouseMoved: ClientAction<MouseMovedContext> = ClientAction<MouseMovedContext>()

    public val mouseDragged: ClientAction<MouseDraggedContext> = ClientAction<MouseDraggedContext>()

    public val charTyped: ClientAction<CharContext> = ClientAction<CharContext>()

    public val keyPressed: ClientAction<KeyContext> = ClientAction<KeyContext>()

    public val keyReleased: ClientAction<KeyContext> = ClientAction<KeyContext>()

    private val lazies = mutableListOf<() -> Unit>()

    /**
     * Run the passed configuration block lazily the first time the screen is used.
     */
    public fun lazyConfig(block: () -> Unit) {
        lazies.add(block)
    }

    @Suppress("NOTHING_TO_INLINE")
    @OnlyIn(Dist.CLIENT)
    public data class DrawContext(val screen: TestScreen, val matrix: MatrixStack, val mousePos: Vec2d, val partialTicks: Float): TestContext() {
        public fun hLine(left: Int, right: Int, y: Int, color: Int) {
            screen.hLine(matrix, left, right, y, color)
        }
        public inline fun hLine(left: Int, right: Int, y: Int, color: UInt) {
            hLine(left, right, y, color.toInt())
        }

        public fun vLine(x: Int, top: Int, bottom: Int, color: Int) {
            screen.vLine(matrix, x,  top, bottom, color)
        }
        public inline fun vLine(x: Int, top: Int, bottom: Int, color: UInt) {
            vLine(x,  top, bottom, color.toInt())
        }

        public fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: Int) {
            screen.fillGradient(matrix, minX, minY, maxX, maxY, topColor, bottomColor)
        }
        public inline fun fillGradient(minX: Int, minY: Int, maxX: Int, maxY: Int, topColor: Int, bottomColor: UInt) {
            fillGradient(minX, minY, maxX, maxY, topColor, bottomColor.toInt())
        }

        public fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: Int) {
            AbstractGui.fill(matrix, minX, minY, maxX, maxY, color)
        }
        public inline fun fill(minX: Int, minY: Int, maxX: Int, maxY: Int, color: UInt) {
            fill(minX, minY, maxX, maxY, color.toInt())
        }

        // method no longer exists in Screen
//        public fun drawCenteredString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) {
//            screen.drawCenteredString(matrix, fr, text, x, y, color)
//        }
//        public inline fun drawCenteredString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) {
//            drawCenteredString(fr, text, x, y, color.toInt())
//        }
//
//        public fun drawRightAlignedString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) {
//            screen.drawRightAlignedString(fr, text, x, y, color)
//        }
//        public inline fun drawRightAlignedString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) {
//            drawRightAlignedString(fr, text, x, y, color.toInt())
//        }
//
//        public fun drawString(fr: FontRenderer, text: String, x: Int, y: Int, color: Int) {
//            screen.drawString(fr, text, x, y, color)
//        }
//        public inline fun drawString(fr: FontRenderer, text: String, x: Int, y: Int, color: UInt) {
//            drawString(fr, text, x, y, color.toInt())
//        }

        public fun blit(x: Int, y: Int, z: Int, width: Int, height: Int, sprite: TextureAtlasSprite) {
            AbstractGui.blit(matrix, x, y, z, width, height, sprite)
        }

        public fun blit(p_blit_1_: Int, p_blit_2_: Int, p_blit_3_: Int, p_blit_4_: Int, p_blit_5_: Int, p_blit_6_: Int) {
            screen.blit(matrix, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_)
        }

        public fun blit(x: Int, y: Int, z: Int, p_blit_3_: Float, p_blit_4_: Float, width: Int, height: Int, p_blit_7_: Int, p_blit_8_: Int) {
            AbstractGui.blit(matrix, x, y, z, p_blit_3_, p_blit_4_, width, height, p_blit_7_, p_blit_8_)
        }

        public fun blit(p_blit_0_: Int, p_blit_1_: Int, p_blit_2_: Int, p_blit_3_: Int, p_blit_4_: Float, p_blit_5_: Float, p_blit_6_: Int, p_blit_7_: Int, p_blit_8_: Int, p_blit_9_: Int) {
            AbstractGui.blit(matrix, p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_, p_blit_8_, p_blit_9_)
        }

        public fun blit(p_blit_0_: Int, p_blit_1_: Int, p_blit_2_: Float, p_blit_3_: Float, p_blit_4_: Int, p_blit_5_: Int, p_blit_6_: Int, p_blit_7_: Int) {
            AbstractGui.blit(matrix, p_blit_0_, p_blit_1_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_)
        }

    }
    @OnlyIn(Dist.CLIENT)
    public data class ScreenContext(val screen: TestScreen): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class MouseButtonContext(val screen: TestScreen, val mousePos: Vec2d, val button: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class MouseScrollContext(val screen: TestScreen, val mousePos: Vec2d, val amount: Double): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class MouseMovedContext(val screen: TestScreen, val mousePos: Vec2d): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class MouseDraggedContext(val screen: TestScreen, val startPos: Vec2d, val delta: Vec2d, val button: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class CharContext(val screen: TestScreen, val character: Char, val modifiers: Int): TestContext()
    @OnlyIn(Dist.CLIENT)
    public data class KeyContext(val screen: TestScreen, val key: Int, val scanCode: Int, val modifiers: Int): TestContext()


    @OnlyIn(Dist.CLIENT)
    public fun activate() {
        lazies.forEach { it() }
        lazies.clear()
        Client.displayGuiScreen(customScreen?.getClientFunction()?.create() ?: TestScreen(this))
    }

    public var activatorItem: TestItem = TestItem(TestItemConfig(this.id + "_screen", this.name + " Screen", activatorItemGroup) {
        client {
            rightClick {
                activate()
            }
        }
    })
}

public fun interface ScreenFactory: ClientSideFunction {
    public fun create(): Screen
}
