package com.teamwizardry.librarianlib.common.util.saving

/**
 * @author WireSegal
 * Created at 3:58 PM on 10/27/16.
 *
 * Apply this to a field to have it be serialized by the write/read nbt methods.
 *
 * [saveName] doesn't matter for messages, except in sorting. It's for NBT serializers.
 */
@Target(AnnotationTarget.FIELD)
annotation class Save(val saveName: String = "")
