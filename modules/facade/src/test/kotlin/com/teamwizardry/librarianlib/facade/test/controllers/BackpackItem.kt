package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.test.LibrarianLibFacadeTestMod
import com.teamwizardry.librarianlib.testcore.objects.ITestItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.ActionResult
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class BackpackItem(group: ItemGroup): Item(Settings().group(group).maxStackSize(1)), ITestItem {
    override val itemName: String
        get() = "Backpack"
    override val itemDescription: String?
        get() = "A simple backpack"

    override fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        if(!player.world.isClient) {
            LibrarianLibFacadeTestMod.backpackContainerType.open(
                player as ServerPlayerEntity,
                LiteralText("Backpack"),
                hand
            )
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand))
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider? {
        return ItemData()
    }

    private class ItemData: ICapabilitySerializable<CompoundNBT> {
        private val inventory = ItemStackHandler(18)
        private val inventoryLazy = LazyOptional.of { inventory }

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            if(cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return inventoryLazy.cast()
            return LazyOptional.empty()
        }

        override fun serializeNBT(): CompoundTag {
            return inventory.serializeNBT()
        }

        override fun deserializeNBT(nbt: CompoundTag) {
            inventory.deserializeNBT(nbt)
        }
    }
}