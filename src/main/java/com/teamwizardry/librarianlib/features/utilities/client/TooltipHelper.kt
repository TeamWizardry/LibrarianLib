package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Utilities for tooltips.
 */
object TooltipHelper {

    /**
     * If the client player is sneaking, invoke the given lambda, and otherwise add a 'press shift for info'.
     */
    @SideOnly(Side.CLIENT)
    fun tooltipIfShift(tooltip: MutableList<String>, lambda: (MutableList<String>) -> Unit) {
        if (GuiScreen.isShiftKeyDown())
            lambda(tooltip)
        else
            addToTooltip(tooltip, "${LibrarianLib.MODID}.shiftinfo")
    }

    /**
     * A wrapper for easy access from java of the tooltipIfShift function.
     */
    @SideOnly(Side.CLIENT)
    @JvmStatic
    fun tooltipIfShift(tooltip: MutableList<String>, lambda: Runnable) = tooltipIfShift(tooltip, { lambda.run() })

    /**
     * Add something to the tooltip that's translated and colorized.
     */
    @JvmStatic
    fun addToTooltip(tooltip: MutableList<String>, s: String, vararg format: Any?) {
        tooltip.add(local(s, *format).replace("&".toRegex(), "§"))
    }

    /**
     * Localize a key and format it.
     */
    @JvmStatic
    fun local(s: String, vararg format: Any?): String {
        return LibrarianLib.PROXY.translate(s, *format)
    }

    @JvmStatic
    fun addDynamic(tooltip: MutableList<String>, desc: String, vararg args: Any?) {
        val used = if (LibrarianLib.PROXY.canTranslate(desc)) desc else desc + "0"
        if (LibrarianLib.PROXY.canTranslate(used)) {
            TooltipHelper.addToTooltip(tooltip, used)
            var i = 0
            while (LibrarianLib.PROXY.canTranslate(desc + ++i))
                TooltipHelper.addToTooltip(tooltip, desc + i, *args)
        }
    }
}
