package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import net.minecraft.util.Identifier

public sealed class TestConfig(public val manager: TestModContentManager, public val id: Identifier) {
    /**
     * Additional text to show in the item tooltip
     */
    public open var description: String? = null

    public inline fun client(block: ClientActionScope.() -> Unit): ClientActionScope = ClientActionScope.apply(block)
    public inline fun server(block: ServerActionScope.() -> Unit): ServerActionScope = ServerActionScope.apply(block)
    public inline fun common(block: CommonActionScope.() -> Unit): CommonActionScope = CommonActionScope.apply(block)
}