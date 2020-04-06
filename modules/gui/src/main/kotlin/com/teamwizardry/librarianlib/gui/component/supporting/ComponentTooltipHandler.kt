package com.teamwizardry.librarianlib.gui.component.supporting

import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiLayer
//import com.teamwizardry.librarianlib.gui.provided.pastry.components.PastryBasicTooltip
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.IMValueInt
import com.teamwizardry.librarianlib.gui.value.RMValue

interface IComponentTooltip {
    /**
     * @see tooltipText
     */
    val tooltipText_im: IMValue<String?>

    /**
     * The text to display as a tooltip when the mouse is over this component. If [tooltip] is nonnull this value will
     * be ignored.
     */
    var tooltipText: String?

    /**
     * @see tooltip
     */
    val tooltip_rm: RMValue<GuiLayer?>

    /**
     * The layer to display as a tooltip when the mouse is over this component. If this value is null it will fall back
     * to [tooltipText].
     */
    var tooltip: GuiLayer?

    /**
     * @see tooltipDelay
     */
    val tooltipDelay_im: IMValueInt

    /**
     * How many ticks should the mouse have to hover over this component before the tooltip appears.
     */
    var tooltipDelay: Int

    val tooltipLayer: GuiLayer?
}

class ComponentTooltipHandler: IComponentTooltip {
    lateinit var component: GuiComponent

//    private val _tooltipTextLayer = PastryBasicTooltip()

    override val tooltipText_im: IMValue<String?> = IMValue()
    override var tooltipText: String? by tooltipText_im
    override val tooltip_rm: RMValue<GuiLayer?> = RMValue(null)
    override var tooltip: GuiLayer? by tooltip_rm
    override val tooltipDelay_im: IMValueInt = IMValueInt(0)
    override var tooltipDelay: Int by tooltipDelay_im

    override val tooltipLayer: GuiLayer?
        get() {
            tooltip?.also { return it }
            tooltipText?.also {
//                _tooltipTextLayer.text = it
//                return _tooltipTextLayer
            }
            return null
        }
}
