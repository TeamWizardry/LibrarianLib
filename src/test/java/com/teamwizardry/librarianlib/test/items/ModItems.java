package com.teamwizardry.librarianlib.test.items;

import com.teamwizardry.librarianlib.common.base.Ignored;
import com.teamwizardry.librarianlib.common.base.ResourceClass;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import net.minecraft.block.material.Material;

@ResourceClass
public class ModItems {
    public static ModItems.ItemFoo foo;
    @Ignored
    public static ModItems.ItemBar bar;

    static {
        bar = new ItemBar();
    }

    public static class ItemFoo extends BlockMod {
        public ItemFoo() {
            super("foo", Material.ANVIL);
        }
    }

    public static class ItemBar extends BlockMod {
        public ItemBar() {
            super("bar", Material.ANVIL);
        }
    }
}
