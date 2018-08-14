package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11

class SpriteRenderModule(
        val previousPosition: ParticleBinding,
        val position: ParticleBinding,
        val color: ParticleBinding,
        val size: ParticleBinding,
        val facingVector: ParticleBinding? = null
): ParticleRenderModule {
    override fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
//        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

        val player = Minecraft.getMinecraft().player
        GlStateManager.pushAttrib()
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT)
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F)
        GlStateManager.disableLighting()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableCull()


        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        val playerYaw = player.rotationYaw.toDouble()//ClientTickHandler.interpWorldPartialTicks(player.prevCameraYaw, player.cameraYaw)
        val playerPitch = player.rotationPitch.toDouble()//ClientTickHandler.interpWorldPartialTicks(player.prevCameraPitch, player.cameraPitch)

        val yawX = Math.sin(-Math.toRadians(playerYaw+180))
        val yawZ = Math.cos(-Math.toRadians(playerYaw+180))
        val yawScale = Math.sin(-Math.toRadians(playerPitch))
        val pitchY = Math.cos(-Math.toRadians(playerPitch))

        var iHatX = yawZ
        var iHatY = 0.0
        var iHatZ = -yawX

        var jHatX = yawX*yawScale
        var jHatY = pitchY
        var jHatZ = yawZ*yawScale


        particles.forEach { particle ->
            prepModules.forEach {
                it.update(particle)
            }
            if(facingVector != null) {
                val facing = player.getLook(ClientTickHandler.worldPartialTicks)
                // x axis, facing • (0, 1, 0)
                iHatX = -facingVector.get(particle, 2)
                iHatY = 0.0
                iHatZ = facingVector.get(particle, 0)
                val iHatInvLength = MathHelper.fastInvSqrt(iHatX * iHatX + iHatY * iHatY + iHatZ * iHatZ)
                iHatX *= iHatInvLength
                iHatY *= iHatInvLength
                iHatZ *= iHatInvLength

                // y axis, facing • iHat
                jHatX = facingVector.get(particle, 1) * facingVector.get(particle, 0)
                jHatY = facingVector.get(particle, 2) * -facingVector.get(particle, 2) - facingVector.get(particle, 0) * facingVector.get(particle, 0)
                jHatZ = facingVector.get(particle, 1) * facingVector.get(particle, 2)
                val jHatInvLength = MathHelper.fastInvSqrt(jHatX * jHatX + jHatY * jHatY + jHatZ * jHatZ)
                jHatX *= -jHatInvLength
                jHatY *= -jHatInvLength
                jHatZ *= -jHatInvLength
            }

            val size = this.size.get(particle, 0)/2
            val localIHatX = iHatX * size
            val localIHatY = iHatY * size
            val localIHatZ = iHatZ * size
            val localJHatX = jHatX * size
            val localJHatY = jHatY * size
            val localJHatZ = jHatZ * size

            val x = ClientTickHandler.interpWorldPartialTicks(previousPosition.get(particle, 0), position.get(particle, 0))
            val y = ClientTickHandler.interpWorldPartialTicks(previousPosition.get(particle, 1), position.get(particle, 1))
            val z = ClientTickHandler.interpWorldPartialTicks(previousPosition.get(particle, 2), position.get(particle, 2))

            val r = color.get(particle, 0).toFloat()
            val g = color.get(particle, 1).toFloat()
            val b = color.get(particle, 2).toFloat()
            val a = color.get(particle, 3).toFloat()

            vb.pos(x-localIHatX-localJHatX, y-localIHatY-localJHatY, z-localIHatZ-localJHatZ).color(r, g, b, a).endVertex()
            vb.pos(x+localIHatX-localJHatX, y+localIHatY-localJHatY, z+localIHatZ-localJHatZ).color(r, g, b, a).endVertex()
            vb.pos(x+localIHatX+localJHatX, y+localIHatY+localJHatY, z+localIHatZ+localJHatZ).color(r, g, b, a).endVertex()
            vb.pos(x-localIHatX+localJHatX, y-localIHatY+localJHatY, z-localIHatZ+localJHatZ).color(r, g, b, a).endVertex()
        }

        tessellator.draw()

        GlStateManager.enableCull()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.disableBlend()
        GL11.glPopAttrib()
        GlStateManager.popAttrib()
    }
}