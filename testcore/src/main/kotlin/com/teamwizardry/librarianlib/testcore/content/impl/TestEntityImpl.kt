package com.teamwizardry.librarianlib.testcore.content.impl

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import com.teamwizardry.librarianlib.testcore.content.TestEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityPose
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

public open class TestEntityImpl(public val config: TestEntity, type: EntityType<TestEntityImpl>, world: World): Entity(type, world) {

    override fun collides(): Boolean {
        return true
    }

    override fun handleAttack(attacker: Entity): Boolean {
        val context = TestEntity.HitContext(this, attacker, attacker is PlayerEntity)

        config.hit.run(world.isClient, context)
        if (context.kill) {
            this.remove(RemovalReason.KILLED)
            return true
        }
        return false
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        super.onPlayerCollision(player)
    }

    override fun hasNoGravity(): Boolean {
        return super.hasNoGravity()
    }

    override fun isGlowing(): Boolean {
        return config.enableGlow || (world.isClient && isClientHoldingItem()) || super.isGlowing()
    }

    private fun isClientHoldingItem(): Boolean {
        return false
//        return Client.minecraft.player?.getStackInHand(Hand.MAIN_HAND)?.item == config.spawnerItem ||
//                Client.minecraft.player?.getStackInHand(Hand.OFF_HAND)?.item == config.spawnerItem
    }

    override fun tick() {
        super.tick()
        config.tick.run(this.world.isClient, TestEntity.TickContext(this))
    }

    // TODO: forge patch?
//    override fun attackEntityFrom(source: DamageSource, amount: Float): Boolean {
//        config.attack.run(this.world.isClient, TestEntity.AttackContext(this, source, amount))
//        return false
//    }

    override fun interactAt(player: PlayerEntity, hitPos: Vec3d, hand: Hand): ActionResult {
        config.rightClick.run(this.world.isClient, TestEntity.RightClickContext(this, player, hand, hitPos))
        if (config.rightClick.exists)
            return ActionResult.SUCCESS
        return super.interactAt(player, hitPos, hand)
    }

    // miscellaneous boilerplate =======================================================================================

    override fun createSpawnPacket(): Packet<*> {
        return EntitySpawnS2CPacket(this)
    }

    override fun readCustomDataFromNbt(tag: NbtCompound?) {
    }

    override fun writeCustomDataToNbt(tag: NbtCompound?) {
    }

    override fun initDataTracker() {
    }

    override fun getEyeHeight(pose: EntityPose?, dimensions: EntityDimensions?): Float {
        return 0f
    }

    public val relativeBoundingBox: Box
        get() {
            val size = this.getDimensions(EntityPose.STANDING)
            val width = size.width.toDouble()
            val height = size.height.toDouble()
            return Box(
                -width / 2, -height / 2, -width / 2,
                +width / 2, +height / 2, +width / 2
            )
        }

    override fun calculateBoundingBox(): Box {
        return relativeBoundingBox.offset(this.x, this.y, this.z)
    }
}
