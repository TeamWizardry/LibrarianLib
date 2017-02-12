package com.teamwizardry.librarianlib.common.base

import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * @author WireSegal
 * Created at 7:58 PM on 2/11/17.
 */
class ModSoundEvent(name: String) : SoundEvent(ResourceLocation(currentModId, name)) {
    init {
        GameRegistry.register(this, ResourceLocation(currentModId, name))
    }
}
