package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.core.util.kotlin.makeTranslationKey
import com.teamwizardry.librarianlib.testcore.content.TestItemConfig
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.world.World

public open class TestItemImpl(public val config: TestItemConfig): Item(config.properties) {
    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        type: TooltipType
    ) {
        super.appendTooltip(stack, context, tooltip, type)
        for(line in config.description?.lines() ?: emptyList()) {
            val description = Text.literal(line)
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

        val context = TestItemConfig.RightClickContext(world, user, hand)

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
        if (context.player == null) return ActionResult.PASS

        val clickContext = TestItemConfig.RightClickContext(context.world, context.player!!, context.hand)
        val clickBlockContext = TestItemConfig.RightClickBlockContext(context)

        config.rightClick.run(context.world.isClient, clickContext)
        config.rightClickBlock.run(context.world.isClient, clickBlockContext)
        if (config.rightClick.exists || config.rightClickBlock.exists)
            return ActionResult.SUCCESS
        return ActionResult.PASS
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int {
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

        val context = TestItemConfig.RightClickHoldContext(stack, user, remainingUseTicks)

        config.rightClickHold.run(user.world.isClient, context)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user !is PlayerEntity) return

        val context = TestItemConfig.RightClickReleaseContext(stack, user, world, remainingUseTicks)

        config.rightClickRelease.run(world.isClient, context)
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (entityIn !is PlayerEntity) return

        val context = TestItemConfig.InventoryTickContext(stack, entityIn, worldIn, itemSlot, isSelected)
        config.inventoryTick.run(worldIn.isClient, context)

        if (isSelected) {
            config.tickInHand.run(worldIn.isClient, context)
        }
    }
}
