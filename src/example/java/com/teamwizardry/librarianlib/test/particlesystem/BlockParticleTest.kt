package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.particlesystem.examples.PhysicsCurtain
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
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class BlockParticleTest : BlockModContainer("particle_test", Material.ROCK) {
    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TEContainer()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = (worldIn.getTileEntity(pos) as? TEContainer)
        if (tile != null && worldIn.isRemote) tile.example += 1

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }
}

@TileRegister("particle_fountain")
class TEContainer : TileMod(), ITickable {
    @Save
    var example = 0

    override fun update() {
        if (this.world.isRemote) {
            ClientRunnable.run {
                particleExamples[example % particleExamples.size].update(Vec3d(pos) + vec(0.5, 2.5, 0.5))
            }
        }
    }
}

@SideOnly(Side.CLIENT)
val particleExamples = listOf(
        PhysicsCurtain
)
