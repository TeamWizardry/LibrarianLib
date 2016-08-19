package com.teamwizardry.librarianlib.common.util.lambdainterfs;

import com.teamwizardry.librarianlib.client.book.data.DataNode;
import com.teamwizardry.librarianlib.client.book.gui.GuiBook;
import com.teamwizardry.librarianlib.client.book.util.BookSectionOther;

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
public interface SectionInitializer {
    GuiBook invoke(BookSectionOther section, DataNode node, String tag);
}
