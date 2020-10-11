package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule;
import net.minecraft.util.ResourceLocation;

public class SpriteRenderExampleSystem extends ParticleSystem {
    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding previousPosition = bind(3);
        StoredBinding color = bind(4);

        getRenderModules().add(new SpriteRenderModule(
                SpriteRenderModule.simpleRenderType(
                        new ResourceLocation("modid", "textures/particle/sprite.png")
                ),
                position,
                previousPosition,
                color,
                new ConstantBinding(0.25) // size
        ));
    }
}
