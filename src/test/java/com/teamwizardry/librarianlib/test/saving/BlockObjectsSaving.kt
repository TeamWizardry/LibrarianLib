package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.kotlin.sendMessage
import com.teamwizardry.librarianlib.features.kotlin.set
import com.teamwizardry.librarianlib.features.kotlin.toNonnullList
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.items.ItemStackHandler
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class BlockObjectsSaving : BlockMod("saving_objects", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.color = kolors[ThreadLocalRandom.current().nextInt(kolors.size)]
            te.tag.setInteger(te.color.toString(), te.tag.getInteger(te.color.toString()) + 1)
            te.stack = playerIn.heldItemMainhand

            te.handler = ItemStackHandler(arrayOf(playerIn.heldItemMainhand, playerIn.heldItemOffhand).toNonnullList())
            te.vec3d = vec(hitX, hitY, hitZ)
            te.vec3i = pos
            te.vec2d = vec(hitX, hitZ)
            te.enum = facing
            te.serializable.isHawt = !te.serializable.isHawt
            te.privatePropertyTestAccess++

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
            playerIn.sendMessage("INBTSerializable: " + if(te.serializable.isHawt) "is hawt" else "isn't hawt")
            playerIn.sendMessage("Private Property: " + te.privatePropertyTestAccess)
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
        @Save var stack: ItemStack = ItemStack.EMPTY
        @Save var handler: ItemStackHandler? = null
        @Save var vec3d: Vec3d = Vec3d.ZERO
        @Save var vec3i: Vec3i = Vec3i.NULL_VECTOR
        @Save var vec2d: Vec2d = Vec2d.ZERO
        @Save var enum: EnumFacing = EnumFacing.UP
        @Save var serializable: INBTSerializableTest = INBTSerializableTest()
        @Save private var privateProperty: Int = 0
        var privatePropertyTestAccess: Int
            get() = privateProperty
            set(value) { privateProperty = value }
    }

    class INBTSerializableTest: INBTSerializable<NBTTagCompound> {
        var isHawt = false

        override fun deserializeNBT(nbt: NBTTagCompound) {
            val amIHawt = (nbt["amIHawt"] as NBTTagString?)?.string
            isHawt = amIHawt == "yes"
        }

        override fun serializeNBT(): NBTTagCompound {
            val tag = NBTTagCompound()
            tag["amIHawt"] = NBTTagString(if(isHawt) "yes" else "no")
            return tag
        }

    }

    companion object {
        val kolors = arrayOf(Color.RED, Color.BLUE, Color.GREEN)
    }
}
