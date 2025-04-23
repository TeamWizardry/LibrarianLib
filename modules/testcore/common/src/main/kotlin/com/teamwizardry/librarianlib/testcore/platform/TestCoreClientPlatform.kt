package com.teamwizardry.librarianlib.testcore.platform

import com.teamwizardry.librarianlib.core.util.ServiceLoaderHelper
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import kotlin.getValue

public interface TestCoreClientPlatform {
    // these are necessary because architectury's `ParticleProviderRegistry` doesn't work
    public fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: ParticleFactory<T>)
    public fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: DeferredParticleFactory<T>)

    public fun interface DeferredParticleFactory<T : ParticleEffect> {
        public fun create(spriteSheet: SpriteProvider): ParticleFactory<T>
    }

    public companion object {
        public val instance: TestCoreClientPlatform by ServiceLoaderHelper.required()
    }
}
