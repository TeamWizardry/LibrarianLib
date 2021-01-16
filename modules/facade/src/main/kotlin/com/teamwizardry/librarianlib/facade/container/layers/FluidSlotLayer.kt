package com.teamwizardry.librarianlib.facade.container.layers

import com.teamwizardry.librarianlib.facade.container.slot.FluidSlot
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.minecraft.FluidGaugeLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.math.Vec2d
import java.awt.Color

public class FluidSlotLayer @JvmOverloads constructor(
    public val slot: FluidSlot,
    posX: Int, posY: Int,
    width: Int, height: Int,
    showBackground: Boolean = false
) : GuiLayer(posX, posY, width, height), JeiIngredientLayer {
    /**
     * Whether to show the default slot background.
     */
    public var showBackground: Boolean = showBackground

    /**
     * Whether this layer should expose its contents to JEI for the view recipe/view uses keys.
     */
    public var exposeJeiIngredient: Boolean = true

    /**
     * Whether this layer should have an overlay when the mouse is over it, similar to slots.
     */
    public var enableOverlay: Boolean = true

    public val gauge: FluidGaugeLayer = FluidGaugeLayer(0, 0, width, height)

    private val background: PastryBackground =
        PastryBackground(PastryBackgroundStyle.INPUT, -1, -1, width + 2, height + 2)
    private val overlay: RectLayer = RectLayer(Color(1f, 1f, 1f, 0.5f), 0, 0, width, height)

    init {
        add(background, gauge, overlay)
        background.isVisible_im.set { this.showBackground }
        overlay.isVisible_im.set { enableOverlay && mouseOver }

        gauge.content_im.set { slot.fluid }
        gauge.capacity_im.set { slot.capacity }
        gauge.floatLightFluids = true
    }

    override fun layoutChildren() {
        gauge.frame = this.bounds
        background.frame = this.bounds.grow(1.0)
    }

    override fun getJeiIngredient(pos: Vec2d): Any? {
        return if(exposeJeiIngredient) slot.fluid else null
    }
}