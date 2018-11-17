package com.teamwizardry.librarianlib.test.worlddata

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.worlddata.WorldData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

/**
 * TODO: Document file ItemChunkData
 *
 * Created by TheCodeWarrior
 */
class ItemWorldData : ItemMod("worlddata") {
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        val data = WorldData.get(worldIn, TestWorldData::class.java)
        if(data != null) {
            if(!worldIn.isRemote) {
                player.sendStatusMessage(TextComponentString("[S] Jumps: ${data.jumps}"), false)
            } else {
                player.sendStatusMessage(TextComponentString("[C] Jumps: ${data.jumps}"), false)
            }
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }
}
