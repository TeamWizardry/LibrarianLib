package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.LifetimeInterpBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.VariableBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import java.awt.Color

object FountainParticleSystem {
    private val system = ParticleSystem()

    private val isEnd = system.bind(1)
    private val position = system.bind(3)
    private val previousPosition = system.bind(3)
    private val velocity = system.bind(3)
    private val color = system.bind(4)
    private val lift = system.bind(1)

    init {
        reloadSystem()
        ParticleRenderManager.emitters.add(system)
        ParticleRenderManager.reloadHandlers.add(Runnable { reloadSystem() })
    }

    fun spawn(lifetime: Double, position: Vec3d, velocity: Vec3d, color: Color, lift: Double, isEnd: Boolean) {
        system.addParticle(lifetime,
                if(isEnd) 1.0 else 0.0,
                position.x, position.y, position.z,
                position.x, position.y, position.z,
                velocity.x, velocity.y, velocity.z,
                color.red/255.0, color.green/255.0, color.blue/255.0, color.alpha/255.0,
                lift
        )
    }

    fun reloadSystem() {
        system.updateModules.clear()
        system.postUpdateModules.clear()
        system.renderModules.clear()

        system.updateModules.add(VelocityUpdateModule(
                position,
                velocity,
                previousPosition
        ))

        system.updateModules.add(AccelerationUpdateModule(
                velocity,
                object: ReadParticleBinding {
                    override val size: Int = 3
                    private val lift = this@FountainParticleSystem.lift

                    override fun get(particle: DoubleArray, index: Int): Double {
                        if(index == 1) {
                            return lift.get(particle, 0)
                        }
                        return 0.0
                    }

                }
        ))
//        system.updateModules.add(BasicPhysicsUpdateModule(
//                position = position,
//                previousPosition = previousPosition,
//                velocity = velocity,
//                gravity = 0.01,
//                bounciness = 0.2,
//                friction = -0.015,
//                damping = 0.001
//        ))
//        system.renderModules.add(SpriteRenderModule(
//                sprite = "librarianlibtest:textures/particles/glow.png".toRl(),
//                blend = true,
//                previousPosition = previousPosition,
//                position = position,
//                color = color,
//                size = size,
//                alpha = LifetimeInterpBinding(system.lifetime, system.age, InterpFloatInOut(0.25f, 0.25f)),
//                blendFactors = GlStateManager.SourceFactor.SRC_ALPHA to GlStateManager.DestFactor.ONE,
//                depthMask = false
//        ))
        system.renderModules.add(GlLineBeamRenderModule(
                isEnd = isEnd,
                blend = true,
                previousPosition = previousPosition,
                position = position,
                color = color,
                size = 2f,
                alpha = LifetimeInterpBinding(system.lifetime, system.age, InterpFloatInOut(0f, 0.8f)),
                blendFactors = GlStateManager.SourceFactor.SRC_ALPHA to GlStateManager.DestFactor.ONE,
                depthMask = false
        ))
    }
}