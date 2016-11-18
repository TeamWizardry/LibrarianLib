package com.teamwizardry.librarianlib.common.base.block

import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional

/**
 * @author WireSegal
 * Created at 8:17 PM on 11/17/16.
 *
 * Only construct this class if you know what you're doing.
 * Used to prevent [ClassNotFoundException] when TOP is not present.
 */
class ProbeInfoWrapper(private val mode: Any, private val info: Any, private val hitData: Any) {

    private val modeClazz: Class<*> by lazy { Class.forName("mcjty.theoneprobe.api.ProbeMode") }
    private val infoClazz: Class<*> by lazy { Class.forName("mcjty.theoneprobe.api.IProbeInfo") }
    private val dataClazz: Class<*> by lazy { Class.forName("mcjty.theoneprobe.api.IProbeHitData") }

    init {
        if (!Loader.isModLoaded("theoneprobe"))
            throw ClassNotFoundException("TOP is not loaded, constructing a ProbeInfoWrapper is impossible!")
        if (!modeClazz.isInstance(mode) || !infoClazz.isInstance(info) || !dataClazz.isInstance(hitData))
            throw IllegalArgumentException("ProbeInfoWrapper constructed with invalid objects! This is on the mod author!")
    }

    /**
     * Get the probe mode that should be used for determining data.
     */
    @Optional.Method(modid = "theoneprobe") fun getProbeMode() = mode as ProbeMode

    /**
     * Get the probe info that should be written to.
     */
    @Optional.Method(modid = "theoneprobe") fun getProbeInfo() = info as IProbeInfo

    /**
     * Get the probe's hit information (side, pos, hit vec).
     */
    @Optional.Method(modid = "theoneprobe") fun getHitData() = hitData as IProbeHitData
}
