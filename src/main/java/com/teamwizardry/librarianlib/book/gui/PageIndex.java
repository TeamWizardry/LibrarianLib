package com.teamwizardry.librarianlib.book.gui;

import com.teamwizardry.librarianlib.api.gui.components.ComponentSliderTray;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.api.gui.components.template.SliderTemplate;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Link;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.common.network.data.DataNode;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PageIndex extends GuiBook {

    public PageIndex(Book book, DataNode rootData, DataNode pageData, Page page) {
        super(book, rootData, pageData, page);

        List<DataNode> icons = pageData.get("icons").asList();

        Color normalColor = Color.BLACK;
        Color hoverColor = Color.rgb(0x00BFFF);
        // TODO: pressed color not working
        Color pressColor = Color.rgb(0x191970);

        int x = 0;
        int y = 0;
        int w = 32;
        int h = 32;
        int sep = 10;

        for (DataNode icon : icons) {

            // TODO: change margin based on row so it centers a row with, for example, 2 elements instead of 3
            int margin = (PAGE_WIDTH / 2) - ((w + sep) * (PAGE_WIDTH / ((w + sep)))) / 2;

            contents.add(new ComponentSprite(new Sprite(new ResourceLocation(icon.get("icon").asStringOr("missingno"))), margin + x, y, w, h).setup((i) -> {
                AtomicReference<ComponentSliderTray> s = new AtomicReference<>(null);
                new ButtonMixin(i,
                        () -> i.color.setValue(normalColor), () -> i.color.setValue(hoverColor), () -> i.color.setValue(pressColor),
                        () -> {
                            Link l = new Link(icon.get("link").asStringOr("/"));
                            openPageRelative(l.path, l.page);
                        }
                );
                i.mouseIn.add((c, pos) -> {
                    if (s.get() != null)
                        s.get().invalidate();
                    ComponentSliderTray slider = SliderTemplate.text(c.getPos().yi, icon.get("text").asStringOr("<NULL>"));

                    tips.add(slider);
                    s.set(slider);
                    return false;
                });
                i.mouseOut.add((c, pos) -> {
                    if (s.get() != null)
                        s.get().close();
                    s.set(null);
                    return false;
                });
            }));
            x += w + sep;
            if (x > PAGE_WIDTH - w) {
                x = 0;
                y += w + h;
            }
        }
    }
}
