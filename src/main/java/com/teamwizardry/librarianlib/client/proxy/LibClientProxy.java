package com.teamwizardry.librarianlib.client.proxy;

import net.minecraftforge.common.MinecraftForge;

import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.client.fx.shader.LibShaders;
import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.common.proxy.LibCommonProxy;

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
public class LibClientProxy extends LibCommonProxy {

	@Override
	public void preInit() {
		super.preInit();
        MinecraftForge.EVENT_BUS.register(ScissorUtil.INSTANCE);
        LibShaders.INSTANCE.getClass();// load the class
        ShaderHelper.initShaders();
	}
	
}
