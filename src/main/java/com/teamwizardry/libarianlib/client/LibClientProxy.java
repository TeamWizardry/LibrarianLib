package com.teamwizardry.libarianlib.client;

import net.minecraftforge.common.MinecraftForge;

import com.teamwizardry.libarianlib.common.LibCommonProxy;

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
public class LibClientProxy extends LibCommonProxy {

	@Override
	public void preInit() {
		super.preInit();
        MinecraftForge.EVENT_BUS.register(ScissorUtil.INSTANCE);
	}
	
}
