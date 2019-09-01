package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.core.util.kotlin.threadLocal
import net.minecraft.entity.Entity
import net.minecraft.entity.EntitySize
import net.minecraft.entity.Pose
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class TestEntity(val config: TestEntityConfig, world: World): Entity(config.also { configHolder = it }.type, world) {

    init {
        canUpdate(true)
    }

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

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    override fun hitByEntity(entity: Entity): Boolean {
        if(entity is PlayerEntity) {
            this.remove()
            return true
        }
        return false
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

    val relativeBoundingBox: AxisAlignedBB = run {
        val size = this.getSize(Pose.STANDING)
        val width = size.width.toDouble()
        val height = size.height.toDouble()
        AxisAlignedBB(
            -width / 2, -height / 2, -width / 2,
            +width / 2, +height / 2, +width / 2
        )
    }

    override fun getBoundingBox(): AxisAlignedBB {
        return relativeBoundingBox.offset(this.posX, this.posY, this.posZ)
    }

    companion object {
        // needed because registerData is called before we can set the config property
        private var configHolder: TestEntityConfig? by threadLocal()
    }
}
