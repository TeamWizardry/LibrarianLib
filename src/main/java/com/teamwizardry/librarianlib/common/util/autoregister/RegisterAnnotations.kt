package com.teamwizardry.librarianlib.common.util.autoregister

// todo once mcmultipart is 1.11
//import com.teamwizardry.librarianlib.common.base.multipart.PartMod
import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.saving.serializers.Serializer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.relauncher.Side
import kotlin.reflect.KClass

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

/**
 * Apply this to a class that extends [Serializer] to have it automatically be registered for those classes.
 *
 * [classes] are the classes that this serializer will be applied to.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class SerializerRegister(vararg val classes: KClass<*>)

/**
 * Apply this to a class that extends [SerializerFactory] to have it automatically be registered.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class SerializerFactoryRegister()
