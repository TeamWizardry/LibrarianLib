package com.teamwizardry.librarianlib.facade.example.gettingstarted;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.Color;

public class HelloSquareScreen extends FacadeScreen {
    public HelloSquareScreen() {
        super(new TranslationTextComponent("modid.screen.hello_square.title"));

        getMain().setSize(new Vec2d(20, 20));

        RectLayer redSquare = new RectLayer(Color.RED, 0, 0, 20, 20);
        getMain().add(redSquare);
    }
}
