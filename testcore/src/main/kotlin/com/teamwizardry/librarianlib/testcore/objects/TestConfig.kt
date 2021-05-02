package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.testcore.TestModManager
import net.minecraft.util.Identifier

public abstract class TestConfig(public val manager: TestModManager, public val id: Identifier) {
    /**
     * Additional text to show in the item tooltip
     */
    public open var description: String? = null

    public inline fun client(block: ClientActionScope.() -> Unit): ClientActionScope = ClientActionScope.apply(block)
    public inline fun server(block: ServerActionScope.() -> Unit): ServerActionScope = ServerActionScope.apply(block)
    public inline fun common(block: CommonActionScope.() -> Unit): CommonActionScope = CommonActionScope.apply(block)
}