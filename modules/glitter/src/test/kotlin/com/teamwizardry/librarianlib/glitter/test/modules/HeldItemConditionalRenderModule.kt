package com.teamwizardry.librarianlib.glitter.test.modules

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Only runs the passed render module when the player is holding a specific item
 */
class HeldItemConditionalRenderModule(
    /**
     * What item ID the player must be holding in order for the particles to render
     */
    @JvmField val filter: Identifier,
    /**
     * The render module to run when the player is holding the item
     */
    @JvmField val wrapped: ParticleRenderModule,
): ParticleRenderModule {

    @Suppress("LocalVariableName")
    override fun render(
        context: WorldRenderContext,
        particles: List<DoubleArray>,
        prepModules: List<ParticleUpdateModule>
    ) {
        val isHoldingItem = Client.player!!.let {
            Registry.ITEM.getId(it.mainHandStack.item) == filter || Registry.ITEM.getId(it.offHandStack.item) == filter
        }
        if(isHoldingItem)
            wrapped.render(context, particles, prepModules)
    }
}
