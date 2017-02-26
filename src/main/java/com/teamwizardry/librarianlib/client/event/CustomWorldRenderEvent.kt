package com.teamwizardry.librarianlib.client.event

import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by TheCodeWarrior
 */
@SideOnly(Side.CLIENT)
class CustomWorldRenderEvent(val world: World, val context: RenderGlobal, val partialTicks: Float) : Event()
