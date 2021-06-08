package com.teamwizardry.librarianlib.facade.example;

import com.teamwizardry.librarianlib.facade.container.FacadeControllerRegistry;
import com.teamwizardry.librarianlib.facade.container.FacadeControllerType;
import com.teamwizardry.librarianlib.facade.container.FacadeViewRegistry;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterController;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterItem;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterView;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FacadeExampleMod {
    public static FacadeControllerType<DirtSetterController> dirtSetterControllerType;
    public static Item dirtSetterItem;

    public static class CommonInit {
        public static void init() {
            dirtSetterItem = new DirtSetterItem(new Item.Settings().maxCount(1));
            Registry.register(Registry.ITEM, new Identifier("ll-facade-test:dirt_setter"), dirtSetterItem);
            dirtSetterControllerType = FacadeControllerRegistry.register(
                    new Identifier("ll-facade-test:dirt_setter"),
                    DirtSetterController.class
            );
        }
    }

    public static class ClientInit {
        public static void clientInit() {
            FacadeViewRegistry.register(dirtSetterControllerType, DirtSetterView::new);
        }
    }
}
