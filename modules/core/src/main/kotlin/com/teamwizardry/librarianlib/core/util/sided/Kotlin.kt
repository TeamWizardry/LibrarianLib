package com.teamwizardry.librarianlib.core.util.sided

/**
 * Runs the passed block on the client side, stripping the block out on the dedicated server side
 */
@JvmSynthetic
public fun clientOnly(block: ClientRunnable) {
    block.run()
}
