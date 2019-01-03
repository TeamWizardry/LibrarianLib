package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.supporting.setData
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.layout.Flexbox
import com.teamwizardry.librarianlib.features.gui.layout.flex
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import java.awt.Color

class GuiTestFlexBox: PastryWindow(100, 100, PastryWindow.Style.PANEL, true) {
    val stack = Flexbox(0, 0, 100, 100, Cardinal2d.GUI.DOWN)
    val top = ColorLayer(Color.BLACK, 0, 0, 0, 3)
    val bottom = ColorLayer(Color.BLACK, 0, 0, 0, 3)
    val left = ColorLayer(Color.BLACK, 0, 0, 3, 0)
    val right = ColorLayer(Color.BLACK, 0, 0, 3, 0)

    init {
        bottom.anchor = vec(0, 1)
        right.anchor = vec(1, 0)

        content.add(top, bottom, left, right, stack)
        stack.spacing = 2

        minContentSize = vec(10, 10)
        maxContentSize = Vec2d.INFINITY

        createTest(50, 40, 50) { box, a, b, c ->
            box.spacing = 2
            a.flex.config(maxSize = 50, minSize = 10)
            b.flex.config(maxSize = 150, minSize = 50)
            c.flex.config(maxSize = 50, minSize = 10)
        }
        createTest(50, 50, 50) { box, a, b, c ->
            box.spacing = 2
            box.flexDirection = Cardinal2d.GUI.LEFT
            a.flex.config(maxSize = 50, minSize = 10)
            b.flex.config(maxSize = 150, minSize = 50)
            c.flex.config(maxSize = 50, minSize = 10)
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            a.add(ColorLayer(Color.ORANGE, -30, 0, 40, 5))
            a.flex.config(order = 1) // `a` will appear last in the list but will still be beneath `b` and `c`
            b.flex.config(flexGrow = 2) // this will hog 1/2 the space. `2/(1+2+1)`
            c.flex.config(flexShrink = 2) // this will shrink 2x as fast as `a` or `b`
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.START
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.CENTER
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.END
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.SPACE_BETWEEN
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.SPACE_AROUND
        }
        createTest(25, 25, 25) { box, a, b, c ->
            box.spacing = 2
            box.justifyContent = Flexbox.Justify.SPACE_EVENLY
        }
    }

    fun createTest(sizeA: Int, sizeB: Int, sizeC: Int,
        config: (box: Flexbox, a: GuiComponent, b: GuiComponent, c: GuiComponent) -> Unit) {
        val flexbox = Flexbox(0, 0, 100, 25)
        val a = ColorLayer(Color.RED, 0, 0, sizeA, 0).componentWrapper()
        val b = ColorLayer(Color.GREEN, 0, 0, sizeB, 0).componentWrapper()
        val c = ColorLayer(Color.BLUE, 0, 0, sizeC, 0).componentWrapper()
        flexbox.add(a, b, c)
        config(flexbox, a, b, c)

        stack.add(flexbox)
        flexbox.flex
    }

    override fun layoutChildren() {
        super.layoutChildren()
        stack.frame = content.bounds.shrink(3.0)

        top.width = content.width
        left.height = content.height

        bottom.width = content.width
        bottom.y = content.height
        right.height = content.height
        right.x = content.width
    }
}