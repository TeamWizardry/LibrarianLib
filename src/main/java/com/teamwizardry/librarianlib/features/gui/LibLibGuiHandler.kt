package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.base.item.ItemModBook
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 2:30 PM on 2/27/18.
 */

object LibLibGuiHandler : IGuiHandler {
    const val ID_BOOK = 0

    private inline fun <reified T : Any> getStack(p: EntityPlayer): Pair<T, ItemStack>? {
        val isBlock = Block::class.java.isAssignableFrom(T::class.java)
        val isItem = Item::class.java.isAssignableFrom(T::class.java)

        var target: T? = tFromStack(p.heldItemMainhand, isItem, isBlock)
        if (target != null)
            return target to p.heldItemMainhand
        target = tFromStack(p.heldItemOffhand, isItem, isBlock)
        if (target != null)
            return target to p.heldItemOffhand

        return null
    }

    private inline fun <reified T : Any> tFromStack(stack: ItemStack, isItem: Boolean, isBlock: Boolean): T? {
        val item = stack.item
        return if (isItem && item is T)
            item
        else if (isBlock && item is ItemBlock) {
            val block = item.block
            if (block is T)
                block
            else null
        } else null
    }

    @SideOnly(Side.CLIENT)
    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        if (ID == ID_BOOK) {
            val (item, stack) = getStack<ItemModBook>(player) ?: return null
            return item.createGui(player, world, stack)
        }
        return null
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int): Any? {
        return null
    }
}
