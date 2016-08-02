package com.teamwizardry.librarianlib.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class GuiTickHandler {

	public static int ticks = 0;
	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;

	private void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == Phase.START)
			partialTicks = event.renderTickTime;
	}

	@SubscribeEvent
	public void clientTickEnd(ClientTickEvent event) {
		if (event.phase == Phase.END) {
			Minecraft mc = Minecraft.getMinecraft();
			GuiScreen gui = mc.currentScreen;
			if (gui == null || !gui.doesGuiPauseGame()) {
				ticksInGame++;
				partialTicks = 0;
			}

			ticks++;

			calcDelta();
		}
	}

}
