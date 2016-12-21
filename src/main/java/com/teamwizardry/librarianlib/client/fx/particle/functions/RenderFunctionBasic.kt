package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBase
import com.teamwizardry.librarianlib.client.fx.particle.ParticleRenderManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * Render a particle from the block texture map.
 */
@SideOnly(Side.CLIENT)
class RenderFunctionBasic(val texture: TextureAtlasSprite, flatLayer: Boolean) : RenderFunction(if (flatLayer) ParticleRenderManager.LAYER_BLOCK_MAP else ParticleRenderManager.LAYER_BLOCK_MAP_ADDITIVE) {

    /**
     * Get [tex] from `Minecraft.getMinecraft().getTextureMapBlocks()` automatically
     */
    constructor(tex: ResourceLocation, flatLayer: Boolean) : this(Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(tex.toString()) ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite, flatLayer)

    /**
     * `i` is from 0-1 along the animation
     */
    override fun render(i: Float, particle: ParticleBase, color: Color, alpha: Float,
                        worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, pos: Vec3d, skyLight: Int, blockLight: Int) {
        val uMin = texture.minU.toDouble()
        val uMax = texture.maxU.toDouble()
        val vMin = texture.minV.toDouble()
        val vMax = texture.maxV.toDouble()

        val radius = 0.1f * scale

        val vertOffsets = arrayOf(
                Vec3d((-rotationX * radius - rotationXY * radius).toDouble(), (-rotationZ * radius).toDouble(), (-rotationYZ * radius - rotationXZ * radius).toDouble()),
                Vec3d((-rotationX * radius + rotationXY * radius).toDouble(), (rotationZ * radius).toDouble(), (-rotationYZ * radius + rotationXZ * radius).toDouble()),
                Vec3d((rotationX * radius + rotationXY * radius).toDouble(), (rotationZ * radius).toDouble(), (rotationYZ * radius + rotationXZ * radius).toDouble()),
                Vec3d((rotationX * radius - rotationXY * radius).toDouble(), (-rotationZ * radius).toDouble(), (rotationYZ * radius - rotationXZ * radius).toDouble())
        )

        worldRendererIn.pos(pos.xCoord + vertOffsets[0].xCoord, pos.yCoord + vertOffsets[0].yCoord, pos.zCoord + vertOffsets[0].zCoord).tex(uMax, vMax).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.xCoord + vertOffsets[1].xCoord, pos.yCoord + vertOffsets[1].yCoord, pos.zCoord + vertOffsets[1].zCoord).tex(uMax, vMin).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.xCoord + vertOffsets[2].xCoord, pos.yCoord + vertOffsets[2].yCoord, pos.zCoord + vertOffsets[2].zCoord).tex(uMin, vMin).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.xCoord + vertOffsets[3].xCoord, pos.yCoord + vertOffsets[3].yCoord, pos.zCoord + vertOffsets[3].zCoord).tex(uMin, vMax).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
    }
}
