package com.teamwizardry.librarianlib.facade.example.containers;

import com.teamwizardry.librarianlib.facade.example.ExampleModContainers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

public class DirtSetterItem extends Item {
    public DirtSetterItem(@NotNull Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getWorld().isRemote) {
            ExampleModContainers.dirtSetterContainerType.open(
                    (ServerPlayerEntity) context.getPlayer(),
                    new TranslationTextComponent("modid.container.dirt_setter"),
                    // additional constructor arguments:
                    context.getPos()
            );
        }
        return ActionResultType.SUCCESS;
    }
}
