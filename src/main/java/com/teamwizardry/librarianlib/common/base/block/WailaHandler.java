package com.teamwizardry.librarianlib.common.base.block;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import org.jetbrains.annotations.Nullable;


public class WailaHandler {
    @Nullable
    public static IWailaRegistrar registrar = null;
    public static void onWailaCall(IWailaRegistrar iWailaRegistrar) {
        BlockMod.ALL_BLOCKS.stream().filter(blockMod -> blockMod instanceof IWailaDataProvider).forEach(blockMod -> {
            iWailaRegistrar.registerBodyProvider((IWailaDataProvider) blockMod, blockMod.getClass());
            iWailaRegistrar.registerStackProvider((IWailaDataProvider) blockMod, blockMod.getClass());
        });
        registrar = iWailaRegistrar;
    }
}
