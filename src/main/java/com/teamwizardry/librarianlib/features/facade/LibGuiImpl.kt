package com.teamwizardry.librarianlib.features.facade

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.features.facade.layers.GradientLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastrySwitch
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryToggle
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.delegate
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color
import java.io.IOException
import kotlin.reflect.KMutableProperty0

open class LibGuiImpl(
    protected val guiWidth: () -> Int,
    protected val guiHeight: () -> Int,
    catchSafetyNet: (e: Exception) -> Unit
) {
    val root: StandaloneRootComponent = StandaloneRootComponent(catchSafetyNet)
    val safetyNet: Boolean by root::safetyNet.delegate
    val main: GuiComponent = object: GuiComponent(0, 0) {
        override var pos: Vec2d
            get() = vec(root.size.xi/2, root.size.yi/2)
            set(value) {}
        override var anchor: Vec2d
            get() = vec(0.5, 0.5)
            set(value) {}

        override fun layoutChildren() {
            val parentSize = this.parent!!.size - vec(20, 20)
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val maxScale = scaledResolution.scaleFactor
            var scale = 1
            while((size.x / scale > parentSize.x || size.y / scale > parentSize.y) && scale < maxScale) {
                scale++
            }
            this.scale = 1.0/scale
        }
    }
    private val background = GradientLayer(Axis2d.Y,
        Color(0x10, 0x10, 0x10, 0xC0),
        Color(0x10, 0x10, 0x10, 0xD0),
        0, 0, 0, 0
    )
    private val debugDialog = DebugDialogComponent()
    var useDefaultBackground = false
        set(value) {
            field = value
            if(value && background.parent == null) {
                root.add(background)
            } else if(!value && background.parent != null) {
                root.remove(background)
            }
        }
    /**
     * Whether to cloes the GUI when the escape key is pressed
     */
    var escapeClosesGUI: Boolean = true
    /**
     * The GUI to reopen when closing this GUI.
     */
    var lastGui: GuiScreen? = null

    init {
        background.zIndex = Double.NEGATIVE_INFINITY
        main.disableMouseCollision = true
        root.disableMouseCollision = true
        root.add(main)

        root.add(debugDialog)
        debugDialog.zIndex = GuiLayer.DIALOG_Z
        debugDialog.isVisible = false

        initGui()
    }

    fun initGui() {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val resolution = vec(scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        root.size_rm.set(resolution) //
        background.size = resolution
        main.setNeedsLayout()
        Keyboard.enableRepeatEvents(true)
    }

    fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val scaleFactor = ScaledResolution(Minecraft.getMinecraft()).scaleFactor
        GlStateManager.enableBlend()
        // convert logical to real position
        val relPos = vec(mouseX*scaleFactor, mouseY*scaleFactor)
        GlStateManager.pushMatrix()

        root.renderRoot(partialTicks, relPos)

        GlStateManager.popMatrix()
    }

    @Throws(IOException::class)
    fun mouseClicked(button: Int) {
        root.mouseDown(EnumMouseButton.getFromCode(button))
    }

    fun mouseReleased(button: Int) {
        root.mouseUp(EnumMouseButton.getFromCode(button))
    }

    fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1 && escapeClosesGUI) {
            if(debugDialog.isVisible) {
                debugDialog.isVisible = false
            } else {
                Minecraft.getMinecraft().displayGuiScreen(lastGui)

                if (Minecraft.getMinecraft().currentScreen == null) {
                    Minecraft.getMinecraft().setIngameFocus()
                }
            }
        }
    }

    @Throws(IOException::class)
    fun handleKeyboardInput() {
        if (LibrarianLib.DEV_ENVIRONMENT && Keyboard.getEventKeyState()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_D && GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                debugDialog.isVisible = !debugDialog.isVisible
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                GuiLayer.showDebugBoundingBox = !GuiLayer.showDebugBoundingBox
            }
        }

        if(Keyboard.isRepeatEvent()) {
            root.keyRepeat(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else if (Keyboard.getEventKeyState()) {
            root.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        } else {
            root.keyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey())
        }
    }

    @Throws(IOException::class)
    fun handleMouseInput() {
        val wheelAmount = Mouse.getEventDWheel()

        var mouseWheelModifier: Double = -16.0/360
        if (wheelAmount != 0) {
            root.mouseWheel(wheelAmount / mouseWheelModifier)
        }
    }

    fun update() {
        root.update()
    }

    fun tick() {
        root.tick()
    }

    fun onClose() {
        Keyboard.enableRepeatEvents(false)
        Mouse.setNativeCursor(null)
    }

    companion object {
        /**
         * The LWJGL mouse wheel -> pixel modifier.
         */
        internal var mouseWheelModifier: Double = -16.0/360
    }

    @UseExperimental(ExperimentalBitfont::class)
    private class DebugDialogComponent : GuiComponent() {
        override var pos: Vec2d
            get() = vec(root.size.xi/2, root.size.yi/2)
            set(value) {}
        override var anchor: Vec2d
            get() = vec(0.5, 0.5)
            set(value) {}

        val bg = PastryBackground()
        val contents = StackLayout.build()
            .vertical().alignTop().alignLeft()
            .add(ToggleRow(
                "Bounding boxes",
                """
                    Show bounding boxes of layers and components.
                    
                    • Layers have thin bounding boxes, components have thick ones
                    • Components with the mouse over them have white bounding boxes
                    • When holding Shift, components will draw a line from their origin to the mouse cursor
                """.trimIndent(),
                GuiLayer.Companion::showDebugBoundingBox
            ))
            .add(ToggleRow(
                "Visualize layoutChildren",
                """
                    Display a translucent red overlay on layers that were just laid out. Useful to identify layers \
                    that are causing excessive layouts
                """.trimIndent(),
                GuiLayer.Companion::showLayoutOverlay
            ))
            .add(ToggleRow(
                "Tilt",
                """
                    Tilt the UI slightly to make the Z axis visible.
                """.trimIndent(),
                GuiLayer.Companion::showDebugTilt
            ))
            .fit()
            .component()

        init {
            this.add(bg, contents)
            val margin = vec(6, 3)
            this.size = contents.size + margin * 2
            contents.pos = margin
            bg.frame = this.bounds
        }

        @Hook
        fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
            if(this.mouseHit == null)
                this.isVisible = false
        }

        private class ToggleRow(
            name: String,
            description: String,
            val property: KMutableProperty0<Boolean>
        ) : GuiComponent() {
            val switch = PastrySwitch()
            val label = PastryLabel(name)
            val contents = StackLayout.build()
                .horizontal().alignLeft().alignCenterY()
                .space(3)
                .add(switch, label).fit()
                .component()
            private var currentPropertyState = property.get()

            init {
                this.add(contents)
                this.size = contents.size

                switch.state = property.get()
                switch.hook<PastryToggle.StateChangedEvent> {
                    property.set(switch.state)
                    currentPropertyState = property.get()
                }

                this.tooltipText = description
            }

            @Hook
            fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
                if(property.get() != currentPropertyState) {
                    switch.state = property.get()
                    currentPropertyState = property.get()
                }
            }
        }
    }
}
