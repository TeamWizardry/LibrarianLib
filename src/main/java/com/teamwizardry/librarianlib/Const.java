package com.teamwizardry.librarianlib;

import net.minecraft.launchwrapper.Launch;

public class Const {
	
	public static final boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	
}
