package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.network.datasync.IDataSerializer
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Rotations
import net.minecraft.util.text.ITextComponent
import kotlin.reflect.KProperty

class TestEntityConfig(val id: String, val name: String): TestConfig() {
    constructor(id: String, name: String, block: TestEntityConfig.() -> Unit): this(id, name) {
        this.block()
    }

    val typeBuilder = EntityType.Builder.create<TestEntity>({ _, world ->
        TestEntity(this, world)
    }, EntityClassification.MISC)
        .setCustomClientFactory { _, world ->
            TestEntity(this, world)
        }
        .size(0.5f, 0.5f)

    val type by lazy { typeBuilder.build(id).setRegistryName(modid, id) }

    val lookLength: Double = 1.0

    internal val entityProperties = mutableListOf<Property<*>>()

    fun <T> parameter(serializer: IDataSerializer<T>): DataParameter<T> {
        return EntityDataManager.createKey(TestEntity::class.java, serializer)
    }

    operator fun <T: Property<*>> T.unaryPlus(): T {
        return this.also { entityProperties.add(it) }
    }

    fun <T: Any> property(serializer: IDataSerializer<T>, defaultValue: T): Property<T> {
        return Property(parameter(serializer), defaultValue)
    }

    inline fun <reified T: Any> property(defaultValue: T): Property<T> {
        val serializer = when(T::class.java) {
            Boolean::class.java, Boolean::class.javaPrimitiveType -> DataSerializers.BOOLEAN
            Byte::class.java, Byte::class.javaPrimitiveType -> DataSerializers.BYTE
            Int::class.java, Int::class.javaPrimitiveType -> DataSerializers.VARINT
            Float::class.java, Float::class.javaPrimitiveType -> DataSerializers.FLOAT
            String::class.java -> DataSerializers.STRING

            ITextComponent::class.java -> DataSerializers.TEXT_COMPONENT
            //ITextComponent::class.java -> DataSerializers.OPTIONAL_TEXT_COMPONENT
            ItemStack::class.java -> DataSerializers.ITEMSTACK
            Rotations::class.java -> DataSerializers.ROTATIONS
            BlockPos::class.java -> DataSerializers.BLOCK_POS
            //ZzZ::class.java -> DataSerializers.OPTIONAL_BLOCK_POS
            Direction::class.java -> DataSerializers.DIRECTION
            //ZzZ::class.java -> DataSerializers.OPTIONAL_UNIQUE_ID
            //ZzZ::class.java -> DataSerializers.OPTIONAL_BLOCK_STATE
            CompoundNBT::class.java -> DataSerializers.COMPOUND_NBT
            //ZzZ::class.java -> DataSerializers.OPTIONAL_VARINT
            //ZzZ::class.java -> DataSerializers.POSE
            else -> error("No known serializer for type ${T::class.java.simpleName}")
        }
        @Suppress("UNCHECKED_CAST")
        return property(serializer as IDataSerializer<T>, defaultValue)
    }

    class Property<T: Any>(val parameter: DataParameter<T>, val defaultValue: T) {
        internal var entity: Entity? by threadLocal()

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val entity = entity!!
            return entity.dataManager[parameter]
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val entity = entity!!
            entity.dataManager[parameter] = value
        }
    }

    internal fun createSpawnerItem(): Item {
        return TestItem(TestItemConfig(this.id + "_entity", this.name + " Entity") {
            server {
                rightClick {
                    val eye = player.getEyePosition(0f)
                    val entity = TestEntity(this@TestEntityConfig, world)
                    entity.posX = eye.x
                    entity.posY = eye.y - entity.eyeHeight
                    entity.posZ = eye.z
                    entity.rotationPitch = player.rotationPitch
                    entity.rotationYaw = player.rotationYaw
//                    entity.system = type
                    world.addEntity(entity)
                }
            }
        })
    }
}