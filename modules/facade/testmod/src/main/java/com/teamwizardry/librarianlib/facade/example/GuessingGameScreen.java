package com.teamwizardry.librarianlib.facade.example;

import java.awt.Color;

import net.minecraft.text.TranslatableText;

import com.teamwizardry.librarianlib.facade.FacadeScreen;
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents;
import com.teamwizardry.librarianlib.facade.layers.RectLayer;
import com.teamwizardry.librarianlib.math.Vec2d;

public class GuessingGameScreen extends FacadeScreen {
    private final boolean[] currentState = new boolean[5];
    private final boolean[] targetState = new boolean[5];
    private final RectLayer outputRect;

    public GuessingGameScreen() {
        super(new TranslatableText("modid.screen.guessing_game.title"));

        getMain().setSize(new Vec2d(20 * 5 + 2 * 4, 32));

        for(int i = 0; i < 5; i++) {
            targetState[i] = Math.random() < 0.5;

            RectLayer toggle = new RectLayer(Color.BLACK, 22 * i, 0, 20, 20);
            int index = i;
            toggle.hook(GuiLayerEvents.MouseClick.class, (e) -> {
                currentState[index] = !currentState[index];
                toggle.setColor(currentState[index] ? Color.RED : Color.BLACK);
                checkGuesses();
            });
            getMain().add(toggle);
        }

        outputRect = new RectLayer(Color.GREEN, 0, 22, 0, 10);
        getMain().add(new RectLayer(Color.RED, 0, 22, getMain().getWidthi(), 10));
        getMain().add(outputRect);
        checkGuesses();
    }

    private void checkGuesses() {
        int correctGuesses = 0;
        for(int i = 0; i < 5; i++) {
            if(currentState[i] == targetState[i])
                correctGuesses++;
        }

        outputRect.setWidth(getMain().getWidth() * correctGuesses / 5);
    }
}
