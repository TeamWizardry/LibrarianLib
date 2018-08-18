package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderManager
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
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class BlockParticleFountainTest: BlockModContainer("particle_fountain", Material.ROCK) {
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
        if(this.world.isRemote) {
            if (countdown <= 0) {
                countdown = 1
                (0 until 4).forEach {
                    spawnParticle()
                }
            }
            countdown--
        }
    }

    fun spawnParticle() {
        val pos = Vec3d(this.pos) + vec(0.5, 8.5, 0.5)
//        val normal = randomNormal()
//        val majorAxis = (normal cross randomNormal()).normalize()
//        val minorAxis = (normal cross majorAxis).normalize()
        val majorAxis = vec(1, 0, 0)
        val minorAxis = vec(0, 0, 1).rotatePitch(rand(0.0, 2*Math.PI).toFloat())

        FountainParticleSystem.spawn(200.0,
                pos,
                majorAxis,
                minorAxis,
                10.0,
                2.0,
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), rand(0.5, 1.0).toFloat()),
                0.5
        )
    }

    private fun generateLightning(start: Vec3d, end: Vec3d, iterations: Int, list: MutableList<Vec3d> = mutableListOf()): List<Vec3d> {
        if(iterations == 0) return list

        var center = (start+end)/2
        val distance = (start-end).lengthVector()/8
        center += vec(
                rand(-distance, distance),
                rand(-distance, distance),
                rand(-distance, distance)
        )

        val isFirst = list.isEmpty()
        if(isFirst) list.add(start)
        generateLightning(start, center, iterations-1, list)
        list.add(center)
        generateLightning(center, end, iterations-1, list)
        if(isFirst) list.add(end)

        return list
    }

    private fun rand(min: Double, max: Double): Double {
        if(min == max) return min
        return ThreadLocalRandom.current().nextDouble(min, max)
    }

    private fun rand(max: Double): Double {
        return ThreadLocalRandom.current().nextDouble(0.0, max)
    }
}
