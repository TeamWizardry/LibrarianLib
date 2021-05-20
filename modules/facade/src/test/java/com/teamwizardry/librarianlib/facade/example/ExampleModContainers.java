package com.teamwizardry.librarianlib.facade.example;

import net.minecraft.util.Identifier;

import com.teamwizardry.librarianlib.facade.container.FacadeControllerRegistry;
import com.teamwizardry.librarianlib.facade.container.FacadeControllerType;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterController;
import com.teamwizardry.librarianlib.facade.example.containers.DirtSetterView;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class ExampleModContainers {
    public static FacadeControllerType<DirtSetterController> dirtSetterContainerType = FacadeControllerRegistry.register(new Identifier("ll-facade-test:dirt_setter"), DirtSetterController.class);

    public static void clientSetup() {
        ScreenRegistry.register(
                dirtSetterContainerType.getScreenHandlerType(),
                DirtSetterView::new
        );
    }
}
