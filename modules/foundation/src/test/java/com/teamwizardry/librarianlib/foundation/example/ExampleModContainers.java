package com.teamwizardry.librarianlib.foundation.example;

import com.teamwizardry.librarianlib.foundation.registration.ContainerSpec;
import com.teamwizardry.librarianlib.foundation.registration.LazyContainerType;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;

public class ExampleModContainers {
    public static final LazyContainerType<CoolContainer> coolContainer
            = new LazyContainerType<>();

    public static void register(RegistrationManager registrationManager) {
        coolContainer.from(registrationManager.add(
                new ContainerSpec<>("cool_container",
                        CoolContainer.class,
                        CoolContainerScreen::new
                )
        ));
    }
}
