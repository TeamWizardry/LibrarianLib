package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.StringTextComponent

@TestObjectDslMarker
abstract class TestContext {
}

/**
 * A common class for all test contexts that include a player, providing methods to easily respond to them
 */
abstract class PlayerTestContext(player: PlayerEntity): TestContext() {
    private val _player: PlayerEntity = player
    val sneaking: Boolean get() = _player.isSneaking

    inline fun <T> sneaking(block: () -> T): T? {
        if(sneaking)
            return block()
        return null
    }

    inline fun <T> notSneaking(block: () -> T): T? {
        if(!sneaking)
            return block()
        return null
    }

    fun chat(text: String) {
        _player.sendMessage(StringTextComponent(text))
    }

    fun status(text: String) {
        _player.sendStatusMessage(StringTextComponent(text), true)
    }
}
