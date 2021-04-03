package com.teamwizardry.librarianlib.facade.example;

import com.teamwizardry.librarianlib.facade.container.FacadeContainerType;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterContainer;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ExampleModContainers {
    public static FacadeContainerType<DirtSetterContainer> dirtSetterContainerType =
            new FacadeContainerType<>(DirtSetterContainer.class);

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
                DirtSetterContainerScreen::new
        );
    }
}
