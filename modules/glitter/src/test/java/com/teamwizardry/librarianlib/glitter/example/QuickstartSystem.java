package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class QuickstartSystem extends ParticleSystem {
    public QuickstartSystem() {
        super(new Identifier("example:quickstart"));
    }

    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding previousPosition = bind(3);
        StoredBinding velocity = bind(3);
        StoredBinding color = bind(4);

        getUpdateModules().add(new BasicPhysicsUpdateModule(
                position, previousPosition, velocity
        ));

        getRenderModules().add(
                SpriteRenderModule.build(
                        new Identifier("modid", "textures/particle/sprite.png"),
                        position
                )
                        .previousPosition(previousPosition)
                        .color(color)
                        .size(0.25)
                        .build()
        );
    }

    public void spawn(
            double x, double y, double z,
            double vx, double vy, double vz,
            float r, float g, float b, float a
    ) {
        this.addParticle(
                20,         // lifetime
                x, y, z,    // position
                x, y, z,    // previousPosition
                vx, vy, vz, // velocity
                r, g, b, a  // color
        );
    }
}
