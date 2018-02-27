package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent

abstract class NavBarHolder(posX: Int, posY: Int, width: Int, height: Int, book: IBookGui) : GuiComponent(posX, posY, width, height) {

    val pages: MutableList<GuiComponent> = mutableListOf()
    var navBar = ComponentNavBar(book, size.xi / 2 - 35, size.yi + 16, 70, pages.size)
    open var currentActive: GuiComponent? = null
    private var first = true

    init {
        add(navBar)

        navBar.BUS.hook(EventNavBarChange::class.java) { update() }
    }

    protected fun addPage(pageComponent: GuiComponent) {
        if (first) {
            currentActive = pageComponent
            first = false
        } else {
            pageComponent.isVisible = false
        }

        add(pageComponent)
        pages.add(pageComponent)

        navBar.maxPages = pages.size - 1
    }

    protected open fun update() {
        currentActive?.isVisible = false
        currentActive = pages[navBar.page]
        currentActive?.isVisible = true
    }
}
