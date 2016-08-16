package com.teamwizardry.librarianlib.structure

import com.teamwizardry.librarianlib.util.BlockRenderUtils
import com.teamwizardry.librarianlib.util.Color
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import org.lwjgl.opengl.GL11

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
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.r, color.g, color.b, brightness, color.a)
        }

        // cutout block next
        for (info in structure.blockInfos()) {
            val state = access.getBlockState(info.pos)
            if (state.renderType == EnumBlockRenderType.INVISIBLE)
                continue
            if (state.block.blockLayer != BlockRenderLayer.CUTOUT && state.block.blockLayer != BlockRenderLayer.CUTOUT_MIPPED)
                continue
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.r, color.g, color.b, brightness, color.a)
        }

        // translucent block next
        for (info in structure.blockInfos()) {
            val state = access.getBlockState(info.pos)
            if (state.renderType == EnumBlockRenderType.INVISIBLE)
                continue
            if (state.block.blockLayer != BlockRenderLayer.TRANSLUCENT)
                continue
            BlockRenderUtils.renderBlockToVB(state, access, info.pos, info.pos.subtract(structure.origin), blockBuf, color.r, color.g, color.b, brightness, color.a)
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
