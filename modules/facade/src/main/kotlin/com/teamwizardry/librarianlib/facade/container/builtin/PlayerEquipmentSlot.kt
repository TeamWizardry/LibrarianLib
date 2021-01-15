package com.teamwizardry.librarianlib.facade.container.builtin

import com.mojang.datafixers.util.Pair
import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.EquipmentSlotType
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

public class PlayerEquipmentSlot(
    itemHandler: IItemHandler, index: Int,
    public var player: PlayerEntity, public var type: EquipmentSlotType
) : FacadeSlot(itemHandler, index) {

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
     * the case of armor slots)
     */
    override fun getSlotStackLimit(): Int {
        return 1
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    override fun isItemValid(stack: ItemStack): Boolean {
        return stack.canEquip(type, player)
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    override fun canTakeStack(playerIn: PlayerEntity): Boolean {
        val itemstack = this.stack
        return if (!itemstack.isEmpty && !playerIn.isCreative && EnchantmentHelper.hasBindingCurse(itemstack))
            false
        else
            super.canTakeStack(playerIn)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getBackground(): Pair<ResourceLocation, ResourceLocation>? {
        return EQUIPMENT_TYPE_TEXTURES[type.ordinal]?.let { Pair.of(AtlasTexture.LOCATION_BLOCKS_TEXTURE, it) }
    }

    private companion object {
        private val EQUIPMENT_TYPE_TEXTURES = listOf(
            null, // main hand has no icon
            PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD,
            PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS,
            PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
            PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
            PlayerContainer.EMPTY_ARMOR_SLOT_HELMET
        )
    }
}