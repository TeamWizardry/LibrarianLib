package com.teamwizardry.librarianlib.features.base.item

import baubles.api.BaubleType
import baubles.api.BaublesApi
import baubles.api.IBauble
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.common.LibLibSoundEvents
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper
import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.world.World
import net.minecraftforge.fml.common.Optional

/**
 * @author WireSegal
 * Created at 7:55 PM on 2/11/17.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
abstract class ItemModBauble(name: String, vararg variants: String) : ItemMod(name, *variants), IBauble {
    companion object {
        val TAG_HASHCODE = "playerHashcode"

        fun getLastPlayerHashcode(stack: ItemStack) = ItemNBTHelper.getInt(stack, TAG_HASHCODE, 0)
        fun setLastPlayerHashcode(stack: ItemStack, hash: Int) = ItemNBTHelper.setInt(stack, TAG_HASHCODE, hash)
    }

    init {
        maxStackSize = 1
    }

    @Optional.Method(modid = "baubles")
    override abstract fun getBaubleType(stack: ItemStack): BaubleType

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val stack = player.getHeldItem(hand)
        if (canEquip(stack, player)) {
            val baubles = BaublesApi.getBaublesHandler(player) ?: return ActionResult(EnumActionResult.FAIL, stack)
            for (i in 0 until baubles.slots) {
                if (baubles.isItemValidForSlot(i, stack, player)) {
                    val stackInSlot = baubles.getStackInSlot(i)
                    if (stackInSlot.isEmpty || (stackInSlot.item as IBauble).canUnequip(stackInSlot, player)) {
                        if (!world.isRemote) {
                            baubles.setStackInSlot(i, stack.copy())
                            if (!player.capabilities.isCreativeMode)
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY)
                        }

                        if (stackInSlot.isNotEmpty) {
                            (stackInSlot.item as IBauble).onUnequipped(stackInSlot, player)
                            return ActionResult.newResult(EnumActionResult.SUCCESS, stackInSlot.copy())
                        }
                        break
                    }
                }
            }
        }

        return ActionResult.newResult(EnumActionResult.PASS, stack)
    }

    open fun onEquippedOrLoadedIntoWorld(stack: ItemStack, player: EntityLivingBase) {
        //NO-OP
    }

    @Optional.Method(modid = "baubles")
    override fun onEquipped(stack: ItemStack, player: EntityLivingBase) {
        if (!player.world.isRemote)
            player.world.playSound(null, player.posX, player.posY, player.posZ, LibLibSoundEvents.baubleEquip, SoundCategory.PLAYERS, 0.1F, 1.3F)

        onEquippedOrLoadedIntoWorld(stack, player)
        setLastPlayerHashcode(stack, player.hashCode())
    }

    @Optional.Method(modid = "baubles")
    override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
        if (getLastPlayerHashcode(stack) != player.hashCode()) {
            onEquippedOrLoadedIntoWorld(stack, player)
            setLastPlayerHashcode(stack, player.hashCode())
        }
    }

    @Optional.Method(modid = "baubles")
    override fun addInformation(stack: ItemStack, player: EntityPlayer, tooltip: MutableList<String>, advanced: Boolean) {
        TooltipHelper.tooltipIfShift(tooltip) {
            addHiddenTooltip(stack, player, tooltip, advanced)
        }
    }

    open fun addHiddenTooltip(stack: ItemStack, player: EntityPlayer, tooltip: MutableList<String>, advanced: Boolean) {
        val keys = Minecraft.getMinecraft().gameSettings.keyBindings
        val key: String? = keys
                .firstOrNull { it.keyDescription == "Baubles Inventory" }
                ?.displayName

        if (key != null) TooltipHelper.addToTooltip(tooltip, "${LibrarianLib.MODID}.bauble_tooltip", key)
    }
}
