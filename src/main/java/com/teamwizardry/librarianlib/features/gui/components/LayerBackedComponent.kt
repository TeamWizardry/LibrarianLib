package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerClipping
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerGeometry
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerRelationships
import com.teamwizardry.librarianlib.features.gui.component.supporting.ILayerRendering

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class LayerBackedComponent(val layer: GuiLayer): GuiComponent(0, 0, 0, 0),
    ILayerGeometry by layer, ILayerRelationships by layer,
    ILayerRendering by layer, ILayerClipping by layer {
}