package com.teamwizardry.librarianlib.features.properties

import com.teamwizardry.librarianlib.features.properties.context.BlockPropertyContext
import com.teamwizardry.librarianlib.features.properties.context.GenericPropertyContext
import com.teamwizardry.librarianlib.features.properties.context.IPropertyContext
import com.teamwizardry.librarianlib.features.properties.context.ItemPropertyContext
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * @author WireSegal
 * Created at 5:28 PM on 11/1/17.
 */
open class ItemProperty : ModProperty() {
    override fun apply(obj: Any, contextConsumer: (IPropertyContext) -> Boolean): Any? {
        if (obj !is Item)
            return null
        return super.apply(obj, contextConsumer)
    }

    open fun applies(stack: ItemStack): Boolean {
        val reg = registrations[stack.item] ?: return false
        return reg.contextConsumer(ItemPropertyContext(stack))
    }
}

open class BlockProperty : ModProperty() {
    override fun apply(obj: Any, contextConsumer: (IPropertyContext) -> Boolean): Any? {
        if (obj !is Block)
            return null
        return super.apply(obj, contextConsumer)
    }

    open fun applies(blockPos: BlockPos, world: IBlockAccess): Boolean {
        val state = world.getBlockState(blockPos)
        val reg = registrations[state.block] ?: return false
        return reg.contextConsumer(BlockPropertyContext(state, blockPos, world))
    }
}

open class GenericProperty<T : Any>(val clazz: Class<T>) : ModProperty() {
    override fun apply(obj: Any, contextConsumer: (IPropertyContext) -> Boolean): Any? {
        if (!clazz.isInstance(obj))
            return null
        return super.apply(obj, contextConsumer)
    }

    open fun applies(inst: T): Boolean {
        val reg = registrations[inst] ?: return false
        return reg.contextConsumer(GenericPropertyContext(inst))
    }
}
