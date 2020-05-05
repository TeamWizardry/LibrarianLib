package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.translationKey
import com.teamwizardry.librarianlib.testbase.junit.UnitTestRunner
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.fml.ModLoadingContext

@Suppress("PublicApiImplicitType")
class UnitTestItem(val config: UnitTestConfig): Item(config.properties) {
    init {
        this.registryName = ResourceLocation(ModLoadingContext.get().activeContainer.modId, config.id)
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if(config.description != null) {
            val description = TranslationTextComponent(registryName!!.translationKey("item", "tooltip"))
            description.style.color = TextFormatting.GRAY
            tooltip.add(description)
        }
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        if(worldIn.isRemote) // only print once, this will always run first on the client
            playerIn.sendMessage(StringTextComponent("Running $registryName tests..."))
        val report = UnitTestRunner.runUnitTests(config.tests)
        config.logger.info("Unit tests for $registryName\n" + report.roots.joinToString("\n") { UnitTestRunner.format(it) })
        playerIn.sendMessage(
                UnitTestRunner.makeTextComponent(report).appendText(if(worldIn.isRemote) " (Client)" else " (Server)")
        )

        return ActionResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn))
    }
}