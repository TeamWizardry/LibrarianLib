package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.block.BlockState
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
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

class TestItem(val config: TestItemConfig): Item(config.properties), TestObject {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        var used = false
        if(config.rightClickHoldDuration != 0) {
            playerIn.activeHand = handIn
            used = true
        }

        val context = TestItemConfig.Actions.RightClickContext(worldIn, playerIn, handIn)

        config.common.rightClick?.also {
            context.it()
            used = true
        }
        config.common.rightClickAir?.also {
            context.it()
            used = true
        }

        if(worldIn.isRemote) {
            config.client.rightClick?.also {
                context.it()
                used = true
            }
            config.client.rightClickAir?.also {
                context.it()
                used = true
            }
        } else {
            config.server.rightClick?.also {
                context.it()
                used = true
            }
            config.server.rightClickAir?.also {
                context.it()
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

        if(context.player == null) return result

        val clickContext = TestItemConfig.Actions.RightClickContext(context.world, context.player!!, context.hand)
        val clickBlockContext = TestItemConfig.Actions.RightClickBlockContext(context)

        config.common.rightClick?.also {
            clickContext.it()
            result = ActionResultType.SUCCESS
        }
        config.common.rightClickBlock?.also {
            clickBlockContext.it()
            result = ActionResultType.SUCCESS
        }

        if(context.world.isRemote) {
            config.client.rightClick?.also {
                clickContext.it()
                result = ActionResultType.SUCCESS
            }
            config.client.rightClickBlock?.also {
                clickBlockContext.it()
                result = ActionResultType.SUCCESS
            }
        } else {
            config.server.rightClick?.also {
                clickContext.it()
                result = ActionResultType.SUCCESS
            }
            config.server.rightClickBlock?.also {
                clickBlockContext.it()
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
        if(player !is PlayerEntity) return

        val context = TestItemConfig.Actions.RightClickHoldContext(stack, player, count)

        config.common.rightClickHold?.also {
            context.it()
        }

        if(player.world.isRemote) {
            config.client.rightClickHold?.also {
                context.it()
            }
        } else {
            config.server.rightClickHold?.also {
                context.it()
            }
        }
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: LivingEntity, timeLeft: Int) {
        if(entityLiving !is PlayerEntity) return

        val context = TestItemConfig.Actions.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft)

        config.common.rightClickRelease?.also {
            context.it()
        }

        if(worldIn.isRemote) {
            config.client.rightClickRelease?.also {
                context.it()
            }
        } else {
            config.server.rightClickRelease?.also {
                context.it()
            }
        }
    }

    override fun onBlockStartBreak(itemstack: ItemStack, pos: BlockPos, player: PlayerEntity): Boolean {
        val context = TestItemConfig.Actions.LeftClickBlockContext(itemstack, pos, player)
        config.common.leftClickBlock?.also {
            context.it()
        }

        if(player.world.isRemote) {
            config.client.leftClickBlock?.also {
                context.it()
            }
        } else {
            config.server.leftClickBlock?.also {
                context.it()
            }
        }

        return super.onBlockStartBreak(itemstack, pos, player)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
        var result = false

        val context = TestItemConfig.Actions.LeftClickEntityContext(stack, player, entity)
        config.common.leftClickEntity?.also {
            context.it()
            result = true
        }

        if(player.world.isRemote) {
            config.client.leftClickEntity?.also {
                context.it()
                result = true
            }
        } else {
            config.server.leftClickEntity?.also {
                context.it()
                result = true
            }
        }
        return result
    }

    override fun itemInteractionForEntity(stack: ItemStack, playerIn: PlayerEntity, target: LivingEntity, hand: Hand): Boolean {
        var result = false

        val context = TestItemConfig.Actions.RightClickEntityContext(stack, playerIn, target, hand)
        val clickContext = TestItemConfig.Actions.RightClickContext(playerIn.world, playerIn, hand)

        config.common.rightClick?.also {
            clickContext.it()
        }
        config.common.rightClickEntity?.also {
            context.it()
            result = true
        }

        if(playerIn.world.isRemote) {
            config.client.rightClick?.also {
                clickContext.it()
            }
            config.client.rightClickEntity?.also {
                context.it()
                result = true
            }
        } else {
            config.server.rightClick?.also {
                clickContext.it()
            }
            config.server.rightClickEntity?.also {
                context.it()
                result = true
            }
        }
        return result
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if(entityIn !is PlayerEntity) return

        val context = TestItemConfig.Actions.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected)
        config.common.inventoryTick?.also {
            context.it()
        }

        if(worldIn.isRemote) {
            config.client.inventoryTick?.also {
                context.it()
            }
        } else {
            config.server.inventoryTick?.also {
                context.it()
            }
        }

        if(isSelected) {
            config.common.tickInHand?.also {
                context.it()
            }

            if(worldIn.isRemote) {
                config.client.tickInHand?.also {
                    context.it()
                }
            } else {
                config.server.tickInHand?.also {
                    context.it()
                }
            }
        }
    }
}
