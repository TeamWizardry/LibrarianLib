package com.teamwizardry.librarianlib.glitter.example;

import com.teamwizardry.librarianlib.glitter.ParticleSystem;
import com.teamwizardry.librarianlib.glitter.bindings.StoredBinding;

public class BindingExampleSystem extends ParticleSystem {
    @Override
    public void configure() {
        StoredBinding position = bind(3);
        StoredBinding color = bind(4);

        // The resulting particle array layout:
        // [
        //     age,
        //     lifetime,
        //     pos, pos, pos,
        //     color, color, color, color
        // ]
    }
}
