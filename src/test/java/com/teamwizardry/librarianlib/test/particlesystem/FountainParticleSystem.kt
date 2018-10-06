package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.InterpBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.paths.EllipsePath
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import java.awt.Color

object FountainParticleSystem {
    private val system = ParticleSystem()

    private val origin = system.bind(3)
    private val position = system.bind(3)
    private val previousPosition = system.bind(3)
    private val majorAxis = system.bind(3)
    private val minorAxis = system.bind(3)
    private val majorRadius = system.bind(1)
    private val minorRadius = system.bind(1)
    private val color = system.bind(4)
    private val size = system.bind(1)
    private val velocity = system.bind(3)
    private val initVelocity = system.bind(3)

    init {
        reloadSystem()
        ParticleRenderManager.systems.add(system)
        ParticleRenderManager.reloadHandlers.add(Runnable { reloadSystem() })
    }

    fun spawn(lifetime: Double, position: Vec3d, majorAxis: Vec3d, minorAxis: Vec3d,
              majorRadius: Double, minorRadius: Double, color: Color, size: Double) {
        reloadSystem()
        system.addParticle(lifetime,
                position.x, position.y, position.z, // origin
                position.x + majorAxis.x, position.y + majorAxis.y, position.z + majorAxis.z, // position
                position.x + majorAxis.x, position.y + majorAxis.y, position.z + majorAxis.z, // previous position
                majorAxis.x, majorAxis.y, majorAxis.z, // majorAxis
                minorAxis.x, minorAxis.y, minorAxis.z, // minorAxis
                majorRadius, // majorRadius
                minorRadius, // minorRadius
                color.red / 255.0, color.green / 255.0, color.blue / 255.0, color.alpha / 255.0, // color
                size, // size
                0.0, 0.0, 0.0,
                RandomUtils.nextDouble(0.0, 2.0) - 1.0, RandomUtils.nextDouble(0.0, 2.0) - 2.0, RandomUtils.nextDouble(0.0, 2.0) - 1.0 // initVelocity
        )
    }

    fun reloadSystem() {
        system.updateModules.clear()
        system.postUpdateModules.clear()
        system.renderModules.clear()

//        system.updateModules.add(VelocityUpdateModule(
//                position,
//
//                previousPosition
//        ))
        val ellipse = EllipsePath(
                majorAxis,
                minorAxis,
                majorRadius,
                minorRadius
        )
       // system.updateModules.add(SetValueUpdateModule(
       //         previousPosition,
       //         position
       // ))
        system.updateModules.add(BasicPhysicsUpdateModule(
                position,
                previousPosition,
                velocity = velocity,
                initVelocity = initVelocity,
                gravity = 0.1,
                enableCollision = true,
                bounciness = 0.3f,
                friction = 0.4f
        ))
        //   system.updateModules.add(SetValueUpdateModule(
        //           position,
        //           PathBinding(
        //                   lifetime = system.lifetime,
        //                   age = system.age,
        //                   origin = origin,
        //                   timescale = ConstantBinding(0.5),
        //                   path = ellipse,
        //                   easing = Easing.linear
        //           )
        //   ))

        //  system.updateModules.add(SetValueUpdateModule(
        //          position,
        //          EaseBinding(
        //                  lifetime = system.lifetime,
        //                  age = system.age,
        //                  easing = Easing.easeOutQuint,
        //                  origin = origin,
        //                  target = position,
        //                  bindingSize = 3
        //          )
        //  ))

//        system.updateModules.add(AccelerationUpdateModule(
//                velocity,
//                object: ReadParticleBinding {
//                    override val size: Int = 3
//                    private val lift = this@FountainParticleSystem.lift
//
//                    override fun get(particle: DoubleArray, index: Int): Double {
//                        if(index == 1) {
//                            return lift.get(particle, 0)
//                        }
//                        return 0.0
//                    }
//
//                }
//        ))
//        system.updateModules.add(BasicPhysicsUpdateModule(
//                position = position,
//                previousPosition = previousPosition,
//                velocity = velocity,
//                gravity = 0.01,
//                bounciness = 0.2,
//                friction = -0.015,
//                damping = 0.001
//        ))
        system.renderModules.add(SpriteRenderModule(
                sprite = "librarianlibtest:textures/particles/glow.png".toRl(),
                previousPosition = previousPosition,
                position = position,
                color = color,
                size = size,
                alphaMultiplier = InterpBinding(system.lifetime, system.age, interp = InterpFloatInOut(0.1f, 0.3f)),
                blendMode = BlendMode.ADDITIVE
        ))
        val facingVector = object : ReadParticleBinding {
            override val size: Int = 3
            override fun get(particle: DoubleArray, index: Int): Double {
                return origin[particle, index] - position[particle, index]
            }
        }
//        system.renderModules.add(GlLineBeamRenderModule(
//                isEnd = isEnd,
//                enableBlend = true,
//                previousPosition = previousPosition,
//                position = position,
//                color = color,
//                size = 2f,
//                alpha = LifetimeInterpBinding(system.lifetime, system.age, InterpFloatInOut(0f, 0.8f)),
//                blendFactors = GlStateManager.SourceFactor.SRC_ALPHA to GlStateManager.DestFactor.ONE,
//                depthMask = false
//        ))
    }
}