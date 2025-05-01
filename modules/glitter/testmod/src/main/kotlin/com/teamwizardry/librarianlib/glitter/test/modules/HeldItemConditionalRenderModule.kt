package com.teamwizardry.librarianlib.glitter.test.modules

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.glitter.ParticleRenderContext
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

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
        context: ParticleRenderContext,
        particles: List<DoubleArray>,
        prepModules: List<ParticleUpdateModule>
    ) {
        val isHoldingItem = Client.player!!.let { player ->
            player.isHolding {
                Registries.ITEM.getId(it.item) == filter
            }
        }
        if(isHoldingItem)
            wrapped.render(context, particles, prepModules)
    }
}
