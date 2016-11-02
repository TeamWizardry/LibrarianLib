package com.teamwizardry.librarianlib.test.bitstorage.block

import com.teamwizardry.librarianlib.common.util.FakeList
import com.teamwizardry.librarianlib.common.util.FakeMap
import com.teamwizardry.librarianlib.common.util.bitsaving.*
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class TileIntSaveTest : TileEntity(), BitStorageContainer {
    override val S = BitwiseStorageManager.createStorage(this, STORAGE_LOC)

    var arrayIndex by S.getProp<Int>("arrayIndex")
    val array by S.getProp<FakeList<Int>>("array")
    val sides by S.getProp<FakeMap<EnumFacing, Int>>("sides")

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        return S.writeToNBT(super.writeToNBT(compound))
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        S.readFromNBT(compound)
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return S.writeToNBT(super.getUpdateTag())
    }

    companion object {
        val STORAGE_LOC = ResourceLocation(TestMod.MODID, "intTest")

        init {
            BitwiseStorageManager.createAllocator(STORAGE_LOC).
                    createProp("arrayIndex", IntProp(2)).
                    createProp("array", ArrayProp(4, { IntProp(3, true) })).
                    createProp("sides", MapProp(EnumFacing.values(), { IntProp(3, true) }))
        }
    }
}
