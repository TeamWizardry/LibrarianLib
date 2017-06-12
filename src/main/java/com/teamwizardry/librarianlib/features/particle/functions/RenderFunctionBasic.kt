package com.teamwizardry.librarianlib.features.particle.functions

import com.teamwizardry.librarianlib.features.kotlin.x
import com.teamwizardry.librarianlib.features.kotlin.y
import com.teamwizardry.librarianlib.features.kotlin.z
import com.teamwizardry.librarianlib.features.particle.ParticleBase
import com.teamwizardry.librarianlib.features.particle.ParticleRenderLayer
import com.teamwizardry.librarianlib.features.particle.ParticleRenderManager
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle.cameraViewDir
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

/**
 * Render a particle from the block texture map.
 */
@SideOnly(Side.CLIENT)
class RenderFunctionBasic(val texture: TextureAtlasSprite, layer: ParticleRenderLayer) : RenderFunction(layer) {

    constructor(tex: ResourceLocation, layer: ParticleRenderLayer) : this(Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(tex.toString()) ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite, layer)

    /**
     * Get [tex] from `Minecraft.getMinecraft().getTextureMapBlocks()` automatically
     */
    constructor(tex: ResourceLocation, flatLayer: Boolean) : this(Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(tex.toString()) ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite, flatLayer)

    constructor(tex: TextureAtlasSprite, flatLayer: Boolean) : this(tex, if (flatLayer) ParticleRenderManager.LAYER_BLOCK_MAP else ParticleRenderManager.LAYER_BLOCK_MAP_ADDITIVE)

    /**
     * `i` is from 0-1 along the animation
     */
    override fun render(i: Float, particle: ParticleBase, color: Color, alpha: Float,
                        worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float,
                        scale: Float, rotation: Float, pos: Vec3d, skyLight: Int, blockLight: Int) {
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

        if (rotation != 0.0F) {
            val f9 = MathHelper.cos(rotation * 0.5F)
            val vec3d = cameraViewDir.scale(MathHelper.sin(rotation * 0.5F).toDouble())

            for (l in 0..3) {
                vertOffsets[l] = vec3d.scale(2.0 * vertOffsets[l].dotProduct(vec3d)).add(vertOffsets[l].scale((f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vertOffsets[l]).scale(2.0 * f9))
            }
        }

        worldRendererIn.pos(pos.x + vertOffsets[0].x, pos.y + vertOffsets[0].y, pos.z + vertOffsets[0].z).tex(uMax, vMax).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.x + vertOffsets[1].x, pos.y + vertOffsets[1].y, pos.z + vertOffsets[1].z).tex(uMax, vMin).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.x + vertOffsets[2].x, pos.y + vertOffsets[2].y, pos.z + vertOffsets[2].z).tex(uMin, vMin).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
        worldRendererIn.pos(pos.x + vertOffsets[3].x, pos.y + vertOffsets[3].y, pos.z + vertOffsets[3].z).tex(uMin, vMax).color(color.red / 255f, color.green / 255f, color.blue / 255f, alpha * color.alpha / 255f).lightmap(skyLight, blockLight).endVertex()
    }
}
