package com.teamwizardry.librarianlib.common.base.block;

import mcp.mobius.waila.api.IWailaRegistrar;
import org.jetbrains.annotations.Nullable;


public class WailaHandler {
    @Nullable
    public static IWailaRegistrar registrar = null;
    public static void onWailaCall(IWailaRegistrar iWailaRegistrar) {
        for(BlockMod blockMod : BlockMod.blocks) {
            iWailaRegistrar.registerBodyProvider(blockMod, blockMod.getClass());
            iWailaRegistrar.registerStackProvider(blockMod, blockMod.getClass());
        }
        registrar = iWailaRegistrar;
    }
}
