package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.BlockDispenser
import net.minecraft.dispenser.BehaviorDefaultDispenseItem
import net.minecraft.dispenser.IBlockSource
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World

/**
 * The default implementation for an IVariantHolder item.
 */
@Suppress("LeakingThis")
open class ItemModShield(name: String, durability: Int = 336) : ItemMod(name), IShieldItem, IModelGenerator {
    init {
        maxDamage = durability
        maxStackSize = 1
        this.addPropertyOverride(ResourceLocation("blocking")) { stack, _, entityIn ->
            if (entityIn != null && entityIn.isHandActive && entityIn.activeItemStack == stack) 1.0f else 0.0f
        }
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseBehavior)
    }

    companion object DispenseBehavior : BehaviorDefaultDispenseItem() {
        override fun dispenseStack(source: IBlockSource, stack: ItemStack): ItemStack {
            val returnStack = armorDispense(source, stack)
            return if (returnStack.isEmpty) super.dispenseStack(source, stack) else returnStack
        }

        fun armorDispense(source: IBlockSource, stack: ItemStack): ItemStack {
            val pos = source.blockPos.offset(source.blockState.getValue(BlockDispenser.FACING) as EnumFacing)
            val list = source.world.getEntitiesWithinAABB(EntityLivingBase::class.java, AxisAlignedBB(pos)) {
                (it !is EntityPlayer || !it.isSpectator) && it?.heldItemOffhand?.isEmpty ?: true
            }

            if (list.isEmpty()) return ItemStack.EMPTY else {
                val entity = list[0]
                val dispense = stack.splitStack(1)
                entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, dispense)
                if (entity is EntityLiving) entity.setDropChance(EntityEquipmentSlot.OFFHAND, 2.0f)

                return stack
            }
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        playerIn.activeHand = handIn
        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun getMaxItemUseDuration(stack: ItemStack) = 72000
    override fun getItemUseAction(stack: ItemStack) = EnumAction.BLOCK

    override fun onDamageBlocked(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource) {
        // NO-OP
    }

    override fun damageItem(stack: ItemStack, player: EntityPlayer, indirectSource: Entity?, directSource: Entity?, amount: Float, source: DamageSource, damageAmount: Int)
            = false

    override fun onAxeBlocked(stack: ItemStack, player: EntityPlayer, attacker: EntityLivingBase, amount: Float, source: DamageSource)
            = false


    override fun generateMissingItem(variant: String): Boolean {
        ModelHandler.generateItemJson(this) {
            mapOf(JsonGenerationUtils.getPathForItemModel(this, variant) to json {
                obj(
                        "parent" to "item/generated",
                        "textures" to obj(
                                "layer0" to "${registryName.resourceDomain}:items/$variant"
                        ),
                        "overrides" to array(
                                obj(
                                        "predicate" to obj(
                                                "blocking" to 1
                                        ),
                                        "model" to "${registryName.resourceDomain}:item/${variant}_blocking"
                                )
                        )
                )
            },
                    JsonGenerationUtils.getPathForItemModel(this, "${variant}_blocking") to json {
                        obj(
                                "parent" to "${registryName.resourceDomain}:item/$variant",
                                "display" to obj(
                                        "firstperson_righthand" to obj(
                                                "rotation" to array(0, -45, 25),
                                                "translation" to array(-1, 1.75, 0)
                                        ),
                                        "firstperson_lefthand" to obj(
                                                "rotation" to array(0, -45, 25),
                                                "translation" to array(-1, 1.75, 0)
                                        )
                                )
                        )
                    })
        }
        return true
    }
}

