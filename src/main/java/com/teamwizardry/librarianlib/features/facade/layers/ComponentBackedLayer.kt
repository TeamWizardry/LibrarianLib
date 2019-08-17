package com.teamwizardry.librarianlib.features.facade.layers

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.supporting.ILayerBase
import com.teamwizardry.librarianlib.features.facade.component.supporting.ILayerClipping
import com.teamwizardry.librarianlib.features.facade.component.supporting.ILayerGeometry
import com.teamwizardry.librarianlib.features.facade.component.supporting.ILayerRelationships
import com.teamwizardry.librarianlib.features.facade.component.supporting.ILayerRendering

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class ComponentBackedLayer(val component: GuiComponent): GuiLayer(0, 0, 0, 0),
    ILayerGeometry by component, ILayerRelationships by component,
    ILayerRendering by component, ILayerClipping by component, ILayerBase by component {
    init {
        BUS.delegateTo(component.BUS)
    }

    override val parent: GuiLayer?
        get() = component.parent

    override fun setParentInternal(value: GuiLayer?) {
        component.setParentInternal(value)
    }

    override fun componentWrapper(): GuiComponent {
        return component
    }
}
