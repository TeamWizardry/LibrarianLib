package com.teamwizardry.librarianlib.gui.component.supporting

import com.teamwizardry.librarianlib.core.util.kotlin.unreachable
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.input.Cursor

data class MouseHit(val component: GuiComponent, val zIndex: Double, val cursor: Cursor?) {
    companion object {
        /**
         * @param   left the object on the left of the comparison.
         * @param   right the object on the right of the comparison.
         * @return  a negative integer, zero, or a positive integer as the left object
         *          is less than, equal to, or greater than the right object.
         */
        @JvmStatic
        fun compare(left: MouseHit?, right: MouseHit?): Int {
            return when {
                left == null && right == null -> 0
                left != null && right == null -> 1
                left == null && right != null -> -1
                left != null && right != null -> left.zIndex.compareTo(right.zIndex)
                else -> unreachable()
            }
        }
    }
}

operator fun MouseHit?.compareTo(other: MouseHit?): Int {
    return MouseHit.compare(this, other)
}
