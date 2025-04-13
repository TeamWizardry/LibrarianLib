package com.teamwizardry.librarianlib.testcore.util

import com.teamwizardry.librarianlib.testcore.content.TestConfigDslMarker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

@TestConfigDslMarker
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
        _player.sendMessage(Text.literal(text), false)
    }

    public fun status(text: String) {
        _player.sendMessage(Text.literal(text), true)
    }
}
