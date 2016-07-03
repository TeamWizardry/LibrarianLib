package com.teamwizardry.librarianlib.api.gui;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class GuiTickHandler {
	public static final GuiTickHandler INSTANCE = new GuiTickHandler();
	
	public static int ticks = 0;
	
	private GuiTickHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@SubscribeEvent
	public void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			
			ticks++;
			
		}
	}
	
}
