package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedFunction
import com.teamwizardry.librarianlib.core.util.SidedSupplier
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.network.datasync.IDataSerializer
import net.minecraft.util.DamageSource
import net.minecraft.util.Direction
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Rotations
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import kotlin.reflect.KProperty

class TestEntityConfig(val id: String, val name: String, spawnerItemGroup: ItemGroup): TestConfig() {
    constructor(id: String, name: String, spawnerItemGroup: ItemGroup, block: TestEntityConfig.() -> Unit): this(id, name, spawnerItemGroup) {
        this.block()
    }

    override var description: String?
        get() = super.description
        set(value) {
            super.description = value
            spawnerItem.config.description = value
        }

    var serverFactory: (World) -> TestEntity = { world ->
        TestEntity(this, world)
    }
    var clientFactory: SidedFunction.Client<World, TestEntity> = SidedFunction.Client { world ->
        TestEntity(this, world)
    }

    val typeBuilder = EntityType.Builder.create<TestEntity>({ _, world ->
        serverFactory(world)
    }, EntityClassification.MISC)
        .setCustomClientFactory { _, world ->
            clientFactory.apply(world)
        }
        .size(0.5f, 0.5f)
    val type by lazy {
        @Suppress("UNCHECKED_CAST")
        typeBuilder.build(id).setRegistryName(modid, id) as EntityType<TestEntity>
    }

    val lookLength: Double = 1.0
    /**
     * Designed to be modified at runtime. When true, all entities using this config will have the "glowing" effect
     * applied.
     */
    var enableGlow: Boolean = false

    /**
     * Called when the player right-clicks this entity
     *
     * @see Entity.applyPlayerInteraction
     */
    val rightClick = SidedAction<RightClickContext>()

    /**
     * Called every tick
     *
     * @see Entity.tick
     */
    val tick = SidedAction<TickContext>()

    /**
     * Called when the entity is hit. Set [HitContext.kill] to false if the entity should not be killed.
     *
     * @see Entity.hitByEntity
     */
    val hit = SidedAction<HitContext>()

    /**
     * Called when the entity is attacked.
     *
     * @see Entity.attackEntityFrom
     */
    val attack = SidedAction<AttackContext>()

    data class RightClickContext(val target: TestEntity, val player: PlayerEntity, val hand: Hand, val hitPos: Vec3d): PlayerTestContext(player) {
        val world: World = target.world
        val stack: ItemStack = player.getHeldItem(hand)
    }
    data class TickContext(val target: TestEntity): TestContext() {
        val world: World = target.world
    }
    data class HitContext(val target: TestEntity, val actor: Entity, var kill: Boolean): TestContext() {
        val world: World = target.world
    }
    data class AttackContext(val target: TestEntity, val source: DamageSource, var amount: Float): TestContext() {
        val world: World = target.world
    }


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

    /**
     * Spawns this entity with the same eye position and look vector as the passed player. Only call this on the logical
     * server.
     */
    fun spawn(player: PlayerEntity) {
        val eye = player.getEyePosition(0f)
        val entity = TestEntity(this@TestEntityConfig, player.world)
        entity.setPosition(eye.x, eye.y - entity.eyeHeight, eye.z)
        entity.rotationPitch = player.rotationPitch
        entity.rotationYaw = player.rotationYaw
        player.world.addEntity(entity)
    }

    var spawnerItem = TestItem(TestItemConfig(this.id + "_entity", this.name + " Entity", spawnerItemGroup) {
        server {
            rightClick {
                spawn(player)
            }
        }
    })
}