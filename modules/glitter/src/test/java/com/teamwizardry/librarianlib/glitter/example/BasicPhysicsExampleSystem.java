package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BasicPhysicsExampleSystem extends ParticleSystem {

    public BasicPhysicsExampleSystem() {
        super(new Identifier("example:basic"));
    }

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
