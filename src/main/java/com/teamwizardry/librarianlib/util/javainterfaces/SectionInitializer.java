package com.teamwizardry.librarianlib.util.javainterfaces;

import com.teamwizardry.librarianlib.book.gui.GuiBook;
import com.teamwizardry.librarianlib.book.util.BookSectionOther;
import com.teamwizardry.librarianlib.data.DataNode;

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
public interface SectionInitializer {
	GuiBook invoke(BookSectionOther section, DataNode node, String tag);
}
