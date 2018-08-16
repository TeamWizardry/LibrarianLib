package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
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
        val pos = Vec3d(this.pos) + vec(0.5, 1.5, 0.5) + vec(0, 0, (Math.random()-0.5)*5)
//        val vel = vec(0, 0, 0.2 + (Math.random()-0.5)*0.1)
//                .rotatePitch((Math.random()*Math.PI/8 + Math.PI/8).toFloat())
//                .rotateYaw(((Math.random()-0.5)*Math.PI/4).toFloat())
        val vel = vec(0.05, 0, (Math.random()-0.5) * 0.01)

        FountainParticleSystem.spawn(200.0,
                pos,
                vel,
                Color(Math.random().toFloat()*0.1f, Math.random().toFloat()*0.1f, (Math.random()*0.5+0.5).toFloat(), 0.5f),
                10.0
        )
    }
}
