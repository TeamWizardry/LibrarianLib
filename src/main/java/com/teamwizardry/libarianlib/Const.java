package com.teamwizardry.libarianlib;

import net.minecraft.launchwrapper.Launch;

public class Const {
	
	public static final boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	
	public static class GuiID {
		public static final int GUIDE = 0;
	}
	
	public static final class ButtonID {
		public static final int NAV_BAR_BACK = 0;
		public static final int NAV_BAR_NEXT = 1;
		public static final int NAV_BAR_INDEX = 2;
		public static final int BOOKMARK = 3;
	}
	
}
