package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSliderTray extends GuiComponent<ComponentSliderTray> {

    boolean animatingIn = true;
    boolean animatingOut = false;
    int tickStart;

    int lifetime = 5;
    int offsetX, offsetY;
    float currentOffsetX;
    Vec2 rootPos;

    public ComponentSliderTray(int posX, int posY, int offsetX, int offsetY) {
        super(posX, posY);
        setCalculateOwnHover(false);
        tickStart = GuiTickHandler.ticks;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.currentOffsetX = (float) pos.x;
        rootPos = pos;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public void close() {
        tickStart = GuiTickHandler.ticks;
        animatingIn = false;
        animatingOut = true;
    }

    @Override
    public void drawComponent(Vec2 mousePos, float partialTicks) {
        // TODO: Respect partialTicks
        float t = (float) (GuiTickHandler.ticks - tickStart) / (float) getLifetime();
        if (t > 1) {
            if (animatingIn)
                animatingIn = false;
        }

        if (Math.signum(offsetX) < 0) {
            if (animatingIn)
                if (currentOffsetX >= offsetX) currentOffsetX -= (-offsetX - Math.abs(currentOffsetX)) / 3;
            if (animatingOut) {
                if (currentOffsetX < rootPos.x && currentOffsetX + (-offsetX - Math.abs(currentOffsetX)) / 3 < rootPos.x)
                    currentOffsetX += (-offsetX - Math.abs(currentOffsetX)) / 3;
                else invalidate();
            }

            // TODO: untested math.signum(x) < 0
        } else if (Math.signum(offsetX) < 0) {
            if (animatingIn)
                if (currentOffsetX > rootPos.x && currentOffsetX - (offsetX - Math.abs(currentOffsetX)) / 3 > rootPos.x)
                    currentOffsetX -= (offsetX - Math.abs(currentOffsetX)) / 3;
                else invalidate();
            if (animatingOut) {
                if (currentOffsetX <= offsetX) currentOffsetX += (offsetX - Math.abs(currentOffsetX)) / 3;
            }

        } else invalidate();

        pos = new Vec2(rootPos.x + currentOffsetX, rootPos.y);
    }
}
