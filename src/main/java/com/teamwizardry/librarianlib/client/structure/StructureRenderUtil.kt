package com.teamwizardry.librarianlib.client.structure

import com.teamwizardry.librarianlib.client.util.BlockRenderUtils
import com.teamwizardry.librarianlib.common.structure.Structure
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import org.lwjgl.opengl.GL11
import java.awt.Color

object StructureRenderUtil {

    private val blockBuf = VertexBuffer(50000)

    fun render(structure: Structure, color: Color, brightness: Float): IntArray {
        val access = structure.blockAccess

        blockBuf.reset()
        blockBuf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

        // solid block first
        for (info in structure.blockInfos()) {
            val state = access.getBlockState(info.pos)
            if (state.renderType == EnumBlockRenderType.INVISIBLE)
                continue
            if (state.block.blockLayer != BlockRenderLayer.SOLID)
                continue
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.red / 255f, color.green / 255f, color.blue / 255f, brightness, color.alpha / 255f)
        }

        // cutout block next
        for (info in structure.blockInfos()) {
            val state = access.getBlockState(info.pos)
            if (state.renderType == EnumBlockRenderType.INVISIBLE)
                continue
            if (state.block.blockLayer != BlockRenderLayer.CUTOUT && state.block.blockLayer != BlockRenderLayer.CUTOUT_MIPPED)
                continue
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.red / 255f, color.green / 255f, color.blue / 255f, brightness, color.alpha / 255f)
        }

        // translucent block next
        for (info in structure.blockInfos()) {
            val state = access.getBlockState(info.pos)
            if (state.renderType == EnumBlockRenderType.INVISIBLE)
                continue
            if (state.block.blockLayer != BlockRenderLayer.TRANSLUCENT)
                continue
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.red / 255f, color.green / 255f, color.blue / 255f, brightness, color.alpha / 255f)
        }

        blockBuf.finishDrawing()

        val intBuf = blockBuf.byteBuffer.asIntBuffer()
        val bufferInts = IntArray(intBuf.limit())
        for (i in bufferInts.indices) {
            bufferInts[i] = intBuf.get(i)
        }
        return bufferInts
    }

}
