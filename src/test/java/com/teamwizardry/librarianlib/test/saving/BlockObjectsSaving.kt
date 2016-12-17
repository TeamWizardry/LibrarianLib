package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.sendMessage
import com.teamwizardry.librarianlib.common.util.toNonnullList
import com.teamwizardry.librarianlib.common.util.vec
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
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class BlockObjectsSaving : BlockMod("saving_objects", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.color = kolors[ThreadLocalRandom.current().nextInt(kolors.size)]
            te.tag.setInteger(te.color.toString(), te.tag.getInteger(te.color.toString()) + 1)
            te.stack = playerIn.heldItemMainhand

            te.handler = ItemStackHandler(arrayOf(playerIn.heldItemMainhand, playerIn.heldItemOffhand).toNonnullList())
            te.vec3d = vec(hitX, hitY, hitZ)
            te.vec3i = pos
            te.vec2d = vec(hitX, hitZ)
            te.enum = side

            te.markDirty()
        } else {
            playerIn.sendMessage("Color: " + te.color)
            playerIn.sendMessage("Tag: " + te.tag)
            playerIn.sendMessage("Stack: " + te.stack)
            playerIn.sendMessage("Handler: " + te.handler?.slots)
            playerIn.sendMessage("Vec3d: " + te.vec3d)
            playerIn.sendMessage("Vec3i: " + te.vec3i)
            playerIn.sendMessage("Vec2d: " + te.vec2d)
            playerIn.sendMessage("Enum: " + te.enum)
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_objects")
    class TETest : TileMod() {
        @Save var color: Color = Color.BLACK
        @Save var tag: NBTTagCompound = NBTTagCompound()
        @Save var stack: ItemStack? = null
        @Save var handler: ItemStackHandler? = null
        @Save var vec3d: Vec3d = Vec3d.ZERO
        @Save var vec3i: Vec3i = Vec3i.NULL_VECTOR
        @Save var vec2d: Vec2d = Vec2d.ZERO
        @Save var enum: EnumFacing = EnumFacing.UP

    }

    companion object {
        val kolors = arrayOf(Color.RED, Color.BLUE, Color.GREEN)
    }
}
