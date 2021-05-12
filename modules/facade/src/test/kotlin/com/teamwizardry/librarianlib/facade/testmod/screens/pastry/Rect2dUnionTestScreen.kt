package com.teamwizardry.librarianlib.facade.testmod.screens.pastry

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderTypes
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.pastry.Rect2dUnion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.text.Text
import net.minecraft.util.text.ITextComponent
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

internal class Rect2dUnionTestScreen(title: Text): FacadeScreen(title) {
    val background = PastryBackground(0, 0, 1, 1)
    val contentBackground = PastryBackground(PastryBackgroundStyle.LIGHT_INSET, 0, 0, 0, 0)
    val segmentViewer = SegmentViewerLayer()

    val steps = mutableListOf(
        "Merge Collinear" to { segmentViewer.rect2dUnion.mergeCollinear() },
        "Compute Depths" to { segmentViewer.rect2dUnion.computeDepths() },
    )
    var stepIndex = 0
    val nextStepButton: PastryButton = PastryButton(steps[stepIndex].first, 1, 1)

    val resetButton = PastryButton("Reset", 1, 1) {
        segmentViewer.reset()
        stepIndex = 0
        nextStepButton.label.text = steps[0].first
        nextStepButton.fitLabel()
    }
    val buttonStack = StackLayout.build(1, 1)
        .horizontal()
        .alignLeft()
        .add(resetButton)
        .add(nextStepButton)
        .fitBreadth()
        .build()

    init {
        nextStepButton.hook<PastryButton.ClickEvent> {
            steps.getOrNull(stepIndex++)?.second?.invoke()
            nextStepButton.label.text = steps.getOrNull(stepIndex)?.first ?: "Done"
            nextStepButton.fitLabel()
        }

        main.size = vec(200, 200)

        main.add(background, contentBackground, segmentViewer, buttonStack)

        main.BUS.hook<GuiLayerEvents.LayoutChildren> {
            background.frame = main.bounds.offset(-2, -2, 2, 2)
            contentBackground.frame = main.bounds.offset(0, resetButton.frame.maxY + 1, 0, 0)

            segmentViewer.frame = contentBackground.frame.offset(2, 2, -2, -2)
        }
    }

    class SegmentViewerLayer(): GuiLayer() {
        val rects: MutableList<Rect2d> = mutableListOf()
        var dragStart: Vec2d? = null
        var rect2dUnion = Rect2dUnion(rects)

        fun reset() {
            rects.clear()
            rect2dUnion = Rect2dUnion(rects)
        }

        @Hook
        fun mouseDown(e: GuiLayerEvents.MouseDown) {
            if(!mouseOver) return
            dragStart = (mousePos / 5).round() * 5
        }

        @Hook
        fun mouseUp(e: GuiLayerEvents.MouseUp) {
            dragStart?.also { dragStart ->
                val dragEnd = (mousePos / 5).round() * 5
                val dragMin = vec(min(dragStart.x, dragEnd.x), min(dragStart.y, dragEnd.y))
                val dragMax = vec(max(dragStart.x, dragEnd.x), max(dragStart.y, dragEnd.y))
                val dragSize = dragMax - dragMin
                if(dragSize != vec(0, 0)) {
                    rects.add(rect(dragMin, dragSize))
                    rect2dUnion = Rect2dUnion(rects)
                }
            }
            dragStart = null
        }

        override fun layoutChildren() {
            super.layoutChildren()
        }

        override fun draw(context: GuiDrawContext) {
            super.draw(context)

            val startColor = Color.BLUE
            val endColor = Color.RED
            val arrowColor = Color.RED

            val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
            val vb = buffer.getBuffer(SimpleRenderLayers.flatLines)

            for(segment in rect2dUnion.horizontalSegments + rect2dUnion.verticalSegments) {
                segment ?: continue
                if(segment.depth != 0) continue
                vb.pos2d(context.transform, segment.startVec).color(startColor).endVertex()
                vb.pos2d(context.transform, segment.endVec).color(endColor).endVertex()
                val sideOffset = segment.side.vector
                val forwardOffset = segment.side.rotateCW().vector * 3

                vb.pos2d(context.transform, segment.endVec - forwardOffset + sideOffset).color(arrowColor).endVertex()
                vb.pos2d(context.transform, segment.endVec).color(arrowColor).endVertex()
                vb.pos2d(context.transform, segment.endVec).color(arrowColor).endVertex()
                vb.pos2d(context.transform, segment.endVec - forwardOffset - sideOffset).color(arrowColor).endVertex()

            }

            RenderSystem.lineWidth(2f)
            buffer.finish()
            RenderSystem.lineWidth(1f)
        }
    }
}
