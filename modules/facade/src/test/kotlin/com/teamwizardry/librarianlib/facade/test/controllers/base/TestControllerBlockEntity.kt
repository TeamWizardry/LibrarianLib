package com.teamwizardry.librarianlib.facade.test.controllers.base

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable

class TestControllerBlockEntity(blockEntityType: BlockEntityType<*>, val controllerSet: TestControllerSet) :
    BlockEntity(blockEntityType), BlockEntityClientSerializable, Tickable {

    val data: ContainerDataSet = ContainerDataSet(controllerSet)

    fun <T : TestControllerData> getData(dataType: Class<T>): T {
        return data.getData(dataType)
    }

    override fun tick() {
        data.tick()
        // this is a test block, I don't care if I mark chucks dirty when I don't have to.
        markDirty()
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        data.deserialize(tag.getCompound("test_data"))
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.put("test_data", data.serialize())
        return super.toTag(tag)
    }

    override fun fromClientTag(tag: CompoundTag) {
        data.deserialize(tag.getCompound("test_data"))
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.put("test_data", data.serialize())
        return tag
    }

    class ContainerDataSet(val controllerSet: TestControllerSet) {

        val dataByType: Map<TestControllerSet.Type<*, *>, TestControllerData> = controllerSet.createData()
        val dataByClass: Map<Class<*>, TestControllerData> = dataByType.mapKeys { it.key.dataClass }

        fun <T : TestControllerData> getData(dataType: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return dataByClass.getValue(dataType) as T
        }

        fun serialize(): CompoundTag {
            val tag = CompoundTag()
            for ((type, data) in dataByType) {
                tag.put(type.id.toString(), data.serialize())
            }
            return tag
        }

        fun deserialize(nbt: CompoundTag) {
            for ((type, data) in dataByType) {
                if (nbt.contains(type.id.toString())) {
                    data.deserialize(nbt.getCompound(type.id.toString()))
                }
            }
        }

        fun serializeSync(): CompoundTag {
            val tag = CompoundTag()
            for ((type, data) in dataByType) {
                tag.put(type.id.toString(), data.serializeSync())
            }
            return tag
        }

        fun deserializeSync(nbt: CompoundTag) {
            for ((type, data) in dataByType) {
                if (nbt.contains(type.id.toString())) {
                    data.deserializeSync(nbt.getCompound(type.id.toString()))
                }
            }
        }

        fun tick() {
            for (data in dataByType.values) {
                data.tick()
            }
        }
    }
}
