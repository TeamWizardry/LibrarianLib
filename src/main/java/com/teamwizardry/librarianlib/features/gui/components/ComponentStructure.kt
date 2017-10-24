package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.mixin.gl.GlMixin
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.structure.Structure
import com.teamwizardry.librarianlib.features.structure.StructureRenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentStructure(posX: Int, posY: Int, var structure: Structure?) : GuiComponent(posX, posY) {

    val color = Option<ComponentStructure, Color>(Color.WHITE)

    init {
        GlMixin.transform(this).func { Vec3d(this.pos.x, this.pos.y, 0.0) }
        initStructure()
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        GlStateManager.translate(this.pos.x, this.pos.y, 0.0)
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val buf = bufferInts
        buf ?: return

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

        vb.addVertexData(buf)

        tessellator.draw()

        GlStateManager.translate(-this.pos.x, -this.pos.y, 0.0)
    }

    fun initStructure() {
        bufferInts = null
        val tmp = structure ?: return
        bufferInts = StructureRenderUtil.render(tmp, color.getValue(this), 1f)
    }

    companion object {
        private var bufferInts: IntArray? = null
    }

}
