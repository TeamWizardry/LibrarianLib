package com.teamwizardry.librarianlib.test.particlesystem.examples

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import java.awt.Color

object PhysicsCurtain : ParticleExample {
    override fun update(origin: Vec3d) {
        PhysicsCurtainSystem.reload()
        PhysicsCurtainSystem.spawn(100.0, 1.0, origin.add(0.0, 5.0, 0.0),
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1.0f),
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1.0f)
                )
    }
}

object PhysicsCurtainSystem : ParticleSystem() {

    override fun configure() {
        val size = bind(1)
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val colorFrom = bind(4)
        val colorTo = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
                position,
                previousPosition,
                velocity = velocity,
                gravity = 0.1,
                enableCollision = true,
                bounciness = 0.3f,
                damping = 0.01f,
                friction = 0.4f
        ))

        renderModules.add(SpriteRenderModule(
                sprite = ResourceLocation("minecraft", "textures/items/clay_ball.png"), // #balance,
                enableBlend = true,
                previousPosition = previousPosition,
                position = position,
                color = EaseBinding(lifetime, age, easing = Easing.linear, bindingSize = 4, origin = colorFrom, target = colorTo),
                size = size
        ))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, colorFrom: Color, colorTo: Color) {
        this.addParticle(lifetime,
                size,
                pos.x, pos.y, pos.z,
                pos.x, pos.y, pos.z,
                RandomUtils.nextDouble(0.0, 2.0) - 1.0, RandomUtils.nextDouble(0.0, 2.0) - 2.0, RandomUtils.nextDouble(0.0, 2.0) - 1.0, // velocity
                colorFrom.red / 255.0, colorFrom.green / 255.0, colorFrom.blue / 255.0, 1.0,
                colorTo.red / 255.0, colorTo.green / 255.0, colorTo.blue / 255.0, 0.0
        )
    }
}
