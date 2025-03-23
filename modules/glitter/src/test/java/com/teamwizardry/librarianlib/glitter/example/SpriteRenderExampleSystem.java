package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class SpriteRenderExampleSystem extends ParticleSystem {
    public SpriteRenderExampleSystem() {
        super(Identifier.of("example:sprite"));
    }

    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding previousPosition = bind(3);
        StoredBinding color = bind(4);

        getRenderModules().add(
                SpriteRenderModule.build(
                        Identifier.of("modid", "textures/particle/sprite.png"),
                        position
                )
                        .previousPosition(previousPosition)
                        .color(color)
                        .size(0.25)
                        .build()
        );
    }
}
