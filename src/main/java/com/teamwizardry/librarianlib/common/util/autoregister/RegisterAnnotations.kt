package com.teamwizardry.librarianlib.common.util.autoregister

import net.minecraft.tileentity.TileEntity;

/**
 * Apply this to a class that extends [TileEntity] to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class TileRegister(val value: String)

/**
 * Apply this to a class that extends PartMod to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class PartRegister(val value: String)
