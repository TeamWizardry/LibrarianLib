package com.teamwizardry.librarianlib.features.utilities;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.common.LibCommonProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class provides a lambda that is safe to contain clientside code.
 * Use this instead of proxying a single use method.
 * See {@link LibCommonProxy#runIfClient(ClientRunnable)} for where to use.
 */
@FunctionalInterface
public interface ClientRunnable {
    static void run(ClientRunnable runnable) {
        LibrarianLib.PROXY.runIfClient(runnable);
    }

    @SideOnly(Side.CLIENT)
    void runIfClient();
}
