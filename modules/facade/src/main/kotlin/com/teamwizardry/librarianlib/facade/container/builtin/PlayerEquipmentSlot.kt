package com.teamwizardry.librarianlib.facade.container.builtin

import com.mojang.datafixers.util.Pair
import com.teamwizardry.librarianlib.facade.container.slot.FacadeSlot
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier

public class PlayerEquipmentSlot(
    inventory: Inventory, index: Int,
    public var player: PlayerEntity, public var type: EquipmentSlot
) : FacadeSlot(inventory, index) {

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
     * the case of armor slots)
     */
    override fun getMaxItemCount(): Int {
        return 1
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    override fun canInsert(stack: ItemStack): Boolean {
        return MobEntity.getPreferredEquipmentSlot(stack) == type
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        val itemStack = this.stack
        return if (!itemStack.isEmpty && !playerEntity.isCreative && EnchantmentHelper.hasBindingCurse(itemStack))
            false
        else
            super.canTakeItems(playerEntity)
    }

    @Environment(EnvType.CLIENT)
    override fun getBackgroundSprite(): Pair<Identifier, Identifier>? {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[type.entitySlotId])
    }

    private companion object {
        private val EMPTY_ARMOR_SLOT_TEXTURES = listOf(
            null, // main hand has no icon
            PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT,
            PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE
        )
    }
}