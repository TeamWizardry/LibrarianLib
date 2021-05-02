package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.kotlin.makeTranslationKey
import com.teamwizardry.librarianlib.core.util.registryId
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

public open class TestItemImpl(public val config: TestItem): Item(config.properties) {
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)
        if (config.description != null) {
            val description = TranslatableText(this.registryId.makeTranslationKey("item", "tooltip"))
            description.style.withFormatting(Formatting.GRAY)
            tooltip.add(description)
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        var used = false
        if (config.rightClickHoldDuration != 0) {
            user.setCurrentHand(hand)
            used = true
        }

        val context = TestItem.RightClickContext(world, user, hand)

        config.rightClick.run(world.isClient, context)
        config.rightClickAir.run(world.isClient, context)
        if (config.rightClick.exists || config.rightClickAir.exists)
            used = true

        return if (used) {
            TypedActionResult(ActionResult.SUCCESS, user.getStackInHand(hand))
        } else {
            TypedActionResult(ActionResult.PASS, user.getStackInHand(hand))
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        var result = ActionResult.PASS

        if (context.player == null) return result

        val clickContext = TestItem.RightClickContext(context.world, context.player!!, context.hand)
        val clickBlockContext = TestItem.RightClickBlockContext(context)

        config.rightClick.run(context.world.isClient, clickContext)
        config.rightClickBlock.run(context.world.isClient, clickBlockContext)
        if (config.rightClick.exists || config.rightClickBlock.exists)
            result = ActionResult.SUCCESS
        return result
    }

    override fun getMaxUseTime(stack: ItemStack?): Int {
        return config.rightClickHoldDuration
    }

    override fun getUseAction(stack: ItemStack?): UseAction {
        if (config.rightClickHoldDuration != 0) {
            return UseAction.BOW
        }
        return UseAction.NONE
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (user !is PlayerEntity) return

        val context = TestItem.RightClickHoldContext(stack, user, remainingUseTicks)

        config.rightClickHold.run(user.world.isClient, context)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user !is PlayerEntity) return

        val context = TestItem.RightClickReleaseContext(stack, world, user, remainingUseTicks)

        config.rightClickRelease.run(world.isClient, context)
    }

    override fun finishUsing(stack: ItemStack?, world: World?, user: LivingEntity?): ItemStack {
        return super.finishUsing(stack, world, user)
    }

    override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?): Boolean {
        return super.postHit(stack, target, attacker)
    }

    override fun postMine(
        stack: ItemStack?,
        world: World?,
        state: BlockState?,
        pos: BlockPos?,
        miner: LivingEntity?
    ): Boolean {
        return super.postMine(stack, world, state, pos, miner)
    }

    override fun useOnEntity(stack: ItemStack?, user: PlayerEntity?, entity: LivingEntity?, hand: Hand?): ActionResult {
        return super.useOnEntity(stack, user, entity, hand)
    }

//    override fun onBlockStartBreak(itemstack: ItemStack, pos: BlockPos, player: PlayerEntity): Boolean {
//        val context = TestItem.LeftClickBlockContext(itemstack, pos, player)
//        config.leftClickBlock.run(player.world.isRemote, context)
//
//        return super<Item>.onBlockStartBreak(itemstack, pos, player)
//    }
//
//    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
//        val context = TestItem.LeftClickEntityContext(stack, player, entity)
//        config.leftClickEntity.run(player.world.isRemote, context)
//        return config.leftClickEntity.exists
//    }
//
//    override fun itemInteractionForEntity(stack: ItemStack, playerIn: PlayerEntity, target: LivingEntity, hand: Hand): ActionResultType {
//        val context = TestItem.RightClickEntityContext(stack, playerIn, target, hand)
//        val clickContext = TestItem.RightClickContext(playerIn.world, playerIn, hand)
//
//        config.rightClickEntity.run(playerIn.world.isRemote, context)
//        config.rightClick.run(playerIn.world.isRemote, clickContext)
//
//        return if(config.rightClickEntity.exists) ActionResultType.SUCCESS else ActionResultType.PASS
//    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (entityIn !is PlayerEntity) return

        val context = TestItem.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected)
        config.inventoryTick.run(worldIn.isClient, context)

        if (isSelected) {
            config.tickInHand.run(worldIn.isClient, context)
        }
    }
}
