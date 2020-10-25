package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color

class PositionVisualizationLayer: GuiLayer() {
    var xColor: Color = DistinctColors.red
    var yColor: Color = DistinctColors.green

    init {
        this.zIndex = 102.0
    }

    override fun draw(context: GuiDrawContext) {
        val parent = parent ?: return
        RenderSystem.lineWidth(3f)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderTypes.flatLines)

        // get into parent's space
        context.matrix *= inverseTransform
        // get into their parent space
        context.matrix *= parent.inverseTransform

        val xDrawColor = Color(xColor.red, xColor.green, xColor.blue, 190)
        val yDrawColor = Color(yColor.red, yColor.green, yColor.blue, 190)

        vb.pos2d(context.matrix, 0, parent.pos.y).color(xDrawColor).endVertex()
        vb.pos2d(context.matrix, parent.pos.x, parent.pos.y).color(xDrawColor).endVertex()
        vb.pos2d(context.matrix, parent.pos.x, 0).color(yDrawColor).endVertex()
        vb.pos2d(context.matrix, parent.pos.x, parent.pos.y).color(yDrawColor).endVertex()

        buffer.finish()
        RenderSystem.lineWidth(1f)
    }
}
