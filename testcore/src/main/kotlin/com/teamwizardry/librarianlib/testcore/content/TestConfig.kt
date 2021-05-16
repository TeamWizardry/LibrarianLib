package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.util.ClientActionScope
import com.teamwizardry.librarianlib.testcore.util.CommonActionScope
import com.teamwizardry.librarianlib.testcore.util.ServerActionScope
import net.devtech.arrp.api.RuntimeResourcePack
import net.minecraft.util.Identifier

public sealed class TestConfig(public val manager: TestModContentManager, public val id: Identifier) {
    /**
     * Human-readable name. Defaults to the id's path component
     */
    public open var name: String = id.path
    /**
     * Additional description text. Used in the item tooltip and potentially elsewhere in the future
     */
    public open var description: String? = null

    internal open fun registerCommon(resources: TestModResourceManager) {}
    internal open fun registerClient(resources: TestModResourceManager) {}
    internal open fun registerServer(resources: TestModResourceManager) {}

    public inline fun client(block: ClientActionScope.() -> Unit): ClientActionScope = ClientActionScope.apply(block)
    public inline fun server(block: ServerActionScope.() -> Unit): ServerActionScope = ServerActionScope.apply(block)
    public inline fun common(block: CommonActionScope.() -> Unit): CommonActionScope = CommonActionScope.apply(block)
}

public inline fun <T: TestConfig> T.configure(block: T.() -> Unit): T = this.apply(block)
