package com.teamwizardry.librarianlib.test.render

import com.teamwizardry.librarianlib.core.client.RenderHookHandler
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Float.floatToRawIntBits
import java.lang.Float.intBitsToFloat

/**
 * @author WireSegal
 * Created at 11:00 PM on 1/5/18.
 */
object RenderEntryPoint : TestEntryPoint {

    override fun init(event: FMLInitializationEvent) {
        ClientRunnable.run {
            val sprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite("minecraft:blocks/sponge")
            RenderHookHandler.registerBlockHook { _, world, model, state, pos, buffer ->
                retextureModel(sprite, world, model, state, pos, buffer, true)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private fun retextureModel(newTex: TextureAtlasSprite, world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos, buffer: BufferBuilder, checkSides: Boolean, rand: Long = MathHelper.getPositionRandom(pos)): Boolean {
        var total = 0

        var list = model.getQuads(state, null, rand)

        if (!list.isEmpty()) {
            retextureQuads(newTex, buffer, list, total)
            total += list.size
        }

        for (enumfacing in EnumFacing.values().reversed()) {
            list = model.getQuads(state, enumfacing, rand)

            if (!list.isEmpty() && (!checkSides || state.shouldSideBeRendered(world, pos, enumfacing))) {
                retextureQuads(newTex, buffer, list, total)
                total += list.size
            }
        }

        return total > 0
    }

    @SideOnly(Side.CLIENT)
    private fun retextureQuads(newTex: TextureAtlasSprite, buffer: BufferBuilder, list: List<BakedQuad>, total: Int) {
        val j = list.size
        val format = buffer.vertexFormat
        for (i in (j - 1) downTo 0) {
            val bakedquad = list[i]
            val shift = format.getUvOffsetById(0) / 4
            val truePos = (buffer.vertexCount - (total + j - i) * 4) * format.integerSize + shift
            val jShift = format.integerSize
            val buf = buffer.byteBuffer.asIntBuffer()

            val sprite = bakedquad.sprite

            for (vertexIndex in 0 until 4) {
                val oldU = buf[truePos + vertexIndex * jShift]
                val oldV = buf[truePos + vertexIndex * jShift + 1]

                val originalU = sprite.getUnInterpolatedU(intBitsToFloat(oldU))
                val originalV = sprite.getUnInterpolatedV(intBitsToFloat(oldV))

                val newU = newTex.getInterpolatedU(originalU.toDouble())
                val newV = newTex.getInterpolatedV(originalV.toDouble())

                buf.put(truePos + vertexIndex * jShift, floatToRawIntBits(newU))
                buf.put(truePos + vertexIndex * jShift + 1, floatToRawIntBits(newV))
            }
        }
    }

    override fun preInit(event: FMLPreInitializationEvent) = Unit
    override fun postInit(event: FMLPostInitializationEvent) = Unit
}
