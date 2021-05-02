package com.teamwizardry.librarianlib.testcore.objects

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText

@TestObjectDslMarker
public abstract class TestContext {
}

/**
 * A common class for all test contexts that include a player, providing methods to easily respond to them
 */
public abstract class PlayerTestContext(player: PlayerEntity): TestContext() {
    private val _player: PlayerEntity = player
    public val sneaking: Boolean get() = _player.isSneaking

    public inline fun <T> sneaking(block: () -> T): T? {
        if (sneaking)
            return block()
        return null
    }

    public inline fun <T> notSneaking(block: () -> T): T? {
        if (!sneaking)
            return block()
        return null
    }

    public fun chat(text: String) {
        _player.sendMessage(LiteralText(text), false)
    }

    public fun status(text: String) {
        _player.sendMessage(LiteralText(text), true)
    }
}
