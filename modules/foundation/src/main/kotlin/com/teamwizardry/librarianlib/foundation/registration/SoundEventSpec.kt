package com.teamwizardry.librarianlib.foundation.registration

import com.teamwizardry.librarianlib.core.util.kotlin.builder
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import java.util.function.Consumer

/**
 * The specs for generating a sound event in the `sounds.json` file
 */
public class SoundEventSpec(
    public val name: String
) {
    /**
     * The mod ID this sound is under. This is populated by the [RegistrationManager].
     */
    public var modid: String = ""
        @JvmSynthetic
        internal set

    /**
     * The registry name of the sound. The [mod ID][modid] is populated by the [RegistrationManager].
     */
    public val registryName: ResourceLocation
        get() = ResourceLocation(modid, name)

    public val soundInstance: SoundEvent by lazy {
        SoundEvent(registryName).setRegistryName(registryName)
    }

    // datagen info:

    @get:JvmSynthetic
    internal var subtitle: String? = null
        private set

    @get:JvmSynthetic
    internal val sounds: MutableList<SoundFileInfo> = mutableListOf()

    /**
     * Set the subtitle for data generation.
     */
    public fun subtitle(value: String): SoundEventSpec = builder { subtitle = value }

    /**
     * Add a sound file for data generation.
     */
    public fun sound(name: ResourceLocation): SoundEventSpec = builder { sounds.add(SoundFileInfo(name)) }

    /**
     * Add a sound file for data generation.
     */
    public fun sound(name: ResourceLocation, config: Consumer<SoundFileInfo>): SoundEventSpec = builder {
        val sound = SoundFileInfo(name)
        config.accept(sound)
        sounds.add(sound)
    }

    /**
     * Add a sound file for data generation.
     */
    @JvmSynthetic
    public inline fun sound(name: ResourceLocation, crossinline config: SoundFileInfo.() -> Unit): SoundEventSpec =
        sound(name, Consumer { it.config() })

    public inner class SoundFileInfo(
        public val name: ResourceLocation
    ) {
        public var volume: Double? = null
        public var pitch: Double? = null
        public var weight: Int? = null
        public var stream: Boolean? = null
        public var attenuationDistance: Int? = null
        public var preload: Boolean? = null
        public var type: SoundFileType? = null
    }

    public enum class SoundFileType(public val jsonName: String) {
        SOUND("sound"), EVENT("event")
    }
}