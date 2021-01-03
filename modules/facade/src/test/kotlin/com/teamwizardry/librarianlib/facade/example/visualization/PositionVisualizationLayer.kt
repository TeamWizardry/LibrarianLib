package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.facade.text.BitfontRenderer
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.core.util.vec
import dev.thecodewarrior.bitfont.typesetting.MutableAttributedString
import dev.thecodewarrior.bitfont.typesetting.TextAttribute
import net.minecraft.client.renderer.IRenderTypeBuffer
import java.awt.Color

class PositionVisualizationLayer(val target: GuiLayer): GuiLayer() {
    var xColor: Color = DistinctColors.red
    var yColor: Color = DistinctColors.green
    val coordinateLayer: TextLayer = TextLayer()

    init {
        this.zIndex = 102.0
        coordinateLayer.textFitting = TextLayer.FitType.BOTH
        coordinateLayer.textMargins = TextLayer.Margins(2.0, 2.0, 2.0, 2.0)
        coordinateLayer.color = DistinctColors.blue
        this.add(coordinateLayer)
    }

    override fun update() {
        coordinateLayer.attributedText = MutableAttributedString("(")
            .append("${target.pos.x.toInt()}", BitfontFormatting.color to xColor)
            .append(", ")
            .append("${target.pos.y.toInt()}", BitfontFormatting.color to yColor)
            .append(")")
        coordinateLayer.anchor = vec(
            if(target.pos.x < 0) 1 else 0,
            1
        )
        coordinateLayer.pos = target.pos
    }

    override fun draw(context: GuiDrawContext) {
        RenderSystem.lineWidth(3f)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderTypes.flatLines)

        val xDrawColor = Color(xColor.red, xColor.green, xColor.blue, 190)
        val yDrawColor = Color(yColor.red, yColor.green, yColor.blue, 190)

        vb.pos2d(context.matrix, 0, target.pos.y).color(xDrawColor).endVertex()
        vb.pos2d(context.matrix, target.pos.x, target.pos.y).color(xDrawColor).endVertex()
        vb.pos2d(context.matrix, target.pos.x, 0).color(yDrawColor).endVertex()
        vb.pos2d(context.matrix, target.pos.x, target.pos.y).color(yDrawColor).endVertex()

        buffer.finish()
        RenderSystem.lineWidth(1f)
    }
}
