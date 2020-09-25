package com.teamwizardry.librarianlib.facade.bridge;

import net.minecraft.inventory.container.Slot;

public interface FacadeContainerScreenHooks {
    /**
     * Hooks into the result of the private {@code isSlotSelected} method and allows custom containers edit the value.
     *
     * @param slotIn The slot being tested
     * @param mouseX The test x position
     * @param mouseY The test y position
     * @param result The result of the default method
     */
    boolean isSlotSelectedHook(Slot slotIn, double mouseX, double mouseY, boolean result);
}
