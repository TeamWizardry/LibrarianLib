package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.registration.SoundEventSpec
import net.minecraft.util.SoundEvent

object ModSounds {
    lateinit var testSound: SoundEvent
        private set

    internal fun registerSounds(registrationManager: RegistrationManager) {
        testSound = registrationManager.add(
            SoundEventSpec("test_sound")
                .subtitle("foundation.subtitle.testSound")
                .sound(loc("minecraft:mob/cow/say1"))
                .sound(loc("minecraft:mob/pig/say1")) {
                    pitch = 1.2
                }
        )
    }
}