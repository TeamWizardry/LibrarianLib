package com.teamwizardry.librarianlib.common.util.autoregister

import com.teamwizardry.librarianlib.common.base.multipart.PartMod
import com.teamwizardry.librarianlib.common.network.PacketBase
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side

/**
 * Apply this to a class that extends [TileEntity] to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 *
 * Should [value] be without a prefix, the register will attempt to assign it one
 * based on the mod jar in which it resides. If multiple mods are in the same jar,
 * this can cause undefined behavior.
 *
 * Should [value] be empty, the register will assign it a name based on the class name.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class TileRegister(val value: String = "")

/**
 * Apply this to a class that extends [PartMod] to have it be automatically registered.
 *
 * [value] is the name or domain:name to register as.
 *
 * Should [value] be without a prefix, the register will attempt to assign it one
 * based on the mod jar in which it resides. If multiple mods are in the same jar,
 * this can cause undefined behavior.
 *
 * Should [value] be empty, the register will assign it a name based on the class name.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class PartRegister(val value: String = "")

/**
 * Apply this to a class that extends [PacketBase] to have it be automatically registered.
 *
 * [value] is the side the packet will be received on.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class PacketRegister(val value: Side)
