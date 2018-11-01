package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace

class RootComponent: GuiComponent(0, 0) {
    override var size: Vec2d
        get() = super.size
        set(value) {}
    override var pos: Vec2d
        get() = super.pos
        set(value) {}
    override var translateZ: Double
        get() = super.translateZ
        set(value) {}
    override var scale2d: Vec2d
        get() = super.scale2d
        set(value) {}
    override var rotation: Double
        get() = super.rotation
        set(value) {}
    override var anchor: Vec2d
        get() = super.anchor
        set(value) {}
    override var contentsOffset: Vec2d
        get() = super.contentsOffset
        set(value) {}

    override fun glApplyTransform() {
        // nop
    }

    override val parentSpace: CoordinateSpace2D?
        get() = ScreenSpace
    override val parent: GuiLayer?
        get() = null
    override val root: GuiLayer
        get() = this
}