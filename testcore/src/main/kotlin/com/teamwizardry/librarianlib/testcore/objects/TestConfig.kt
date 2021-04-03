package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.sided.SidedRunnable
import net.minecraftforge.fml.ModLoadingContext

public abstract class TestConfig {
    public val modid: String = ModLoadingContext.get().activeContainer.modId

    /**
     * Additional text to show in the item tooltip
     */
    public open var description: String? = null

    public inline fun client(crossinline block: ClientActions.() -> Unit): ClientActions = ClientActions.also {
        SidedRunnable.client {
            it.block()
        }
    }

    public inline fun server(block: ServerActions.() -> Unit): ServerActions = ServerActions.also { it.block() }
    public inline fun common(block: CommonActions.() -> Unit): CommonActions = CommonActions.also { it.block() }
}