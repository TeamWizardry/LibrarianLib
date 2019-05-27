package com.teamwizardry.librarianlib.features.neogui.provided.book.context

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent

/**
 * @author WireSegal
 * Created at 8:12 PM on 8/7/18.
 */

data class PaginationContext(val component: () -> GuiComponent, val pageBookmarks: List<Bookmark>) {
    constructor(component: () -> GuiComponent) : this(component, listOf())
}