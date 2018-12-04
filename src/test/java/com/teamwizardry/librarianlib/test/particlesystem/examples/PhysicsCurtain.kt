package com.teamwizardry.librarianlib.test.particlesystem.examples

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import java.awt.Color

object PhysicsCurtain : ParticleExample {
    override fun update(origin: Vec3d) {
//        PhysicsCurtainSystem.reload()
        for(i in 0 until 50) {
            PhysicsCurtainSystem.spawn(400.0, 2.0, origin.add(0.0, 2.0, 0.0),
                Color(0.3f, 0.3f, 0.4f, 1.0f),
                Color(0.3f, 0.3f, 0.4f, 1.0f)
            )
        }
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
        val color = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
            position,
            previousPosition,
            velocity = velocity,
            gravity = 0.05,
            enableCollision = true,
            bounciness = 0.95f,
            damping = 0.00f,
            friction = 0.00f
        ))
        updateModules.add(SetValueUpdateModule(color,
            EaseBinding(lifetime, age, easing = Easing.linear, bindingSize = 4, origin = colorFrom, target = colorTo)
        ))
        renderModules.add(SpriteRenderModule(
            sprite = ResourceLocation("librarianlibtest", "textures/particles/glow.png"),
            enableBlend = true,
            blendMode = BlendMode.ADDITIVE,
            previousPosition = previousPosition,
            position = position,
            color = color,
            size = size,
            depthMask = false
        ))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, colorFrom: Color, colorTo: Color) {
        this.addParticle(lifetime,
            size,
            pos.x, pos.y, pos.z,
            pos.x, pos.y, pos.z,
            RandomUtils.nextDouble(0.0, 2.0)-1, RandomUtils.nextDouble(0.25, 0.5), RandomUtils.nextDouble(0.0, 2.0)-1, // velocity
            colorFrom.red / 255.0, colorFrom.green / 255.0, colorFrom.blue / 255.0, 1.0,
            colorTo.red / 255.0, colorTo.green / 255.0, colorTo.blue / 255.0, 0.0
        )
    }
}
