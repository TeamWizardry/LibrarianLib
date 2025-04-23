package com.teamwizardry.librarianlib.testcore.fabric

import com.google.auto.service.AutoService
import com.teamwizardry.librarianlib.testcore.platform.TestCoreClientPlatform
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType

@AutoService(TestCoreClientPlatform::class)
internal class TestCoreClientPlatformImpl : TestCoreClientPlatform {
    override fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: ParticleFactory<T>) =
        ParticleFactoryRegistry.getInstance().register(type, factory)

    override fun <T : ParticleEffect> registerParticleFactory(
        type: ParticleType<T>,
        factory: TestCoreClientPlatform.DeferredParticleFactory<T>
    ) = ParticleFactoryRegistry.getInstance().register(type, factory::create)
}
