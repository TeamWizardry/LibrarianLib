package com.teamwizardry.librarianlib.features.neogui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents

open class PastryControl: GuiComponent {
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)

    /**
     * The next control in the sequence
     */
    var next: PastryControl? = null
    /**
     * The previous control in the sequence
     */
    var previous: PastryControl? = null

    fun <T: PastryControl> tabTo(next: T): T {
        this.next = next
        next.previous = this
        return next
    }

    /**
     * Focuses the next control, or if there's no next control focuses the first control.
     *
     * If both next and previous are null this method simply blurs this component and returns true.
     * The "first" control is found by recursively searching along [previous] until either null is encountered or a
     * component is encountered a second time (to prevent infinite recursion)
     *
     * @return whether the focus successfully switched
     */
    fun focusNext(): Boolean {
        var target = next
        if(target == null) {
            if(previous === this) {
                target = this
            } else {
                var head = previous ?: run {
                    this.requestBlur()
                    return true
                }
                val encountered = mutableListOf(this, head)
                while (true) {
                    val tip = head.previous
                    if (tip == null || encountered.any { tip === it })
                        break
                    head = tip
                    encountered.add(head)
                }
                target = head
            }
        }
        return target.requestFocus()
    }

    /**
     * Focuses the previous control, or if there's no previous control focuses the last control.
     *
     * If both next and previous are null this method simply blurs this component and returns true.
     * The "last" control is found by recursively searching along [next] until either null is encountered or a
     * component is encountered a second time (to prevent infinite recursion)
     *
     * @return whether the focus successfully switched
     */
    fun focusPrevious(): Boolean {
        var target = previous
        if(target == null) {
            if(next === this) {
                target = this
            } else {
                var head = next ?: run {
                    this.requestBlur()
                    return true
                }
                val encountered = mutableListOf(this, head)
                while (true) {
                    val tip = head.next
                    if (tip == null || encountered.any { tip === it })
                        break
                    head = tip
                    encountered.add(head)
                }
                target = head
            }
        }
        return target.requestFocus()
    }

    @Hook
    private fun requestFocus(e: GuiComponentEvents.RequestFocusEvent) {
        e.allow = true
    }

    @Hook
    private fun mouseDown(e: GuiComponentEvents.MouseDownEvent) {
        requestFocusedState(mouseOver)
    }
}