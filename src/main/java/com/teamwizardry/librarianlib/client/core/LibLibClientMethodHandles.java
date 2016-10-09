package com.teamwizardry.librarianlib.client.core;

import com.google.common.base.Throwables;
import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelManager;

import java.lang.invoke.MethodHandle;

/**
 * @author WireSegal
 *         Created at 8:17 PM on 9/28/16.
 */
public class LibLibClientMethodHandles {
    private static final MethodHandle modelHandler = MethodHandleHelper.handleForField(Minecraft.class, true, "aQ", "field_175617_aL", "modelManager");

    public static ModelManager getModelManager() {
        try {
            return (ModelManager) modelHandler.invokeExact(Minecraft.getMinecraft());
        } catch (Throwable t) {
            throw propagate(t);
        }
    }

    private static RuntimeException propagate(Throwable t) {
        LibrarianLog.INSTANCE.error("Methodhandle failed!");
        t.printStackTrace();
        return Throwables.propagate(t);
    }
}
