package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBase
import com.teamwizardry.librarianlib.client.fx.particle.ParticleRenderManager
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * Render a particle from the block texture map.
 */
class RenderFunctionBasic(val texture: TextureAtlasSprite) : RenderFunction(ParticleRenderManager.LAYER_BLOCK_MAP) {

    /**
     * Get [tex] from `Minecraft.getMinecraft().getTextureMapBlocks()` automatically
     */
    constructor(tex: ResourceLocation) : this(Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(tex.toString()))

    /**
     * `i` is from 0-1 along the animation
     */
    override fun render(i: Float, particle: ParticleBase, color: Color,
                        worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, posX: Double, posY: Double, posZ: Double, skyLight: Int, blockLight: Int) {
        var uMin = texture.minU.toDouble()
        var uMax = texture.maxU.toDouble()
        var vMin = texture.minV.toDouble()
        var vMax = texture.maxV.toDouble()

        val radius = 0.1f * scale

        val vertOffsets = arrayOf(
                Vec3d((-rotationX * radius - rotationXY * radius).toDouble(), (-rotationZ * radius).toDouble(), (-rotationYZ * radius - rotationXZ * radius).toDouble()),
                Vec3d((-rotationX * radius + rotationXY * radius).toDouble(), ( rotationZ * radius).toDouble(), (-rotationYZ * radius + rotationXZ * radius).toDouble()),
                Vec3d(( rotationX * radius + rotationXY * radius).toDouble(), ( rotationZ * radius).toDouble(), ( rotationYZ * radius + rotationXZ * radius).toDouble()),
                Vec3d(( rotationX * radius - rotationXY * radius).toDouble(), (-rotationZ * radius).toDouble(), ( rotationYZ * radius - rotationXZ * radius).toDouble())
        )

        worldRendererIn.pos(posX.toDouble() + vertOffsets[0].xCoord, posY.toDouble() + vertOffsets[0].yCoord, posZ.toDouble() + vertOffsets[0].zCoord).tex(uMax, vMax).color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(posX.toDouble() + vertOffsets[1].xCoord, posY.toDouble() + vertOffsets[1].yCoord, posZ.toDouble() + vertOffsets[1].zCoord).tex(uMax, vMin).color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(posX.toDouble() + vertOffsets[2].xCoord, posY.toDouble() + vertOffsets[2].yCoord, posZ.toDouble() + vertOffsets[2].zCoord).tex(uMin, vMin).color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(posX.toDouble() + vertOffsets[3].xCoord, posY.toDouble() + vertOffsets[3].yCoord, posZ.toDouble() + vertOffsets[3].zCoord).tex(uMin, vMax).color(color.red/255f, color.green/255f, color.blue/255f, color.alpha/255f).lightmap(skyLight, blockLight).endVertex()
    }

}