package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.InterpBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.apache.commons.lang3.RandomUtils
import org.lwjgl.util.mapped.MappedHelper.setup
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class BlockParticleFountainTest : BlockModContainer("particle_fountain", Material.ROCK) {
    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TEContainer()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        ParticleRenderManager.needsReload = true
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }
}


@TileRegister("particle_fountain")
class TEContainer : TileMod(), ITickable {
    var countdown = 0

    override fun update() {
        if (this.world.isRemote) {
            if (countdown <= 0) {
                countdown = 2
                repeat(1) {
                    val pos = Vec3d(this.pos) + vec(0.5, 8.5, 0.5)

                    CondensedFountainSystem.spawn(100.0,
                            pos,
                            Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1f),
                            0.5
                    )
                }
            }
            countdown--
        }
    }

    object CondensedFountainSystem {
        private val system: ParticleSystem = ParticleSystem().setup(Consumer {
            it.addUpdateModules(
                    BasicPhysicsUpdateModule(
                            position,
                            previousPosition,
                            velocity = velocity,
                            initVelocity = initVelocity,
                            gravity = 0.1,
                            enableCollision = true,
                            bounciness = 0.3f,
                            friction = 0.4f))
                    .addRenderModules(SpriteRenderModule(
                            sprite = "librarianlibtest:textures/particles/glow.png".toRl(),
                            previousPosition = previousPosition,
                            position = position,
                            color = color,
                            size = size,
                            alphaMultiplier = InterpBinding(it.lifetime, it.age, interp = InterpFloatInOut(0.1f, 0.3f)),
                            blendMode = BlendMode.ADDITIVE
                    ))
        })

        private val position = system.bind(3)
        private val previousPosition = system.bind(3)
        private val color = system.bind(4)
        private val size = system.bind(1)
        private val velocity = system.bind(3)
        private val initVelocity = system.bind(3)

        fun spawn(lifetime: Double, position: Vec3d, color: Color, size: Double) {
            system.addParticle(lifetime,
                    position.x, position.y, position.z, // origin
                    color.red / 255.0, color.green / 255.0, color.blue / 255.0, color.alpha / 255.0, // color
                    size, // size
                    0.0, 0.0, 0.0, // velocity
                    RandomUtils.nextDouble(0.0, 2.0) - 1.0, RandomUtils.nextDouble(0.0, 2.0) - 2.0, RandomUtils.nextDouble(0.0, 2.0) - 1.0 // initVelocity
            )
        }
    }

    fun spawnParticle() {
        val pos = Vec3d(this.pos) + vec(0.5, 8.5, 0.5)
//        val normal = randomNormal()
//        val majorAxis = (normal cross randomNormal()).normalize()
//        val minorAxis = (normal cross majorAxis).normalize()
        val majorAxis = vec(1, 0, 0)
        val minorAxis = vec(0, 0, 1).rotatePitch(rand(0.0, 2 * Math.PI).toFloat())

        FountainParticleSystem.spawn(100.0,
                pos,// + vec(RandomUtils.nextDouble(0.0, 100.0) - 50.0, RandomUtils.nextDouble(0.0, 100.0) - 50.0, RandomUtils.nextDouble(0.0, 100.0) - 50.0),
                majorAxis,
                minorAxis,
                10.0,
                2.0,
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 1f),
                0.5
        )
    }

    private fun generateLightning(start: Vec3d, end: Vec3d, iterations: Int, list: MutableList<Vec3d> = mutableListOf()): List<Vec3d> {
        if (iterations == 0) return list

        var center = (start + end) / 2
        val distance = (start - end).length() / 8
        center += vec(
                rand(-distance, distance),
                rand(-distance, distance),
                rand(-distance, distance)
        )

        val isFirst = list.isEmpty()
        if (isFirst) list.add(start)
        generateLightning(start, center, iterations - 1, list)
        list.add(center)
        generateLightning(center, end, iterations - 1, list)
        if (isFirst) list.add(end)

        return list
    }

    private fun rand(min: Double, max: Double): Double {
        if (min == max) return min
        return ThreadLocalRandom.current().nextDouble(min, max)
    }

    private fun rand(max: Double): Double {
        return ThreadLocalRandom.current().nextDouble(0.0, max)
    }
}
