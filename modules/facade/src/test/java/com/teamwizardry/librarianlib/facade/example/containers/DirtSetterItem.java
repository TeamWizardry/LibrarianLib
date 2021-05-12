package com.teamwizardry.librarianlib.facade.example.containers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

import com.teamwizardry.librarianlib.facade.example.ExampleModContainers;
import org.jetbrains.annotations.NotNull;

public class DirtSetterItem extends Item {
    public DirtSetterItem(@NotNull Item.Settings properties) {
        super(properties);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            ExampleModContainers.dirtSetterContainerType.open(
                    (ServerPlayerEntity) context.getPlayer(),
                    new TranslatableText("modid.container.dirt_setter"),
                    // additional constructor arguments:
                    context.getBlockPos()
            );
        }
        return ActionResult.SUCCESS;
    }
}
