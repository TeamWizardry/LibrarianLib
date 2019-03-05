package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerBase
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerClipping
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerGeometry
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerRelationships
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerRendering

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class LayerBackedComponent(val layer: GuiLayer): GuiComponent(0, 0, 0, 0),
    ILayerGeometry by layer, ILayerRelationships by layer,
    ILayerRendering by layer, ILayerClipping by layer, ILayerBase by layer {
    init {
        BUS.delegateTo(layer.BUS)
    }

    override val root: GuiComponent
        get() = layer.root as GuiComponent
    override val parent: GuiComponent?
        get() = layer.parent as GuiComponent?

    override fun setParentInternal(value: GuiLayer?) {
        layer.setParentInternal(value)
    }

    override fun drawDebugBoundingBox() {
        super.drawDebugBoundingBox()
    }

    override fun add(vararg layers: GuiLayer) {
        layers.forEach { (it as? GuiComponent)?.allowAddingToLayer = true }
        layer.add(*layers)
        layers.forEach { (it as? GuiComponent)?.allowAddingToLayer = false }
    }

    override fun debugInfo(): MutableList<String> {
        val list = layer.debugInfo()
        super.addGuiComponentDebugInfo(list)
        return list
    }

    override fun layerWrapper(): GuiLayer {
        return layer
    }
}

