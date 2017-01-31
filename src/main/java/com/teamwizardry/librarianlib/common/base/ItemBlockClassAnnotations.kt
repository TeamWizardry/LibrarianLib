package com.teamwizardry.librarianlib.common.base

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.core.OwnershipHandler
import com.teamwizardry.librarianlib.common.util.AnnotationHelper
import com.teamwizardry.librarianlib.common.util.modIdOverride
import net.minecraft.block.Block
import net.minecraft.item.Item

/**
 * Created by Elad on 1/21/2017.
 *
 * Apply this annotation to classes and their fields will automatically be initialized with
 * a new instance of the type they hold. This is to be used with Item and Block classes.
 * In case the resources are [Block]s or [Item]s, making them [BlockMod]s or
 * [ItemMod]s respectively will allow them to automatically be registered as needed
 * when populated to a resource class.
 * This annotation will only work on classes with fields with types with constructors
 * that only take one argument.
 * This annotation ignores fields marked as [Ignored].
 */
@Target(AnnotationTarget.CLASS)
annotation class ResourceClass

/**
 * Apply this annotation to fields on classes marked as [ResourceClass] in order
 * to have it ignored by the annotation.
 */
@Target(AnnotationTarget.FIELD)
annotation class Ignored

object ItemBlockClassAnnotationsHandler {
    init {
        AnnotationHelper.findAnnotatedClasses(LibrarianLib.PROXY.asmDataTable, Any::class.java, ResourceClass::class.java) {
            clazz, info ->
            val ownedId = OwnershipHandler.getModId(clazz)
            modIdOverride = ownedId
            clazz.declaredFields.filter { it.annotations.none { it is Ignored } && it.name != "Companion" && it.name != "INSTANCE" }.forEach {
                try {
                    it.set(clazz.kotlin.objectInstance, it.type.newInstance()) //if the kotlin object instance is null, it will be all null, which means it's static :)
                } catch(e: IllegalAccessException) {
                    throw RuntimeException("Exception in @ResourceClass! Private field: " + it.toString(), e)
                }
                Class.forName(it.type.name)
            }
            modIdOverride = null
        }
    }
}

