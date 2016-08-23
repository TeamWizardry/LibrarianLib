package com.teamwizardry.librarianlib.client.util

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.nio.ByteOrder

@SideOnly(Side.CLIENT)
object BlockRenderUtils {
    @JvmStatic
    fun transferVB(from: VertexBuffer, to: VertexBuffer) {
        to.addVertexData(from.byteBuffer.asIntBuffer().array())
    }

    @JvmStatic
    fun renderQuadsToBuffer(quads: List<BakedQuad>, state: IBlockState, access: IBlockAccess, pos: BlockPos, renderPos: BlockPos, buf: VertexBuffer, red: Float, green: Float, blue: Float, brightness: Float, alpha: Float) {
        for (i in quads.indices) {
            val bakedquad = quads[i]
            buf.addVertexData(bakedquad.vertexData)

            if (bakedquad.hasTintIndex()) {
                val l = Minecraft.getMinecraft().blockColors.colorMultiplier(state, access, pos, bakedquad.tintIndex)

                val redMul = (l shr 16 and 255).toFloat() / 255.0f
                val blueMul = (l shr 8 and 255).toFloat() / 255.0f
                val greenMul = (l and 255).toFloat() / 255.0f

                putColorMultiplier(buf, redMul * brightness, blueMul * brightness, greenMul * brightness, alpha, 4)
                putColorMultiplier(buf, redMul * brightness, blueMul * brightness, greenMul * brightness, alpha, 3)
                putColorMultiplier(buf, redMul * brightness, blueMul * brightness, greenMul * brightness, alpha, 2)
                putColorMultiplier(buf, redMul * brightness, blueMul * brightness, greenMul * brightness, alpha, 1)
            } else {
                putRGBA_F4(buf, red * brightness, green * brightness, blue * brightness, alpha, 4)
                putRGBA_F4(buf, red * brightness, green * brightness, blue * brightness, alpha, 3)
                putRGBA_F4(buf, red * brightness, green * brightness, blue * brightness, alpha, 2)
                putRGBA_F4(buf, red * brightness, green * brightness, blue * brightness, alpha, 1)
            }

            val normal = bakedquad.face.directionVec
            buf.putNormal(normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat())
            buf.putPosition(renderPos.x.toDouble(), renderPos.y.toDouble(), renderPos.z.toDouble())
        }
    }

    @JvmStatic
    private fun putRGBA_F4(buf: VertexBuffer, red: Float, green: Float, blue: Float, alpha: Float, relIndex: Int) {
        val index = buf.getColorIndex(relIndex)
        val r = MathHelper.clamp_int((red * 255.0f).toInt(), 0, 255)
        val g = MathHelper.clamp_int((green * 255.0f).toInt(), 0, 255)
        val b = MathHelper.clamp_int((blue * 255.0f).toInt(), 0, 255)
        val a = MathHelper.clamp_int((alpha * 255.0f).toInt(), 0, 255)
        buf.putColorRGBA(index, r, g, b, a)
    }

    @JvmStatic
    private fun putColorMultiplier(buf: VertexBuffer, red: Float, green: Float, blue: Float, alpha: Float, p_178978_4_: Int) {
        val index = buf.getColorIndex(p_178978_4_)
        var color = buf.byteBuffer.asIntBuffer().get(index)

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            val newRed = ((color and 255).toFloat() * red).toInt()
            val newGreen = ((color shr 8 and 255).toFloat() * green).toInt()
            val newBlue = ((color shr 16 and 255).toFloat() * blue).toInt()
            val newAlpha = ((color shr 24 and 255).toFloat() * alpha).toInt()

            color = newAlpha shl 24 or newBlue shl 16 or newGreen shl 8 or newRed
        } else {
            val newRed = ((color shr 24 and 255).toFloat() * red).toInt()
            val newGreen = ((color shr 16 and 255).toFloat() * green).toInt()
            val newBlue = ((color shr 8 and 255).toFloat() * blue).toInt()
            val newAlpha = ((color and 255).toFloat() * alpha).toInt()
            color = newRed shl 24 or newGreen shl 16 or newBlue shl 8 or newAlpha
        }

        buf.byteBuffer.asIntBuffer().put(index, color)
    }

    @JvmStatic
    fun renderBlockToVB(state: IBlockState, access: IBlockAccess, pos: BlockPos, renderPos: BlockPos, buffer: VertexBuffer, red: Float, green: Float, blue: Float, brightness: Float, alpha: Float) {
        val newState = state.getActualState(access, pos)

        val model = Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(newState)

        for (enumfacing in EnumFacing.values()) {
            if (!newState.shouldSideBeRendered(access, pos, enumfacing))
                continue
            renderQuadsToBuffer(model.getQuads(newState, enumfacing, 0L), newState, access, pos, renderPos, buffer, red, green, blue, brightness, alpha)
        }

        renderQuadsToBuffer(model.getQuads(newState, null, 0L), newState, access, pos, renderPos, buffer, red, green, blue, brightness, alpha)


    }
}
