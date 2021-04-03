package com.teamwizardry.librarianlib.testcore.objects

import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
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
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import kotlin.reflect.KProperty

public class TestEntityConfig(public val id: String, public val name: String, spawnerItemGroup: ItemGroup): TestConfig() {
    public constructor(id: String, name: String, spawnerItemGroup: ItemGroup, block: TestEntityConfig.() -> Unit): this(id, name, spawnerItemGroup) {
        this.block()
    }

    override var description: String?
        get() = super.description
        set(value) {
            super.description = value
            spawnerItem.config.description = value
        }

    public var serverFactory: (World) -> TestEntity = { world ->
        TestEntity(this, world)
    }
    public var clientFactory: (World) -> TestEntity = { world ->
        TestEntity(this, world)
    }

    public val typeBuilder: EntityType.Builder<TestEntity> = EntityType.Builder.create<TestEntity>({ _, world ->
        serverFactory(world)
    }, EntityClassification.MISC)
        .setCustomClientFactory { _, world ->
            clientFactory(world)
        }
        .size(0.5f, 0.5f)
    public val type: EntityType<TestEntity> by lazy {
        @Suppress("UNCHECKED_CAST")
        typeBuilder.build(id).setRegistryName(modid, id) as EntityType<TestEntity>
    }

    public val lookLength: Double = 1.0

    /**
     * Designed to be modified at runtime. When true, all entities using this config will have the "glowing" effect
     * applied.
     */
    public var enableGlow: Boolean = false

    /**
     * Called when the player right-clicks this entity
     *
     * @see Entity.applyPlayerInteraction
     */
    public val rightClick: SidedAction<RightClickContext> = SidedAction()

    /**
     * Called every tick
     *
     * @see Entity.tick
     */
    public val tick: SidedAction<TickContext> = SidedAction()

    /**
     * Called when the entity is hit. Set [HitContext.kill] to false if the entity should not be killed.
     *
     * @see Entity.hitByEntity
     */
    public val hit: SidedAction<HitContext> = SidedAction()

    /**
     * Called when the entity is attacked.
     *
     * @see Entity.attackEntityFrom
     */
    public val attack: SidedAction<AttackContext> = SidedAction()

    public data class RightClickContext(val target: TestEntity, val player: PlayerEntity, val hand: Hand, val hitPos: Vector3d): PlayerTestContext(player) {
        val world: World = target.world
        val stack: ItemStack = player.getHeldItem(hand)
    }

    public data class TickContext(val target: TestEntity): TestContext() {
        val world: World = target.world
    }

    public data class HitContext(val target: TestEntity, val actor: Entity, var kill: Boolean): TestContext() {
        val world: World = target.world
    }

    public data class AttackContext(val target: TestEntity, val source: DamageSource, var amount: Float): TestContext() {
        val world: World = target.world
    }

    internal val entityProperties = mutableListOf<Property<*>>()

    public fun <T> parameter(serializer: IDataSerializer<T>): DataParameter<T> {
        return EntityDataManager.createKey(TestEntity::class.java, serializer)
    }

    public operator fun <T: Property<*>> T.unaryPlus(): T {
        return this.also { entityProperties.add(it) }
    }

    public fun <T: Any> property(serializer: IDataSerializer<T>, defaultValue: T): Property<T> {
        return Property(parameter(serializer), defaultValue)
    }

    public inline fun <reified T: Any> property(defaultValue: T): Property<T> {
        val serializer = when (T::class.java) {
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

    public class Property<T: Any>(public val parameter: DataParameter<T>, public val defaultValue: T) {
        internal var entity: Entity? by threadLocal()

        public operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val entity = entity!!
            return entity.dataManager[parameter]
        }

        public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val entity = entity!!
            entity.dataManager[parameter] = value
        }
    }

    /**
     * Spawns this entity with the same eye position and look vector as the passed player. Only call this on the logical
     * server.
     */
    public fun spawn(player: PlayerEntity) {
        val eye = player.getEyePosition(0f)
        val entity = TestEntity(this@TestEntityConfig, player.world)
        entity.setPosition(eye.x, eye.y - entity.eyeHeight, eye.z)
        entity.rotationPitch = player.rotationPitch
        entity.rotationYaw = player.rotationYaw
        player.world.addEntity(entity)
    }

    public var spawnerItem: TestItem = TestItem(TestItemConfig(this.id + "_entity", this.name, spawnerItemGroup) {
        server {
            rightClick {
                spawn(player)
            }
        }
    })
}