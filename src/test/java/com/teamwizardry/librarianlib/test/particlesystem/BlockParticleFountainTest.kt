package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
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
                countdown = 2
//            (0 until 1).forEach {
                spawnParticle()
//            }
            }
            countdown--
        }
    }

    fun spawnParticle() {
        val pos = Vec3d(this.pos) + vec(0.2, 4.5, 0.5)
//        val normal = vec(0, 1, 0)
//                .rotatePitch(((Math.random()-0.5)*Math.PI*2).toFloat())
//                .rotateYaw(((Math.random()-0.5)*Math.PI*2).toFloat())
        var end = pos
//        end += vec(rand(-3.0, 3.0), 0, rand(-3.0, 3.0))
//                .rotateYaw(((Math.random() - 0.5) * Math.PI * 2).toFloat())
        end += vec(-10.4, 0, 0)


        val lightning = generateLightning(pos, end, 4)

        lightning.forEachIndexed { i, it ->
            val coeff = max(0.0, sin((i.toDouble()/(lightning.size-1) * Math.PI)))
            val maxVel = coeff * 0.2/20
            FountainParticleSystem.spawn(20.0,
                    it,
                    vec(rand(-maxVel, maxVel), rand(-maxVel, maxVel), rand(-maxVel, maxVel)),
                    Color(1f, 1f, 1f, rand(0.5, 1.0).toFloat()),
                    //Color(rand(0.1).toFloat(), rand(0.5, 1.0).toFloat(), rand(0.1).toFloat(), 0.5f),
                    0.003 * coeff,
                    i == lightning.size-1
            )
        }
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
