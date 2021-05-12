package com.teamwizardry.librarianlib.facade.example.gettingstarted;

import java.awt.Color;

import net.minecraft.text.TranslatableText;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.math.Vec2d;

public class HelloSquareScreen extends FacadeScreen {
    public HelloSquareScreen() {
        super(new TranslatableText("modid.screen.hello_square.title"));

        getMain().setSize(new Vec2d(20, 20));

        RectLayer redSquare = new RectLayer(Color.RED, 0, 0, 20, 20);
        getMain().add(redSquare);
    }
}
