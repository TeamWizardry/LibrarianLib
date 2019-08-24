package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.item.UseAction
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

class TestItem(val config: TestItemConfig): Item(config.properties), TestObject {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.name)
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        var used = false
        if(config.rightClickHoldDuration != 0) {
            playerIn.activeHand = handIn
            used = true
        }

        config.common.rightClick?.also {
            TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
            used = true
        }
        config.common.rightClickAir?.also {
            TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
            used = true
        }

        if(worldIn.isRemote) {
            config.client.rightClick?.also {
                TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
                used = true
            }
            config.client.rightClickAir?.also {
                TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
                used = true
            }
        } else {
            config.server.rightClick?.also {
                TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
                used = true
            }
            config.server.rightClickAir?.also {
                TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn).it()
                used = true
            }
        }

        return if(used) {
            ActionResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn))
        } else {
            ActionResult(ActionResultType.PASS, playerIn.getHeldItem(handIn))
        }
    }

    override fun onItemUse(context: ItemUseContext): ActionResultType {
        var result = ActionResultType.PASS

        config.common.rightClick?.also {
            TestItemConfig.Actions.RightClickContext(context.world, context.player!!, context.hand).it()
            result = ActionResultType.SUCCESS
        }
        config.common.rightClickBlock?.also {
            TestItemConfig.Actions.RightClickBlockContext(context).it()
            result = ActionResultType.SUCCESS
        }

        if(context.world.isRemote) {
            config.client.rightClick?.also {
                TestItemConfig.Actions.RightClickContext(context.world, context.player!!, context.hand).it()
                result = ActionResultType.SUCCESS
            }
            config.client.rightClickBlock?.also {
                TestItemConfig.Actions.RightClickBlockContext(context).it()
                result = ActionResultType.SUCCESS
            }
        } else {
            config.server.rightClick?.also {
                TestItemConfig.Actions.RightClickContext(context.world, context.player!!, context.hand).it()
                result = ActionResultType.SUCCESS
            }
            config.server.rightClickBlock?.also {
                TestItemConfig.Actions.RightClickBlockContext(context).it()
                result = ActionResultType.SUCCESS
            }
        }
        return result
    }


    override fun getUseDuration(stack: ItemStack): Int {
        return config.rightClickHoldDuration
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        if(config.rightClickHoldDuration != 0) {
            return UseAction.BOW
        }
        return UseAction.NONE
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        config.common.rightClickHold?.also {
            TestItemConfig.Actions.RightClickHoldContext(stack, player, count).it()
        }

        if(player.world.isRemote) {
            config.client.rightClickHold?.also {
                TestItemConfig.Actions.RightClickHoldContext(stack, player, count).it()
            }
        } else {
            config.server.rightClickHold?.also {
                TestItemConfig.Actions.RightClickHoldContext(stack, player, count).it()
            }
        }
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: LivingEntity, timeLeft: Int) {
        config.common.rightClickRelease?.also {
            TestItemConfig.Actions.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft).it()
        }

        if(worldIn.isRemote) {
            config.client.rightClickRelease?.also {
                TestItemConfig.Actions.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft).it()
            }
        } else {
            config.server.rightClickRelease?.also {
                TestItemConfig.Actions.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft).it()
            }
        }
    }


    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
        var result = false

        config.common.leftClickEntity?.also {
            TestItemConfig.Actions.LeftClickEntityContext(stack, player, entity).it()
            result = true
        }

        if(player.world.isRemote) {
            config.client.leftClickEntity?.also {
                TestItemConfig.Actions.LeftClickEntityContext(stack, player, entity).it()
                result = true
            }
        } else {
            config.server.leftClickEntity?.also {
                TestItemConfig.Actions.LeftClickEntityContext(stack, player, entity).it()
                result = true
            }
        }
        return result
    }

    override fun itemInteractionForEntity(stack: ItemStack, playerIn: PlayerEntity, target: LivingEntity, hand: Hand): Boolean {
        var result = false

        config.common.rightClickEntity?.also {
            TestItemConfig.Actions.RightClickEntityContext(stack, playerIn, target, hand).it()
            result = true
        }

        if(playerIn.world.isRemote) {
            config.client.rightClickEntity?.also {
                TestItemConfig.Actions.RightClickEntityContext(stack, playerIn, target, hand).it()
                result = true
            }
        } else {
            config.server.rightClickEntity?.also {
                TestItemConfig.Actions.RightClickEntityContext(stack, playerIn, target, hand).it()
                result = true
            }
        }
        return result
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {

        config.common.inventoryTick?.also {
            TestItemConfig.Actions.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected).it()
        }

        if(worldIn.isRemote) {
            config.client.inventoryTick?.also {
                TestItemConfig.Actions.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected).it()
            }
        } else {
            config.server.inventoryTick?.also {
                TestItemConfig.Actions.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected).it()
            }
        }
    }
}
