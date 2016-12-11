package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class BlockObjectArraysSaving : BlockMod("saving_objectArrays", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking) {
                te.index = (te.index + 1) % 3
            } else {
                te.color[te.index] = kolors[ThreadLocalRandom.current().nextInt(kolors.size)]
                te.tag[te.index].setInteger(te.color.toString(), te.tag[te.index].getInteger(te.color.toString()) + 1)
                te.stack[te.index] = playerIn.heldItemMainhand

                te.handler[te.index] = ItemStackHandler(arrayOf(playerIn.heldItemMainhand, playerIn.heldItemOffhand))
                te.vec3d[te.index] = Vec3d(hitX.toDouble(), hitY.toDouble(), hitZ.toDouble())
                te.vec3i[te.index] = pos
                te.vec2d[te.index] = Vec2d(hitX.toDouble(), hitZ.toDouble())

                te.enum[te.index] = side
            }
            te.markDirty()
        } else {
            playerIn.sendMessage("Color: " + Arrays.toString(te.color))
            playerIn.sendMessage("Tag: " + Arrays.toString(te.tag))
            playerIn.sendMessage("Stack: " + Arrays.toString(te.stack))
            playerIn.sendMessage("Handler: " + Arrays.toString(te.handler.map { it?.slots }.toTypedArray()))
            playerIn.sendMessage("Vec3d: " + Arrays.toString(te.vec3d))
            playerIn.sendMessage("Vec3i: " + Arrays.toString(te.vec3i))
            playerIn.sendMessage("Vec2d: " + Arrays.toString(te.vec2d))
            playerIn.sendMessage("Enum: " + Arrays.toString(te.enum))
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_objectArrays")
    class TETest : TileMod() {
        @Save var index: Int = 0

        @Save var color: Array<Color> = arrayOf(Color.BLACK, Color.BLACK, Color.BLACK)
        @Save var tag: Array<NBTTagCompound> = arrayOf(NBTTagCompound(), NBTTagCompound(), NBTTagCompound())
        @Save var stack: Array<ItemStack?> = arrayOfNulls(3)
        @Save var handler: Array<ItemStackHandler?> = arrayOfNulls(3)
        @Save var vec3d: Array<Vec3d> = arrayOf(Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO)
        @Save var vec3i: Array<Vec3i> = arrayOf(Vec3i.NULL_VECTOR, Vec3i.NULL_VECTOR, Vec3i.NULL_VECTOR)
        @Save var vec2d: Array<Vec2d> = arrayOf(Vec2d.ZERO, Vec2d.ZERO, Vec2d.ZERO)
        @Save var enum: Array<EnumFacing> = arrayOf(EnumFacing.UP, EnumFacing.UP, EnumFacing.UP)

    }

    companion object {
        val kolors = arrayOf(Color.RED, Color.BLUE, Color.GREEN)
    }
}
