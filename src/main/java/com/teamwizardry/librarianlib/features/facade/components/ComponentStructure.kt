package com.teamwizardry.librarianlib.features.facade.components

import com.teamwizardry.librarianlib.features.facade.value.IMValue
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.structure.Structure
import com.teamwizardry.librarianlib.features.structure.StructureRenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentStructure(posX: Int, posY: Int, var structure: Structure?) : GuiComponent(posX, posY) {

    val color_im: IMValue<Color> = IMValue(Color.WHITE)
    var color: Color by color_im

    init {
        initStructure()
    }

    override fun draw(partialTicks: Float) {
        GlStateManager.translate(this.pos.x, this.pos.y, 0.0)

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        val buf = bufferInts
        buf ?: return

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

        vb.addVertexData(buf)

        tessellator.draw()

    }

    fun initStructure() {
        bufferInts = null
        val tmp = structure ?: return
        bufferInts = StructureRenderUtil.render(tmp, color, 1f)
    }

    companion object {
        private var bufferInts: IntArray? = null
    }

}
