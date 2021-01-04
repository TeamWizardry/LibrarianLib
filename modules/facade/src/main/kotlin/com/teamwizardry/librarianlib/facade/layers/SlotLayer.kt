package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.util.mapSrgName
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.ContainerSpace
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.core.util.vec
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.inventory.container.Slot

/**
 * A layer that defines the position and visibility of a container slot
 */
public class SlotLayer @JvmOverloads constructor(
    public val slot: Slot,
    posX: Int, posY: Int,
    showBackground: Boolean = false
): GuiLayer(posX, posY, 16, 16) {
    /**
     * Whether to show the default slot background.
     */
    public var showBackground: Boolean = showBackground

    private val background: PastryBackground = PastryBackground(PastryBackgroundStyle.INPUT, -1, -1, 18, 18)

    init {
        add(background)
        background.isVisible_im.set { this.showBackground }
    }

    override fun draw(context: GuiDrawContext) {
        super.draw(context)
        val rootPos = ContainerSpace.convertPointFrom(vec(0, 0), this)
        xPosMirror.set(slot, rootPos.xi)
        yPosMirror.set(slot, rootPos.yi)
    }

    private companion object {
        private val xPosMirror = Mirror.reflectClass<Slot>().getDeclaredField(mapSrgName("field_75223_e"))
        private val yPosMirror = Mirror.reflectClass<Slot>().getDeclaredField(mapSrgName("field_75221_f"))
    }
}

