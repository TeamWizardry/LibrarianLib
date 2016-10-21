package com.teamwizardry.librarianlib.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class clazz {
    ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return null;
        }
    };
}
