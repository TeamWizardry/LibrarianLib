package com.teamwizardry.librarianlib.features.saving

import net.minecraft.util.EnumFacing
import kotlin.annotation.AnnotationTarget.*

/**
 * @author WireSegal
 * Created at 3:58 PM on 10/27/16.
 *
 * Apply this to a field to have it be serialized by the write/read nbt methods and write/read byte methods.
 *
 * If the field is annotated with @[NotNullAcceptor], a default value from [DefaultValues] will be used instead of null.
 *
 * [saveName] doesn't matter for messages, except in sorting. It's for NBT serializers.
 */
@Target(FIELD)
@MustBeDocumented
annotation class Save(val saveName: String = "")

/**
 * @author WireSegal
 * Created at 9:30 AM on 3/13/17.
 *
 * Apply this to a field to have it be marked as not accepting nulls on syncing.
 *
 * The field will receive a [DefaultValues] entry if it exists instead of null.
 */
@Target(FIELD, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@MustBeDocumented
annotation class NotNullAcceptor

/**
 * @author WireSegal
 * Created at 9:24 AM on 11/11/16.
 *
 * Apply this to a field or function annotated with [Save] or [SaveMethodSetter]/[SaveMethodGetter] to prevent
 * the data from syncing to clients, but still get saved to NBT. This does not apply to packets.
 * [NoSync] needs to be applied to both a setter and a getter in the case of method annotations.
 *
 * If [NoSync] is applied to a field/method with @[NotNullAcceptor] without a [DefaultValues] entry,
 * it will crash. This is because it sends nulls instead of regular values when trying to sync,
 * to prevent syncing overhead.
 */
@Target(FIELD, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@MustBeDocumented
annotation class NoSync

/**
 * @author WireSegal
 * Created at 5:33 PM on 10/31/16.
 *
 * Apply this to a method to mark it as the getter component for reading/writing a property.
 * A method must exist in the same class with the same [saveName]
 * and with the return type of this function as its single parameter,
 * annotated with [SaveMethodSetter], otherwise nothing will be saved.
 *
 * If the getter is annotated with @[NotNullAcceptor], a default value from [DefaultValues] will be used instead of null.
 *
 * The "getter" method must take exactly zero parameters, and return the content of the field.
 */
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@MustBeDocumented
annotation class SaveMethodGetter(val saveName: String)

/**
 * @author WireSegal
 * Created at 5:38 PM on 10/31/16.
 *
 * Apply this to a method to mark it as the setter component for reading/writing a property.
 * A method must exist in the same class with the same [saveName]
 * and with the input type of this function as its return type,
 * annotated with [SaveMethodGetter], otherwise nothing will be saved.
 *
 * The "setter" method must take exactly one parameter, and its return value will be ignored.
 */
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@MustBeDocumented
annotation class SaveMethodSetter(val saveName: String)

/**
 * @author TheCodeWarrior
 *
 * Apply this to a class to mark it as serializable.
 *
 * The class must have:
 *  - one or more [Save] annotated fields and/or one or more [SaveMethodGetter] annotated methods, or
 *  - no such fields or methods and one or more non-transient fields
 *
 * In order to construct the class it must have:
 *  - a zero argument constructor
 *  - a constructor with arguments similarly named and typed to the saved properties
 *
 * If any of the saved fields are final, or if there are any [SaveMethodGetter]s without a matching [SaveMethodSetter],
 * the class will be marked as immutable and cannot use a zero argument constructor
 */
@Target(CLASS)
@MustBeDocumented
annotation class Savable

/**
 * @author WireSegal
 * Created at 3:18 PM on 1/6/17.
 *
 * Apply this to a capability-containing field to have it be automatically provided (from TileMods) to the provided [sides]
 * and null. This will apply to all capability superinterfaces of the annotated field type.
 */
@Target(FIELD)
@MustBeDocumented
annotation class CapabilityProvide(vararg val sides: EnumFacing)


/**
 * Marks this class and all of its subclasses to be saved in place. Only [Save] annotated fields will be saved.
 *
 * The automatic object serializer will not create any new instances of annotated classes.
 */
@Target(CLASS)
@MustBeDocumented
annotation class SaveInPlace

/**
 * Specifies the order in which the fields appear in the constructor for dynamic deserialization.
 *
 */
@Target(CONSTRUCTOR)
@MustBeDocumented
annotation class SavableConstructorOrder(vararg val params: String)
