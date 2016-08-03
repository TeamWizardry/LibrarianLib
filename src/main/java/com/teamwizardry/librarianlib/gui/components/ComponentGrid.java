package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.math.Vec2d;

public class ComponentGrid extends GuiComponent<ComponentGrid> {

    public int cellWidth, cellHeight, gridColumns;

    public ComponentGrid(int posX, int posY, int cellWidth, int cellHeight, int gridColumns) {
        super(posX, posY);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.gridColumns = gridColumns;
    }

    @Override
    public void drawComponent(Vec2d mousePos, float partialTicks) {
        // NOOP
    }

    @Override
    public void draw(Vec2d mousePos, float partialTicks) {
        int x = 0;
        int y = 0;
        for (GuiComponent<?> component : components) {
            component.setPos(new Vec2d(x * cellWidth, y * cellHeight));

            x++;
            if (x == gridColumns) {
                x = 0;
                y++;
            }
        }
        super.draw(mousePos, partialTicks);
    }

}
