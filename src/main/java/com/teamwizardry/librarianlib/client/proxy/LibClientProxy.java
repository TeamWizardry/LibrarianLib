package com.teamwizardry.librarianlib.client.proxy;

import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.common.proxy.LibCommonProxy;
import net.minecraftforge.common.MinecraftForge;

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
