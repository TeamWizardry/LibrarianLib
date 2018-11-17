package com.teamwizardry.librarianlib.test.render

import com.teamwizardry.librarianlib.core.client.RenderHookHandler
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.init.Blocks
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


            val texturemap = Minecraft.getMinecraft().textureMapBlocks
            texturesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still")
            texturesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow")
            texturesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still")
            texturesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow")

            val wetSprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite("minecraft:blocks/sponge_wet")

            RenderHookHandler.registerFluidHook { _, world, state, pos, buffer ->
                retextureFluid(wetSprite, world, state, pos, buffer)
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
                val oldU = intBitsToFloat(buf[truePos + vertexIndex * jShift])
                val oldV = intBitsToFloat(buf[truePos + vertexIndex * jShift + 1])

                val originalU = sprite.getUnInterpolatedU(oldU)
                val originalV = sprite.getUnInterpolatedV(oldV)

                val newU = newTex.getInterpolatedU(originalU.toDouble())
                val newV = newTex.getInterpolatedV(originalV.toDouble())

                buf.put(truePos + vertexIndex * jShift, floatToRawIntBits(newU))
                buf.put(truePos + vertexIndex * jShift + 1, floatToRawIntBits(newV))
            }
        }
    }

    private val texturesWater = arrayOfNulls<TextureAtlasSprite>(2)
    private val texturesLava = arrayOfNulls<TextureAtlasSprite>(2)

    private fun retextureFluid(newTex: TextureAtlasSprite, world: IBlockAccess, state: IBlockState, pos: BlockPos, buffer: BufferBuilder) {
        val liquid = state.block as BlockLiquid

        val textures = if (state.material == Material.LAVA) texturesLava else texturesWater
        val txStill = textures[0]
        val txFlow = textures[1]
        if (txStill == null || txFlow == null) return

        var total = 0

        if (state.shouldSideBeRendered(world, pos, EnumFacing.UP)) {
            total++
            if (liquid.shouldRenderSides(world, pos.up()))
                total++
        }

        if (state.shouldSideBeRendered(world, pos, EnumFacing.DOWN))
            total++

        for (face in EnumFacing.HORIZONTALS) if (state.shouldSideBeRendered(world, pos, face)) {
            if (state.material != Material.LAVA) {
                val block = world.getBlockState(pos.offset(face)).block
                if (block != Blocks.GLASS && block != Blocks.STAINED_GLASS)
                    total++
            }
            total++
        }


        val format = buffer.vertexFormat
        val buf = buffer.byteBuffer.asIntBuffer()
        if (total != 0)
            for (i in 1..total) {
                val shift = format.getUvOffsetById(0) / 4
                val truePos = (buffer.vertexCount - i * 4) * format.integerSize + shift
                val jShift = format.integerSize

                for (vertexIndex in 0 until 4) {
                    val oldU = intBitsToFloat(buf[truePos + vertexIndex * jShift])
                    val oldV = intBitsToFloat(buf[truePos + vertexIndex * jShift + 1])

                    val sprite = if (oldU <= txFlow.maxU && oldU >= txFlow.minU && oldV <= txFlow.maxV && oldV >= txFlow.minV) txFlow else txStill

                    val originalU = sprite.getUnInterpolatedU(oldU)
                    val originalV = sprite.getUnInterpolatedV(oldV)

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
