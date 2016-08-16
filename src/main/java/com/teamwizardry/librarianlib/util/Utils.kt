package com.teamwizardry.librarianlib.util

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import org.apache.commons.lang3.text.WordUtils
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * Created by Saad on 4/9/2016.
 */
object Utils {

    fun drawNormalItemStack(itemStack: ItemStack?, x: Int, y: Int) {
        if (itemStack != null) {
            GlStateManager.enableRescaleNormal()
            RenderHelper.enableGUIStandardItemLighting()
            Minecraft.getMinecraft().renderItem.renderItemIntoGUI(itemStack, x, y)
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
        }
    }

    fun padString(string: String?, stringSize: Int): ArrayList<String> {
        val lines = ArrayList<String>()
        if (string != null)
            for (line in WordUtils.wrap(string, stringSize).split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()) lines.add(line.trim({ it <= ' ' }))
        return lines
    }

    fun drawLine2D(x1: Int, y1: Int, x2: Int, y2: Int, width: Int, color: Color) {
        GlStateManager.pushMatrix()
        GlStateManager.disableTexture2D()
        GlStateManager.color(color.r, color.g, color.b, 1f)

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(width.toFloat())

        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3f(x1.toFloat(), y1.toFloat(), 0f)
        GL11.glVertex3f(x2.toFloat(), y2.toFloat(), 0f)
        GL11.glEnd()

        GlStateManager.enableTexture2D()
        GlStateManager.popMatrix()
    }

    fun drawLine3D(pos1: BlockPos, pos2: BlockPos, color: Color) {
        GlStateManager.pushMatrix()

        GL11.glLineWidth(1f)

        GlStateManager.disableTexture2D()
        GlStateManager.color(color.r, color.g, color.b, 0.7f)
        GlStateManager.translate(0.5, 0.7, 0.5)

        val vb = Tessellator.getInstance().buffer
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION)
        vb.pos((pos2.x - pos1.x).toDouble(), (pos2.y - pos1.y).toDouble(), (pos2.z - pos1.z).toDouble()).endVertex()
        vb.pos(0.0, 0.0, 0.0).endVertex()
        Tessellator.getInstance().draw()

        GlStateManager.enableTexture2D()

        GlStateManager.popMatrix()
    }

    fun isInside(currentX: Int, currentY: Int, x: Int, y: Int, size: Int): Boolean {
        return currentX >= x && currentX < x + size && currentY >= y && currentY < y + size
    }

    fun isInside(currentX: Int, currentY: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
        return currentX >= x && currentX < x + width && currentY >= y && currentY < y + height
    }
}
