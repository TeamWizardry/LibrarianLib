package com.teamwizardry.librarianlib.facade.test.screens

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layers.DragLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.math.clamp
import net.minecraft.text.Text
import java.awt.Color

class DragLayerTestScreen(title: Text): FacadeScreen(title) {
    init {
        val background = RectLayer(Color.lightGray, 0, 0, 250, 150)
        main.add(background)
        main.size = background.size

        val dragSelf = DragLayer(20, 20, 50, 50)
        dragSelf.add(RectLayer(Color.red, 0, 0, 50, 50))
        dragSelf.rotation = Math.toRadians(15.0)
        dragSelf.scale2d = vec(1, 0.5)

        val dragParent = RectLayer(Color.blue, 60, 20, 50, 50)
        dragParent.rotation = Math.toRadians(15.0)
        dragParent.scale2d = vec(1, 0.5)
        val parentDragLayer = DragLayer(5, 5, 40, 15)
        parentDragLayer.add(RectLayer(Color.cyan, 0, 0, 40, 15))
        parentDragLayer.rotation = Math.toRadians(15.0)
        parentDragLayer.targetLayer = dragParent
        dragParent.add(parentDragLayer)

        val dragBounds = DragLayer(20, 80, 30, 30)
        dragBounds.add(RectLayer(Color.green, 0, 0, 30, 30))
        dragBounds.hook<DragLayer.DragEvent> {
            it.targetPosition = vec(
                it.targetPosition.x.clamp(0.0, main.size.x),
                it.targetPosition.y.clamp(0.0, main.size.y)
            )
        }

        main.add(dragSelf, dragParent, dragBounds)
    }
}