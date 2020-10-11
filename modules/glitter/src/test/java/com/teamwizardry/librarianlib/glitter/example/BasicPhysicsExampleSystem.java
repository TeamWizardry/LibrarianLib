package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule;

public class BasicPhysicsExampleSystem extends ParticleSystem {
    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding previousPosition = bind(3);
        StoredBinding velocity = bind(3);

        getUpdateModules().add(new BasicPhysicsUpdateModule(
                position, previousPosition, velocity
        ));
    }
}
