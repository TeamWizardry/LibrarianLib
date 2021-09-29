package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.*

public abstract class InputLayout {
    public val ctrl: Boolean
        get() = InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_LEFT_CONTROL) ||
                InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_RIGHT_CONTROL)
    public val alt: Boolean
        get() = InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_LEFT_ALT) ||
                InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_RIGHT_ALT)
    public val shift: Boolean
        get() = InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_LEFT_SHIFT) ||
                InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_RIGHT_SHIFT)
    public val superkey: Boolean
        get() = InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_LEFT_SUPER) ||
                InputUtil.isKeyPressed(Client.window.handle, GLFW_KEY_RIGHT_SUPER)

    public abstract fun isSelectModifierDown(): Boolean
    public abstract fun isSelectAll(keyCode: Int): Boolean
    public abstract fun isSelectNone(keyCode: Int): Boolean
    public abstract fun isCopy(keyCode: Int): Boolean
    public abstract fun isPaste(keyCode: Int): Boolean
    public abstract fun isCut(keyCode: Int): Boolean
    public abstract fun jumpType(): JumpType

    public enum class JumpType {
        CHARACTER, WORD, LINE
    }

    public companion object {
        @JvmStatic
        public var system: InputLayout = if(MinecraftClient.IS_SYSTEM_MAC) MacInputLayout else WindowsInputLayout
    }
}

public object MacInputLayout : InputLayout() {
    public val cmd: Boolean get() = superkey

    override fun isSelectModifierDown(): Boolean = shift

    override fun isSelectAll(keyCode: Int): Boolean = modifiers(cmd = true) && keyCode == GLFW_KEY_A
    override fun isSelectNone(keyCode: Int): Boolean = modifiers(shift = true, cmd = true) && keyCode == GLFW_KEY_A

    override fun isCopy(keyCode: Int): Boolean = modifiers(cmd = true) && keyCode == GLFW_KEY_C
    override fun isPaste(keyCode: Int): Boolean = modifiers(cmd = true) && keyCode == GLFW_KEY_V
    override fun isCut(keyCode: Int): Boolean = modifiers(cmd = true) && keyCode == GLFW_KEY_X

    override fun jumpType(): JumpType = when {
        (ctrl || cmd) && !alt -> JumpType.LINE
        alt && !ctrl && !cmd -> JumpType.WORD
        else -> JumpType.CHARACTER
    }

    private fun modifiers(
        ctrl: Boolean? = false,
        alt: Boolean? = false,
        shift: Boolean? = false,
        cmd: Boolean? = false
    ): Boolean = (ctrl == null || ctrl == this.ctrl) && (alt == null || alt == this.alt) &&
            (shift == null || shift == this.shift) && (cmd == null || cmd == this.cmd)
}

public object WindowsInputLayout : InputLayout() {
    public val win: Boolean get() = superkey

    override fun isSelectModifierDown(): Boolean = shift

    override fun isSelectAll(keyCode: Int): Boolean = modifiers(ctrl = true) && keyCode == GLFW_KEY_A
    override fun isSelectNone(keyCode: Int): Boolean = false // there is no standard "select none" shortcut on Windows

    override fun isCopy(keyCode: Int): Boolean = modifiers(ctrl = true) && keyCode == GLFW_KEY_C ||
            modifiers(ctrl = true) && keyCode == GLFW_KEY_INSERT
    override fun isPaste(keyCode: Int): Boolean = modifiers(ctrl = true) && keyCode == GLFW_KEY_V ||
            modifiers(shift = true) && keyCode == GLFW_KEY_INSERT
    override fun isCut(keyCode: Int): Boolean = modifiers(ctrl = true) && keyCode == GLFW_KEY_X ||
            modifiers(shift = true) && keyCode == GLFW_KEY_DELETE

    override fun jumpType(): JumpType = when {
        // -> JumpType.LINE - there is no standard modifier for jumping to the line ends
        ctrl && !alt && !win -> JumpType.WORD
        else -> JumpType.CHARACTER
    }

    private fun modifiers(
        ctrl: Boolean? = false,
        alt: Boolean? = false,
        shift: Boolean? = false,
        win: Boolean? = false
    ): Boolean = (ctrl == null || ctrl == this.ctrl) && (alt == null || alt == this.alt) &&
            (shift == null || shift == this.shift) && (win == null || win == this.win)
}
