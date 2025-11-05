package com.teamwizardry.librarianlib.facade.example.containers;

import com.teamwizardry.librarianlib.facade.container.FacadeView;
import com.teamwizardry.librarianlib.facade.layers.StackLayout;
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryButton;
import com.teamwizardry.librarianlib.math.Align2d;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class DirtSetterView extends FacadeView<DirtSetterController> {
    public DirtSetterView(
            @NotNull DirtSetterController container,
            @NotNull PlayerInventory inventory,
            @NotNull Text title
    ) {
        super(container, inventory, title);

        getMain().setSize(new Vec2d(100, 50));

        PastryButton plusOne = new PastryButton("Set Y+1 to dirt",
                () -> sendMessage("setBlockPressed", 1)
        );
        PastryButton zero = new PastryButton("Set Y+0 to dirt",
                () -> sendMessage("setBlockPressed", 0)
        );
        PastryButton minusOne = new PastryButton("Set Y-1 to dirt",
                () -> sendMessage("setBlockPressed", -1)
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
