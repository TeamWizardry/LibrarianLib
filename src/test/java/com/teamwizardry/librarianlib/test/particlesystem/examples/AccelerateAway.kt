package com.teamwizardry.librarianlib.test.particlesystem.examples

import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.randomNormal
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.CallbackBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.AccelerationUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.VelocityUpdateModule
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import java.util.concurrent.ThreadLocalRandom

// Creates a lightning effect around the block
// https://i.imgur.com/OMRSuHP.png
object AccelerateAway: ParticleExample {
    override fun update(origin: Vec3d) {
        // details here aren't important. Just generate a list of points
        for(i in 0..2) {
            AccelerateAwaySystem.spawn(10.0,
                ThreadLocalRandom.current().nextDouble(1/16.0, 8/16.0),
                origin + randomNormal(),
                origin
            )
        }
    }
}

object AccelerateAwaySystem: ParticleSystem() {

    override fun configure() {
        // bind values in the particle array
        val size = bind(1)
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val origin = bind(3)

        updateModules.add(AccelerationUpdateModule(velocity, CallbackBinding(3) { particle, contents ->
            position.load(particle)
            origin.load(particle)
            for(i in 0 until 3) {
                contents[i] = position.contents[i] - origin.contents[i]
            }
        }))
        updateModules.add(VelocityUpdateModule(position, velocity, previousPosition))

        renderModules.add(SpriteRenderModule(
            sprite = ResourceLocation("minecraft", "textures/items/clay_ball.png"), // #balance,
            enableBlend = false,
            previousPosition = previousPosition,
            position = position,
            color = ConstantBinding(1.0, 1.0, 1.0, 1.0),
            size = size
        ))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, origin: Vec3d) {
        this.addParticle(lifetime,
            size,
            pos.x, pos.y, pos.z,
            pos.x, pos.y, pos.z,
            0.0, 0.0, 0.0,
            origin.x, origin.y, origin.z
        )
    }
}
