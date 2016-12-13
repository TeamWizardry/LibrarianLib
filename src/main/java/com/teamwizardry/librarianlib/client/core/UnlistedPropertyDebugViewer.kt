package com.teamwizardry.librarianlib.client.core

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

/**
 * Created by TheCodeWarrior
 */
object UnlistedPropertyDebugViewer {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun debugText(event: RenderGameOverlayEvent.Text) {
        val mc = Minecraft.getMinecraft()
        if (mc.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.blockPos != null) {
                val blockpos = mc.objectMouseOver.blockPos
                val iblockstate = mc.world.getBlockState(blockpos)

                val maybeExtended = iblockstate.block.getExtendedState(iblockstate, mc.world, blockpos)

                if (maybeExtended is IExtendedBlockState) {

                    for (entry in maybeExtended.unlistedNames.sortedBy { it.name }) {
                        val value: Any? = maybeExtended.getValue(entry)

                        val s = if (value === TRUE)
                            "${TextFormatting.GREEN}TRUE"
                        else if (value === FALSE)
                            "${TextFormatting.RED}FALSE"
                        else value.toString()

                        event.right.add("${TextFormatting.ITALIC}${entry.name}: $s")
                    }
                }
            }
        }
    }
}
