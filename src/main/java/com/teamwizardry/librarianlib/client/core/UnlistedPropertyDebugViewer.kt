package com.teamwizardry.librarianlib.client.core

import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by TheCodeWarrior
 */
object UnlistedPropertyDebugViewer {
    init { MinecraftForge.EVENT_BUS.register(this) }

    @SubscribeEvent
    fun debugText(event: RenderGameOverlayEvent.Text) {
        val mc = Minecraft.getMinecraft()
        if (GuiScreen.isAltKeyDown()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.blockPos != null) {
                val blockpos = mc.objectMouseOver.blockPos
                val iblockstate = mc.theWorld.getBlockState(blockpos)

                val maybeExtended = iblockstate.block.getExtendedState(iblockstate, mc.theWorld, blockpos)

                if (maybeExtended is IExtendedBlockState) {

                    for (entry in maybeExtended.unlistedNames.sortedBy { it.name }) {
                        val value = maybeExtended.getValue(entry)

                        val s = if (value === java.lang.Boolean.TRUE) {
                            "${ChatFormatting.GREEN}${ChatFormatting.ITALIC}"
                        } else if (value === java.lang.Boolean.FALSE) {
                            "${ChatFormatting.RED}${ChatFormatting.ITALIC}"
                        } else {
                            value?.toString() ?: "NULL"
                        }

                        event.right.add("${ChatFormatting.ITALIC}${entry.name}: $s")
                    }
                }
            }
        }
    }
}
