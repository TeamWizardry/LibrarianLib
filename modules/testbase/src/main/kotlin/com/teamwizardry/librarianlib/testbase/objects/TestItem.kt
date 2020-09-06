package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import net.minecraft.client.util.ITooltipFlag
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
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

public class TestItem(public val config: TestItemConfig): Item(config.properties) {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if (config.description != null) {
            val description = TranslationTextComponent(registryName!!.translationKey("item", "tooltip"))
            description.style.color = TextFormatting.GRAY
            tooltip.add(description)
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        var used = false
        if (config.rightClickHoldDuration != 0) {
            playerIn.activeHand = handIn
            used = true
        }

        val context = TestItemConfig.RightClickContext(worldIn, playerIn, handIn)

        config.rightClick.run(worldIn.isRemote, context)
        config.rightClickAir.run(worldIn.isRemote, context)
        if (config.rightClick.exists || config.rightClickAir.exists)
            used = true

        return if (used) {
            ActionResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn))
        } else {
            ActionResult(ActionResultType.PASS, playerIn.getHeldItem(handIn))
        }
    }

    override fun onItemUse(context: ItemUseContext): ActionResultType {
        var result = ActionResultType.PASS

        if (context.player == null) return result

        val clickContext = TestItemConfig.RightClickContext(context.world, context.player!!, context.hand)
        val clickBlockContext = TestItemConfig.RightClickBlockContext(context)

        config.rightClick.run(context.world.isRemote, clickContext)
        config.rightClickBlock.run(context.world.isRemote, clickBlockContext)
        if (config.rightClick.exists || config.rightClickBlock.exists)
            result = ActionResultType.SUCCESS
        return result
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return config.rightClickHoldDuration
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        if (config.rightClickHoldDuration != 0) {
            return UseAction.BOW
        }
        return UseAction.NONE
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        if (player !is PlayerEntity) return

        val context = TestItemConfig.RightClickHoldContext(stack, player, count)

        config.rightClickHold.run(player.world.isRemote, context)
    }

    override fun onPlayerStoppedUsing(stack: ItemStack, worldIn: World, entityLiving: LivingEntity, timeLeft: Int) {
        if (entityLiving !is PlayerEntity) return

        val context = TestItemConfig.RightClickReleaseContext(stack, worldIn, entityLiving, timeLeft)

        config.rightClickRelease.run(worldIn.isRemote, context)
    }

    override fun onBlockStartBreak(itemstack: ItemStack, pos: BlockPos, player: PlayerEntity): Boolean {
        val context = TestItemConfig.LeftClickBlockContext(itemstack, pos, player)
        config.leftClickBlock.run(player.world.isRemote, context)

        return super.onBlockStartBreak(itemstack, pos, player)
    }

    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
        val context = TestItemConfig.LeftClickEntityContext(stack, player, entity)
        config.leftClickEntity.run(player.world.isRemote, context)
        return config.leftClickEntity.exists
    }

    override fun itemInteractionForEntity(stack: ItemStack, playerIn: PlayerEntity, target: LivingEntity, hand: Hand): Boolean {
        val context = TestItemConfig.RightClickEntityContext(stack, playerIn, target, hand)
        val clickContext = TestItemConfig.RightClickContext(playerIn.world, playerIn, hand)

        config.rightClickEntity.run(playerIn.world.isRemote, context)
        config.rightClick.run(playerIn.world.isRemote, clickContext)

        return config.rightClickEntity.exists
    }

    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (entityIn !is PlayerEntity) return

        val context = TestItemConfig.InventoryTickContext(stack, worldIn, entityIn, itemSlot, isSelected)
        config.inventoryTick.run(worldIn.isRemote, context)

        if (isSelected) {
            config.tickInHand.run(worldIn.isRemote, context)
        }
    }
}
