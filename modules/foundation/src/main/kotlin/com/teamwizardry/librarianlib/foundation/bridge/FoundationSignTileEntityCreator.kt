package com.teamwizardry.librarianlib.foundation.bridge

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.tileentity.SignTileEntity
import net.minecraft.tileentity.TileEntityType
import java.lang.IllegalStateException

/**
 * The vanilla SignTileEntity hard-codes the vanilla TileEntityType, which means it's impossible to use it with other
 * sign blocks. I could spend a couple days setting up a Forge environment and creating a patch, or I could sit staring
 * at a wall for a couple days and get the same results without waiting three months for the stale bot to close my PR.
 *
 * To do this we look in the `SignTileEntity` constructor for the superclass constructor call, then use
 * [interceptSignTileEntityType] to intercept the parameter off the stack. That method then checks the thread-local
 * [tileEntityTypeOverride] property. If there is an override it returns that, otherwise it returns the input unchanged.
 */
public object FoundationSignTileEntityCreator {
    private var tileEntityTypeOverride: TileEntityType<SignTileEntity>? by threadLocal()

    @JvmStatic
    public fun create(type: TileEntityType<SignTileEntity>): SignTileEntity {
        tileEntityTypeOverride = type
        try {
            val te = SignTileEntity()
            if(te.type != type) {
                throw IllegalStateException("Sign tile entity type wasn't overridden! The actual type was " +
                        "${te.type.registryName}, not ${type.registryName}. Did another mod add a destructive ASM " +
                        "transformer?")
            }
            return te
        } finally {
            tileEntityTypeOverride = null
        }
    }

    @JvmStatic
    @JvmSynthetic
    public fun interceptSignTileEntityType(originalType: TileEntityType<*>?): TileEntityType<*>? {
        return tileEntityTypeOverride ?: originalType
    }
}