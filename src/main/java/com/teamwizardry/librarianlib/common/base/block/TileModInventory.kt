package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.IInventoryChangedListener
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import java.util.*

/**
 * Created by TheCodeWarrior
 */
open class TileModInventory(val size: Int) : TileMod(), IInventory {
    var inventoryContents = arrayOfNulls<ItemStack?>(size)
        @SaveMethodGetter("inventory")
        get
        @SaveMethodSetter("inventory")
        set(value) {
            if(value.size != size) {
                field = Arrays.copyOf(value, size)
            } else {
                field = value
            }
        }
    var inventoryTitle = "title"
    var hasCustomName = false

    /**
     * Add a listener that will be notified when any item in this inventory is modified.
     */
    fun addInventoryChangeListener(listener: IInventoryChangedListener) {}

    /**
     * removes the specified IInvBasic from receiving further change notices
     */
    fun removeInventoryChangeListener(listener: IInventoryChangedListener) {}

    /**
     * Returns the stack in the given slot.
     */
    override fun getStackInSlot(index: Int): ItemStack? {
        return if (index >= 0 && index < this.inventoryContents.size) this.inventoryContents[index] else null
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    override fun decrStackSize(index: Int, count: Int): ItemStack? {
        val itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count)

        if (itemstack != null) {
            this.markDirty()
        }

        return itemstack
    }

    fun addItem(stack: ItemStack): ItemStack? {
        val itemstack = stack.copy()

        for (i in 0..inventoryContents.size - 1) {
            val itemstack1 = this.getStackInSlot(i)

            if (itemstack1 == null) {
                this.setInventorySlotContents(i, itemstack)
                this.markDirty()
                return null
            }

            if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
                val j = Math.min(this.inventoryStackLimit, itemstack1.maxStackSize)
                val k = Math.min(itemstack.stackSize, j - itemstack1.stackSize)

                if (k > 0) {
                    itemstack1.stackSize += k
                    itemstack.stackSize -= k

                    if (itemstack.stackSize <= 0) {
                        this.markDirty()
                        return null
                    }
                }
            }
        }

        if (itemstack.stackSize != stack.stackSize) {
            this.markDirty()
        }

        return itemstack
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    override fun removeStackFromSlot(index: Int): ItemStack? {
        if (this.inventoryContents[index] != null) {
            val itemstack = this.inventoryContents[index]
            this.inventoryContents[index] = null
            return itemstack
        } else {
            return null
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    override fun setInventorySlotContents(index: Int, stack: ItemStack?) {
        this.inventoryContents[index] = stack

        if (stack != null && stack.stackSize > this.inventoryStackLimit) {
            stack.stackSize = this.inventoryStackLimit
        }

        this.markDirty()
    }

    /**
     * Returns the number of slots in the inventory.
     */
    override fun getSizeInventory(): Int {
        return inventoryContents.size
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    override fun getName(): String {
        return this.inventoryTitle
    }

    /**
     * Returns true if this thing is named
     */
    override fun hasCustomName(): Boolean {
        return this.hasCustomName
    }

    /**
     * Sets the name of this inventory. This is displayed to the client on opening.
     */
    fun setCustomName(inventoryTitleIn: String) {
        this.hasCustomName = true
        this.inventoryTitle = inventoryTitleIn
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    override fun getDisplayName(): ITextComponent {
        return if (this.hasCustomName()) TextComponentString(this.name) else TextComponentTranslation(this.name, *arrayOfNulls<Any>(0))
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    override fun getInventoryStackLimit(): Int {
        return 64
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    override fun markDirty() {}

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return true
    }

    override fun openInventory(player: EntityPlayer) {
    }

    override fun closeInventory(player: EntityPlayer) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return true
    }

    override fun getField(id: Int): Int {
        return 0
    }

    override fun setField(id: Int, value: Int) {
    }

    override fun getFieldCount(): Int {
        return 0
    }

    override fun clear() {
        for (i in this.inventoryContents.indices) {
            this.inventoryContents[i] = null
        }
    }
}
