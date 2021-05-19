package com.teamwizardry.librarianlib.facade.example;

import com.teamwizardry.librarianlib.facade.container.FacadeScreenHandlerType;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterController;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterView;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ExampleModContainers {
    public static FacadeScreenHandlerType<DirtSetterController> dirtSetterContainerType =
            new FacadeScreenHandlerType<>(DirtSetterController.class);

    static {
//        dirtSetterContainerType.setRegistryName("modid:dirt_setter");
        dirtSetterContainerType.setRegistryName("ll-facade-test:dirt_setter");
    }

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(dirtSetterContainerType);
    }

    public static void clientSetup(FMLClientSetupEvent e) {
        ScreenManager.registerFactory(
                dirtSetterContainerType,
                DirtSetterView::new
        );
    }
}
