package com.teamwizardry.librarianlib.test.particlesystem.examples

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.VelocityUpdateModule
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import java.awt.Color

object AnimatedParticles : ParticleExample {
    var counter = 0
    override fun update(origin: Vec3d) {
        if(counter++ % 20 == 0) {
            val index = if(Math.random() > 0.5) 16 else 0
            AnimatedSystem.spawn(20.0, 1.0, origin.add(0.0, 2.0, 0.0),
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1.0f),
                index, index + 11
            )
        }
    }
}

object AnimatedSystem : ParticleSystem() {

    override fun configure() {
        val size = bind(1)
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)
        val startIndex = bind(1)
        val endIndex = bind(1)

        updateModules.add(VelocityUpdateModule(
            position,
            velocity,
            previousPosition
        ))
        renderModules.add(SpriteRenderModule(
            sprite = ResourceLocation("librarianlibtest", "textures/particles/animated.png"),
            enableBlend = true,
            previousPosition = previousPosition,
            position = position,
            color = color,
            size = size,
            spriteSheetSize = 8,
            spriteIndex = EaseBinding(lifetime, age, easing = Easing.linear, bindingSize = 1, origin = startIndex, target = endIndex)
        ))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, color: Color, startIndex: Int, endIndex: Int) {
        this.addParticle(lifetime,
            size,
            pos.x, pos.y, pos.z,
            pos.x, pos.y, pos.z,
            0.1, 0.0, 0.0, // velocity
            color.red / 255.0, color.green / 255.0, color.blue / 255.0, 1.0,
            startIndex.toDouble(), endIndex.toDouble()
        )
    }
}
