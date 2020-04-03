package com.teamwizardry.librarianlib.particles

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.client.renderer.Matrix4f

/**
 * A self-contained module that handles the rendering of a collection of particles.
 */
interface ParticleRenderModule {
    /**
     * Renders the passed [particles] into the world. If [prepModules] is not empty, they should be run in order before
     * any bindings are read, as they may initialize the values in some of those bindings. The renderer can assume the
     * GL transformation matrix has been set up such that raw world coordinates can be used.
     */
    fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>)
}