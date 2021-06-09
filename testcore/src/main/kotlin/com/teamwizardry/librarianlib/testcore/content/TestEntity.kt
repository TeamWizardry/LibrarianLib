package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.bridge.TestCoreEntityTypes
import com.teamwizardry.librarianlib.testcore.content.impl.TestEntityImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestEntityRenderer
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import com.teamwizardry.librarianlib.testcore.util.TestContext
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

public class TestEntity(manager: TestModContentManager, id: Identifier) : TestConfig(manager, id) {

    public val type: EntityType<TestEntityImpl> by lazy {
        FabricEntityTypeBuilder.create<TestEntityImpl>(SpawnGroup.MISC)
            .entityFactory<TestEntityImpl> { type, world ->
                TestEntityImpl(this, type, world)
            }
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .build()
    }


    public val spawnerItem: TestItem = manager.create(id.path + "_spawner") {
        rightClick.server {
            spawn(player)
        }
    }

    override var name: String
        get() = super.name
        set(value) {
            super.name = value
            spawnerItem.name = value
        }

    override var description: String?
        get() = super.description
        set(value) {
            super.description = value
            spawnerItem.description = value
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
     * @see Entity.handleAttack
     */
    public val hit: SidedAction<HitContext> = SidedAction()

    public data class RightClickContext(
        val target: TestEntityImpl,
        val player: PlayerEntity,
        val hand: Hand,
        val hitPos: Vec3d
    ) : PlayerTestContext(player) {
        val world: World = target.world
        val stack: ItemStack = player.getStackInHand(hand)
    }

    public data class TickContext(val target: TestEntityImpl) : TestContext() {
        val world: World = target.world
    }

    public data class HitContext(val target: TestEntityImpl, val actor: Entity, var kill: Boolean) : TestContext() {
        val world: World = target.world
    }

    public data class AttackContext(val target: TestEntityImpl, val source: DamageSource, var amount: Float) :
        TestContext() {
        val world: World = target.world
    }

    /**
     * Spawns this entity with the same eye position and look vector as the passed player. Only call this on the logical
     * server.
     */
    public fun spawn(player: PlayerEntity) {
        val eye = player.getCameraPosVec(0f)
        val entity = TestEntityImpl(this, type, player.world)
        entity.setPos(eye.x, eye.y - entity.eyeY, eye.z)
        entity.pitch = player.pitch
        entity.yaw = player.yaw
        player.world.spawnEntity(entity)
    }

    override fun registerCommon(resources: TestModResourceManager) {
        Registry.register(Registry.ENTITY_TYPE, this.id, this.type)
        TestCoreEntityTypes.types.add(this.type)
    }

    override fun registerClient(resources: TestModResourceManager) {
        EntityRendererRegistry.INSTANCE.register(this.type) { dispatcher ->
            TestEntityRenderer(dispatcher)
        }

        resources.lang
            .entity(id, name)
            .item(spawnerItem.id, name)
    }
}
