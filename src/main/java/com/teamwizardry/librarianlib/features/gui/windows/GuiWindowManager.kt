package com.teamwizardry.librarianlib.features.gui.windows

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.gui.GuiScreen
import java.util.Collections

class GuiWindowManager(private val existingScreen: GuiScreen?): GuiBase() {
    private var focusedWindow: GuiWindow? = null
    private val delegate = WindowManagerDelegate()

    init {
        main.isVisible = false
        main.zIndex = Double.NEGATIVE_INFINITY
    }

    fun open(window: GuiWindow) {
        delegate.open(window)
    }

    // make it unreasonably difficult to get at the GuiWindowManager, as it should not be touched.
    private inner class WindowManagerDelegate: IWindowManager {
        override val windows: List<GuiWindow>
            get() = root.children.filterIsInstance<GuiWindow>()

        override fun isFocused(window: GuiWindow) = window == focusedWindow

        override fun requestFocus(window: GuiWindow): Boolean {
            if(focusedWindow == window) return true
            focusedWindow?.BUS?.fire(GuiWindow.LoseFocusEvent())
            focusedWindow = window
            window.BUS.fire(GuiWindow.GainFocusEvent())
            sort()
            return true
        }

        override fun close(window: GuiWindow) {
            window.windowManager = null
            root.remove(window)
            if(focusedWindow == window) {
                val top = windows.lastOrNull { !it.isFloating }
                top?.also { requestFocus(it) }
            }
            window.BUS.fire(GuiWindow.CloseEvent())
            if(windows.isEmpty()) {
                Minecraft().displayGuiScreen(existingScreen)
            }
        }

        override fun open(window: GuiWindow) {
            focusedWindow = window
            window.windowManager = this
            root.add(window)
            sort()
            window.BUS.fire(GuiWindow.OpenEvent())
        }

        override fun sort() {
            val sorted = windows.toMutableList()
            val focusedWindow = focusedWindow
            if(focusedWindow != null) {
                if(sorted.remove(focusedWindow))
                    sorted.add(focusedWindow)
            }
            sorted.sortBy { it.isFloating }
            sorted.forEachIndexed { i, it -> it.zIndex = i.toDouble() }
        }
    }
}

interface IWindowManager {
    /**
     * An unmodifiable list of the windows in the window manager in back-to-front order
     */
    val windows: List<GuiWindow>

    /**
     * Returns true if the passed window is currently focused
     */
    fun isFocused(window: GuiWindow): Boolean
    fun requestFocus(window: GuiWindow): Boolean
    fun close(window: GuiWindow)
    fun open(window: GuiWindow)
    fun sort()
}
