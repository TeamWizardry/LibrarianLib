package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadOnlyParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.VariableBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.AccelerationUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.CollisionUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.util.math.Vec3d
import java.awt.Color

object FountainParticleSystem {
    private val system = ParticleSystem()

    private val position = system.bind(3)
    private val previousPosition = system.bind(3)
    private val velocity = system.bind(3)
    private val color = system.bind(4)
    private val size = system.bind(1)

    init {
        reloadSystem()
        ParticleRenderManager.emitters.add(system)
        ParticleRenderManager.reloadHandlers.add(Runnable { reloadSystem() })
    }

    fun spawn(lifetime: Double, position: Vec3d, velocity: Vec3d, color: Color, size: Double) {
        system.addParticle(lifetime,
                position.x, position.y, position.z,
                position.x, position.y, position.z,
                velocity.x, velocity.y, velocity.z,
                color.red/255.0, color.green/255.0, color.blue/255.0, color.alpha/255.0,
                size
        )
    }

    fun reloadSystem() {
        system.updateModules.clear()
        system.renderModules.clear()

        system.updateModules.add(AccelerationUpdateModule(
                velocity,
                ConstantBinding(0.0, -0.04, 0.0)
        ))
        system.updateModules.add(SetValueUpdateModule(
                target = previousPosition,
                source = position
        ))
        val impactNormal = VariableBinding(3)
        val friction = VariableBinding(3)
        val impactFraction = VariableBinding(1)
        system.updateModules.add(CollisionUpdateModule(
                position,
                velocity,
                position,
                impactNormal,
                friction,
                impactFraction
        ))
        system.updateModules.add(SetValueUpdateModule(
                target = velocity,
                source = object: ReadOnlyParticleBinding {
                    private val pVelocity = velocity
                    private val pImpactNormal = impactNormal
                    private val pFriction = friction

                    override fun getSize(): Int = 3

                    override fun get(particle: DoubleArray, index: Int): Double {
                        return pVelocity.get(particle, index) *
                                (1-pImpactNormal.get(particle, index)*1.4) *
                                (1-pFriction.get(particle, index)*0.2)
                    }
                }
        ))
        system.updateModules.add(CollisionUpdateModule(
                position,
                object: ReadOnlyParticleBinding {
                    private val pVelocity = velocity
                    private val pImpactFraction = impactFraction

                    override fun getSize(): Int = 3

                    override fun get(particle: DoubleArray, index: Int): Double {
                        return pVelocity.get(particle, index) *
                                (1-pImpactFraction.get(particle, 0))
                    }
                },
                position,
                impactNormal,
                null,
                impactFraction
        ))
        system.updateModules.add(SetValueUpdateModule(
                target = velocity,
                source = object: ReadOnlyParticleBinding {
                    private val pVelocity = velocity
                    private val pImpactNormal = impactNormal
                    private val pFriction = friction

                    override fun getSize(): Int = 3

                    override fun get(particle: DoubleArray, index: Int): Double {
                        return pVelocity.get(particle, index) *
                                (1-pImpactNormal.get(particle, index)*1.4) *
                                (1-pFriction.get(particle, index)*0.2)
                    }
                }
        ))

        system.renderModules.add(SpriteRenderModule(
                previousPosition = previousPosition,
                position = position,
                color = color,
                size = size
        ))
    }
}