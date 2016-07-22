package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentGrid extends GuiComponent<ComponentGrid> {

    public int cellWidth, cellHeight, gridColumns;

    public ComponentGrid(int posX, int posY, int cellWidth, int cellHeight, int gridColumns) {
        super(posX, posY);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.gridColumns = gridColumns;
    }

    @Override
    public void drawComponent(Vec2 mousePos, float partialTicks) {
        // NOOP
    }

    @Override
    public void draw(Vec2 mousePos, float partialTicks) {
        int x = 0;
        int y = 0;
        for (GuiComponent<?> component : components) {
            component.setPos(new Vec2(x * cellWidth, y * cellHeight));

            x++;
            if (x == gridColumns) {
                x = 0;
                y++;
            }
        }
        super.draw(mousePos, partialTicks);
    }

}
