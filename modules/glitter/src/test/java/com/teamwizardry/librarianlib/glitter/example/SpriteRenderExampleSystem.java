package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import net.minecraft.util.Identifier;

public class SpriteRenderExampleSystem extends ParticleSystem {
    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding previousPosition = bind(3);
        StoredBinding color = bind(4);

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
}
