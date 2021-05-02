package com.teamwizardry.librarianlib.testcore.objects
/*
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import net.minecraft.entity.Entity
import net.minecraft.entity.EntitySize
import net.minecraft.entity.Pose
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.util.ActionResultType
import net.minecraft.util.DamageSource
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

public open class TestEntity(public val config: TestEntityConfig, world: World): Entity(config.also { configHolder = it }.type, world) {

    init {
        canUpdate(true)
    }

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    override fun hitByEntity(entity: Entity): Boolean {
        val context = TestEntityConfig.HitContext(this, entity, entity is PlayerEntity)

        config.hit.run(this.world.isRemote, context)
        if (context.kill) {
            this.remove()
            return true
        }
        return false
    }

    override fun onCollideWithPlayer(entityIn: PlayerEntity) {
        super.onCollideWithPlayer(entityIn)
    }

    override fun hasNoGravity(): Boolean {
        return super.hasNoGravity()
    }

    override fun getPickedResult(target: RayTraceResult?): ItemStack {
        return ItemStack(config.spawnerItem)
    }

    override fun isGlowing(): Boolean {
        var heldGlow = false
        clientOnly {
            heldGlow = Client.minecraft.player?.getHeldItem(Hand.MAIN_HAND)?.item == config.spawnerItem ||
                    Client.minecraft.player?.getHeldItem(Hand.OFF_HAND)?.item == config.spawnerItem
        }
        return config.enableGlow || heldGlow || super.isGlowing()
    }

    override fun tick() {
        super.tick()
        config.tick.run(this.world.isRemote, TestEntityConfig.TickContext(this))
    }

    override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
        config.attack.run(this.world.isRemote, TestEntityConfig.AttackContext(this, source, amount))
        return false
    }

    override fun applyEntityCollision(entityIn: Entity) {
        super.applyEntityCollision(entityIn)
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
    }

    override fun applyPlayerInteraction(player: PlayerEntity, vec: Vector3d, hand: Hand): ActionResultType {
        config.rightClick.run(this.world.isRemote, TestEntityConfig.RightClickContext(this, player, hand, vec))
        if (config.rightClick.exists)
            return ActionResultType.SUCCESS
        return super.applyPlayerInteraction(player, vec, hand)
    }

    // miscellaneous boilerplate =======================================================================================

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun readAdditional(compound: CompoundNBT) {
    }

    override fun writeAdditional(compound: CompoundNBT) {
    }

    override fun registerData() {
        configHolder!!.entityProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            it as TestEntityConfig.Property<Any>

            dataManager.register(it.parameter, it.defaultValue)
        }
    }

    private inline fun <T> loadProperties(block: () -> T): T {
        config.entityProperties.forEach {
            it.entity = this
        }
        val value = block()
        config.entityProperties.forEach {
            it.entity = null
        }
        return value
    }

    override fun getEyeHeight(p_213316_1_: Pose, p_213316_2_: EntitySize): Float {
        return 0f
    }

    public val relativeBoundingBox: Box = run {
        val size = this.getSize(Pose.STANDING)
        val width = size.width.toDouble()
        val height = size.height.toDouble()
        Box(
            -width / 2, -height / 2, -width / 2,
            +width / 2, +height / 2, +width / 2
        )
    }

    override fun getBoundingBox(): Box {
        return relativeBoundingBox.offset(this.posX, this.posY, this.posZ)
    }

    private companion object {
        // needed because registerData is called before we can set the config property
        private var configHolder: TestEntityConfig? by threadLocal()
    }
}


 */