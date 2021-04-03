package com.teamwizardry.librarianlib.facade.testmod.containers.base

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.testcore.objects.TestTileEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.util.INBTSerializable

class TestContainerTile(tileEntityTypeIn: TileEntityType<*>, val containerSet: TestContainerSet) : TestTileEntity(tileEntityTypeIn), ITickableTileEntity {
    @Save
    val data: ContainerDataSet = ContainerDataSet(containerSet)

    fun <T: TestContainerData> getData(dataType: Class<T>): T {
        return data.getData(dataType)
    }

    override fun tick() {
        data.tick()
    }

    class ContainerDataSet(val containerSet: TestContainerSet) :
        INBTSerializable<CompoundNBT> {

        val dataByType: Map<TestContainerSet.Type<*, *>, TestContainerData> = containerSet.createData()
        val dataByClass: Map<Class<*>, TestContainerData> = dataByType.mapKeys { it.key.dataClass }

        fun <T: TestContainerData> getData(dataType: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return dataByClass.getValue(dataType) as T
        }

        override fun serializeNBT(): CompoundNBT {
            val tag = CompoundNBT()
            for ((type, data) in dataByType) {
                tag.put(type.id.toString(), data.serializeNBT())
            }
            return tag
        }

        override fun deserializeNBT(nbt: CompoundNBT) {
            for ((type, data) in dataByType) {
                if (nbt.contains(type.id.toString())) {
                    data.deserializeNBT(nbt.getCompound(type.id.toString()))
                }
            }
        }

        fun tick() {
            for(data in dataByType.values) {
                data.tick()
            }
        }
    }
}
