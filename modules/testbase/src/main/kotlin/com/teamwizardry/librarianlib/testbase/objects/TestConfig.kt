package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedRunnable
import net.minecraftforge.fml.ModLoadingContext

abstract class TestConfig {
    val modid: String = ModLoadingContext.get().activeContainer.modId

    /**
     * Additional text to show in the item tooltip
     */
    open var description: String? = null

    inline fun client(crossinline block: ClientActions.() -> Unit) = ClientActions.also {
        SidedRunnable.client {
            it.block()
        }
    }
    inline fun server(block: ServerActions.() -> Unit) = ServerActions.also { it.block() }
    inline fun common(block: CommonActions.() -> Unit) = CommonActions.also { it.block() }
}