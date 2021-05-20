package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import net.minecraft.client.render.VertexConsumerProvider
import java.awt.Color

class AnchorVisualizationLayer(val target: GuiLayer): GuiLayer() {
    val anchorSize_im: IMValueDouble = imDouble(3.0)
    var anchorSize: Double by anchorSize_im

    var color: Color = DistinctColors.red

    init {
        this.zIndex = 103.0
    }

    override fun update() {
        val parent = parent ?: return
        pos = vec(0, 0)
        size = parent.size
        scale = 1.0
        rotation = 0.0
        anchor = vec(0, 0)
    }

    override fun draw(context: GuiDrawContext) {
        val parent = parent ?: return

        RenderSystem.lineWidth(3f)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderLayers.flatLines)

        // get into parent's space
        context.matrix *= inverseTransform
        // get into their parent space
        context.matrix *= parent.inverseTransform

        vb.vertex2d(context.transform, target.pos.x - anchorSize, target.pos.y - anchorSize).color(color).next()
        vb.vertex2d(context.transform, target.pos.x + anchorSize, target.pos.y + anchorSize).color(color).next()
        vb.vertex2d(context.transform, target.pos.x + anchorSize, target.pos.y - anchorSize).color(color).next()
        vb.vertex2d(context.transform, target.pos.x - anchorSize, target.pos.y + anchorSize).color(color).next()

        buffer.draw()
        RenderSystem.lineWidth(1f)
    }
}