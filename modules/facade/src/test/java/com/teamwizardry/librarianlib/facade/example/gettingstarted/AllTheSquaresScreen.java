package com.teamwizardry.librarianlib.facade.example.gettingstarted;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.Color;

public class AllTheSquaresScreen extends FacadeScreen {
    public AllTheSquaresScreen() {
        super(new TranslationTextComponent("modid.screen.all_the_squares.title"));

        getMain().setSize(new Vec2d(100, 100));
        RectLayer background = new RectLayer(Color.WHITE, 0, 0, 100, 100);
        getMain().add(background);

        getMain().hook(GuiLayerEvents.MouseDown.class, (e) -> {
            Vec2d clickPosition = e.getPos();

            RectLayer redSquare = new RectLayer(Color.RED,
                    clickPosition.getXi(), clickPosition.getYi(),
                    20, 20
            );
            getMain().add(redSquare);
        });
    }
}
