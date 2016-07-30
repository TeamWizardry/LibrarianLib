package com.teamwizardry.librarianlib.api.gui.components;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;
import java.util.function.Function;

public class ComponentText extends GuiComponent<ComponentText> {

    public final Option<ComponentText, String> text = new Option<>("-NULL TEXT-");
    public final Option<ComponentText, Color> color = new Option<>(Color.BLACK);
    public final Option<ComponentText, Integer> wrap = new Option<>(-1);
    public TextAlignH horizontal;
    public TextAlignV vertical;

    private boolean enableFlags = false;

    public ComponentText(int posX, int posY) {
        this(posX, posY, TextAlignH.LEFT, TextAlignV.TOP);
    }

    public ComponentText(int posX, int posY, TextAlignH horizontal, TextAlignV vertical) {
        super(posX, posY);
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.color.setValue(Color.argb(0xff000000));
    }

    /**
     * Set the text value and unset the function
     */
    public ComponentText val(String str) {
        text.setValue(str);
        text.noFunc();
        return this;
    }

    /**
     * Set the callback to create the text for
     *
     * @param func
     * @return
     */
    public ComponentText func(Function<ComponentText, String> func) {
        text.func(func);
        return this;
    }

    /**
     * Will set the fontRenderer's bidi flag and unicode flag to true
     */
    public void enableFontFlags() {
        enableFlags = true;
    }

    @Override
    public void drawComponent(Vec2 mousePos, float partialTicks) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        if (enableFlags) {
            fr.setBidiFlag(true);
            fr.setUnicodeFlag(true);


            String val = text.getValue(this);

            int x = pos.xi;
            int y = pos.yi;

            // TODO: Align properly
            int textWidth = fr.getStringWidth(val);
            if (horizontal == TextAlignH.CENTER) {
                x -= textWidth / 2;
            } else if (horizontal == TextAlignH.RIGHT) {
                x -= textWidth;
            }
            if (vertical == TextAlignV.MIDDLE) {
                y -= fr.FONT_HEIGHT / 2;
            } else if (vertical == TextAlignV.BOTTOM) {
                y -= fr.FONT_HEIGHT;
            }

            int wrap = this.wrap.getValue(this);
            if (wrap == -1)
                fr.drawString(val, x, y, color.getValue(this).hexARGB());
            else fr.drawSplitString(val, x, y, wrap, color.getValue(this).hexARGB());


            fr.setBidiFlag(false);
            fr.setUnicodeFlag(false);
        } else {
            String val = text.getValue(this);

            int x = pos.xi;
            int y = pos.yi;

            int textWidth = fr.getStringWidth(val);
            if (horizontal == TextAlignH.CENTER) {
                x -= textWidth / 2;
            } else if (horizontal == TextAlignH.RIGHT) {
                x -= textWidth;
            }
            if (vertical == TextAlignV.MIDDLE) {
                y -= fr.FONT_HEIGHT / 2;
            } else if (vertical == TextAlignV.BOTTOM) {
                y -= fr.FONT_HEIGHT;
            }

            int wrap = this.wrap.getValue(this);
            if (wrap == -1)
                fr.drawString(val, x, y, color.getValue(this).hexARGB());
            else fr.drawSplitString(val, x, y, wrap, color.getValue(this).hexARGB());
        }
    }

    @Override
    protected BoundingBox2D getContentSize() {
        int wrap = this.wrap.getValue(this);

        Vec2 size;

        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

        if (wrap == -1) {
            size = new Vec2(fr.getStringWidth(text.getValue(this)), fr.FONT_HEIGHT);
        } else {
            List<String> wrapped = fr.listFormattedStringToWidth(text.getValue(this), wrap);
            size = new Vec2(wrap, wrapped.size() * fr.FONT_HEIGHT);
        }

        return new BoundingBox2D(Vec2.ZERO, size);
    }

    public enum TextAlignH {
        LEFT, CENTER, RIGHT
    }

    public enum TextAlignV {
        TOP, MIDDLE, BOTTOM
    }

}
