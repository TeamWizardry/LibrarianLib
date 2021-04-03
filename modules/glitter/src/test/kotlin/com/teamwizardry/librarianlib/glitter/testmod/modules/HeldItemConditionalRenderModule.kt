package com.teamwizardry.librarianlib.glitter.testmod.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 * Only runs the passed render module when the player is holding a specific item
 */
class HeldItemConditionalRenderModule(
    /**
     * What item ID the player must be holding in order for the particles to render
     */
    @JvmField val filter: ResourceLocation,
    /**
     * The render module to run when the player is holding the item
     */
    @JvmField val wrapped: ParticleRenderModule,
): ParticleRenderModule {

    @Suppress("LocalVariableName")
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        val isHoldingItem = Client.player!!.let {
            it.heldItemMainhand.item.registryName == filter || it.heldItemOffhand.item.registryName == filter
        }
        if(isHoldingItem)
            wrapped.render(matrixStack, projectionMatrix, particles, prepModules)
    }
}
