package com.teamwizardry.librarianlib.facade.example.visualization

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DistinctColors
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.text.BitfontFormatting
import com.teamwizardry.librarianlib.core.util.vec
import dev.thecodewarrior.bitfont.typesetting.MutableAttributedString
import net.minecraft.client.render.VertexConsumerProvider
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

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(SimpleRenderLayers.flatLines)

        val xDrawColor = Color(xColor.red, xColor.green, xColor.blue, 190)
        val yDrawColor = Color(yColor.red, yColor.green, yColor.blue, 190)

        vb.vertex2d(context.transform, 0, target.pos.y).color(xDrawColor).next()
        vb.vertex2d(context.transform, target.pos.x, target.pos.y).color(xDrawColor).next()
        vb.vertex2d(context.transform, target.pos.x, 0).color(yDrawColor).next()
        vb.vertex2d(context.transform, target.pos.x, target.pos.y).color(yDrawColor).next()

        buffer.draw()
        RenderSystem.lineWidth(1f)
    }
}
