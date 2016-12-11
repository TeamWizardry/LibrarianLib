package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.common.base.multipart.PartMod
import net.minecraft.tileentity.TileEntity

/**
 * Apply this to a class that extends [TileEntity] to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 *
 * Should [value] be without a prefix, the register will attempt to assign it one
 * based on the mod jar in which it resides. If multiple mods are in the same jar,
 * this can cause undefined behavior.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class TileRegister(val value: String)

/**
 * Apply this to a class that extends [PartMod] to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 *
 * Should [value] be without a prefix, the register will attempt to assign it one
 * based on the mod jar in which it resides. If multiple mods are in the same jar,
 * this can cause undefined behavior.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class PartRegister(val value: String)
