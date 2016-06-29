package com.teamwizardry.librarianlib.api.gui.event;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.GuiEvent;

public class ButtonClickEvent extends GuiEvent {

    public ButtonClickEvent(GuiComponent component) {
        super(component);
    }

}
