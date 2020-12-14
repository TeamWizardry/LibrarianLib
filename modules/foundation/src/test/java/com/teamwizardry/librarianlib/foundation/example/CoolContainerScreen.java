package com.teamwizardry.librarianlib.foundation.example;

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen;
import com.teamwizardry.librarianlib.facade.layers.StackLayout;
import com.teamwizardry.librarianlib.facade.pastry.components.PastryButton;
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground;
import com.teamwizardry.librarianlib.math.Align2d;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class CoolContainerScreen extends FacadeContainerScreen<CoolContainer> {
    public CoolContainerScreen(
            @NotNull CoolContainer container,
            @NotNull PlayerInventory inventory,
            @NotNull ITextComponent title
    ) {
        super(container, inventory, title);

        getMain().setSize(new Vec2d(100, 50));
        getMain().add(new PastryBackground(0, 0, 100, 50));

        PastryButton plusOne = new PastryButton("Set Y+1 to dirt",
                () -> container.sendMessage("setToDirt", 1)
        );
        PastryButton zero = new PastryButton("Set Y+0 to dirt",
                () -> container.sendMessage("setToDirt", 0)
        );
        PastryButton minusOne = new PastryButton("Set Y-1 to dirt",
                () -> container.sendMessage("setToDirt", -1)
        );

        getMain().add(StackLayout.build()
                .align(Align2d.CENTER)
                .size(getMain().getSize())
                .spacing(1)
                .add(plusOne, zero, minusOne)
                .build()
        );
    }
}
