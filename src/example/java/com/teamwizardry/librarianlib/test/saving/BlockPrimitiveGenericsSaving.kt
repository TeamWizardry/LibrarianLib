package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.kotlin.sendMessage
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class BlockPrimitiveGenericsSaving : BlockMod("saving_primitiveGenerics", Material.CACTUS), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = worldIn.getTileEntity(pos!!)!! as TETest
        if (!worldIn.isRemote) {
            te.map[facing] = (te.map[facing] ?: 0) + 1
            te.list.add((te.list.lastOrNull() ?: 0) + 1)
            if(te.list.size > 5) te.list.clear()
            te.linkedList.add((te.linkedList.lastOrNull() ?: 0) + 1)
            if(te.linkedList.size > 5) te.linkedList.clear()
            te.deque.addFirst(te.deque.removeLast())
            te.set.add(facing)
            if(te.set.size > 3) te.set.clear()
            te.markDirty()
        } else {
            playerIn.sendMessage("HashMap<EnumFacing, Int>: [" + te.map.map { "${it.key}:${it.value}" }.joinToString(", ") + "]")
            playerIn.sendMessage("List<Int>: [" + te.list.joinToString(", ") + "]")
            playerIn.sendMessage("Deque<EnumFacing>: [" + te.deque.joinToString(", ") + "]")
            playerIn.sendMessage("Set<EnumFacing>: [" + te.set.joinToString(", ") + "]")
            playerIn.sendMessage("LinkedList<Int>: [" + te.linkedList.joinToString(", ") + "]")
        }
        return true
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TETest()
    }

    @TileRegister("saving_primitiveGenerics")
    class TETest : TileMod() {
        @Save val map: HashMap<EnumFacing, Int> = HashMap()
        @Save val list: MutableList<Int> = ArrayList()
        @Save val linkedList: LinkedList<Int> = LinkedList() // can crash due to being interpreted as a Deque
        @Save val deque: Deque<EnumFacing> = ArrayDeque(EnumFacing.values().asList())
        @Save val set: MutableSet<EnumFacing> = HashSet()
    }
}
