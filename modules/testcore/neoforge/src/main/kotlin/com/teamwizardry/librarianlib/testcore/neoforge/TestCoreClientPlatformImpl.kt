package com.teamwizardry.librarianlib.testcore.neoforge

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.testcore.platform.TestCoreClientPlatform
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent


@AutoService(TestCoreClientPlatform::class)
internal class TestCoreClientPlatformImpl : TestCoreClientPlatform {
    val registrations = mutableListOf<Pair<ParticleType<*>, (SpriteProvider) -> ParticleFactory<*>>>()

    override fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: ParticleFactory<T>) {
        registrations.add(type to { factory })
    }

    override fun <T : ParticleEffect> registerParticleFactory(
        type: ParticleType<T>,
        factory: TestCoreClientPlatform.DeferredParticleFactory<T>
    ) {
        registrations.add(type to factory::create)
    }

    @SubscribeEvent
    fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        for ((type, factory) in registrations) {
            @Suppress("UNCHECKED_CAST")
            event.registerSpriteSet(type as ParticleType<ParticleEffect>) { spriteProvider ->
                factory(spriteProvider) as ParticleFactory<ParticleEffect>
            }
        }
    }
}
