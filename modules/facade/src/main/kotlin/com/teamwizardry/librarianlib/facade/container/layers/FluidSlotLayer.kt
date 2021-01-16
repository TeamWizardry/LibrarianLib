package com.teamwizardry.librarianlib.facade.container.layers

import com.teamwizardry.librarianlib.facade.container.slot.FluidSlot
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.minecraft.FluidGaugeLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground

public class FluidSlotLayer @JvmOverloads constructor(
    public val slot: FluidSlot,
    posX: Int, posY: Int,
    width: Int, height: Int,
    showBackground: Boolean = false
) : GuiLayer(posX, posY, width, height) {
    /**
     * Whether to show the default slot background.
     */
    public var showBackground: Boolean = showBackground

    public val gauge: FluidGaugeLayer = FluidGaugeLayer(0, 0, width, height)

    private val background: PastryBackground =
        PastryBackground(PastryBackgroundStyle.LIGHT_INSET, -1, -1, width + 2, height + 2)

    init {
        add(background, gauge)
        background.isVisible_im.set { this.showBackground }
        gauge.content_im.set { slot.fluid }
        gauge.capacity_im.set { slot.capacity }
        gauge.floatLightFluids = true
    }

    override fun layoutChildren() {
        gauge.frame = this.bounds
        background.frame = this.bounds.grow(1.0)
    }
}