package com.teamwizardry.librarianlib.gui;

import java.lang.reflect.Field;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class GuiTickHandler {
	public static final GuiTickHandler INSTANCE = new GuiTickHandler();
	
	public static int ticks = 0;
	private static Field timer;
	
	private GuiTickHandler() {
		timer = ReflectionHelper.findField(Minecraft.class, "timer", "field_71428_T");
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			
			ticks++;
			
		}
	}
	
	public static float getPartialTicks() {
		try {
			Timer t = (Timer) timer.get(Minecraft.getMinecraft());
			return t.renderPartialTicks;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
}
