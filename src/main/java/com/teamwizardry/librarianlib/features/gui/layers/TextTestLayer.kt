package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.text.Font
import com.teamwizardry.librarianlib.features.text.TextLayout
import com.teamwizardry.librarianlib.features.text.TextRun
import com.teamwizardry.librarianlib.features.text.Typesetter
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import java.awt.Color
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

class TextTestLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private var runs = listOf<TextRun>()
    var text: String = ""
        set(value) {
            field = value
            val runs = mutableListOf<TextRun>()
            var remaining = text
            while(true) {
                val (rem, run) = Typesetter.createRun(Font.tinyMono, remaining, size.xi)
                if(remaining == rem) break
                remaining = rem
                runs.add(run)
            }
            this.runs = runs
        }

    override fun draw(partialTicks: Float) {
        GlStateManager.pushMatrix()

//        when(layout.align.x) {
//            Align2d.X.LEFT -> {}
//            Align2d.X.CENTER -> GlStateManager.translate(size.x/2, 0.0, 0.0)
//            Align2d.X.RIGHT -> GlStateManager.translate(size.x, 0.0, 0.0)
//        }
//        when(layout.align.y) {
//            Align2d.Y.TOP -> {}
//            Align2d.Y.CENTER -> GlStateManager.translate(0.0, ((size.y - layout.bounds.height)/2).toInt() + 1.0, 0.0)
//            Align2d.Y.BOTTOM -> GlStateManager.translate(0.0, size.y - layout.bounds.height, 0.0)
//        }

        GlStateManager.disableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.enableBlend()
        Minecraft().renderEngine.bindTexture(Font.tinyMono.texture)
        val vb = Tessellator.getInstance().buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        var cursor = 0
        runs.forEach { run ->
            run.place(vb, Color.BLACK, 0, cursor, 1)
            cursor += 9
        }
        Tessellator.getInstance().draw()
        GlStateManager.enableCull()

        GlStateManager.popMatrix()
    }
}