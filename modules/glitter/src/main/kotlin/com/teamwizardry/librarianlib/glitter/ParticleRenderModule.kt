package com.teamwizardry.librarianlib.glitter

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

/**
 * A self-contained module that handles the rendering of a collection of particles.
 */
public interface ParticleRenderModule {
    /**
     * Renders the passed [particles] into the world. If [prepModules] is not empty, they should be run in order before
     * any bindings are read, as they may initialize the values in some of those bindings. The renderer can assume the
     * GL transformation matrix has been set up such that raw world coordinates can be used.
     */
    public fun render(context: WorldRenderContext, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {}

    /**
     * Renders the passed [particles] into the world. If [prepModules] is not empty, they should be run in order before
     * any bindings are read, as they may initialize the values in some of those bindings. The renderer can assume the
     * GL transformation matrix has been set up such that raw world coordinates can be used.
     */
    public fun renderDirect(context: WorldRenderContext, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {}
}