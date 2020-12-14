package com.teamwizardry.librarianlib.foundation.example;

import com.teamwizardry.librarianlib.foundation.registration.ItemSpec;
import com.teamwizardry.librarianlib.foundation.registration.LazyItem;
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager;

public class ExampleModItems {
    public static final LazyItem coolItem = new LazyItem();

    public static void register(RegistrationManager registrationManager) {
        coolItem.from(registrationManager.add(
                new ItemSpec("cool_item")
                        .item(spec -> new CoolItem(spec.getItemProperties()))
                        .datagen(dataGen -> {
                            dataGen.name("en_US", "Cool Item");
                        })
        ));
    }
}
