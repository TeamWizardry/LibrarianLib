package com.teamwizardry.librarianlib.test.chunkdata

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.chunkdata.ChunkData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

/**
 * TODO: Document file ItemChunkData
 *
 * Created by TheCodeWarrior
 */
class ItemChunkData : ItemMod("chunkdata") {
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        val data = ChunkData.get(worldIn, ChunkPos(pos), TestChunkData::class.java)
        if(data != null) {
            if(!worldIn.isRemote) {
                data.clicks++
                data.markDirty()
                player.sendStatusMessage(TextComponentString("[S] Clicks: ${data.clicks}"), false)
            } else {
                player.sendStatusMessage(TextComponentString("[C] Clicks: ${data.clicks}"), false)
            }

        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}
