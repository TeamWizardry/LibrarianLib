package com.teamwizardry.librarianlib.client.util.lambdainterfs;

import com.teamwizardry.librarianlib.common.core.LibCommonProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class provides a lambda that is safe to contain clientside code.
 * Use this instead of proxying a single use method.
 * See {@link LibCommonProxy#runIfClient(ClientRunnable)} for where to use.
 */
@FunctionalInterface
public interface ClientRunnable {
    @SideOnly(Side.CLIENT)
    void runIfClient();
}
