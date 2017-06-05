package com.teamwizardry.librarianlib.features.base.entity

import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import net.minecraft.entity.*
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityMinecart
import net.minecraft.entity.item.EntityMinecartContainer
import net.minecraft.entity.monster.AbstractSkeleton
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.*
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 5:31 PM on 5/23/17.
 *
 * One exists for each abstract saved-to-world non-player entity class.
 * They're trivial to implement yourself, though.
 */

@SaveInPlace
abstract class EntityMod(world: World) : Entity(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        // NO-OP
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        // NO-OP
    }
}

@SaveInPlace
abstract class LivingEntityMod(world: World) : EntityLiving(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class LivingBaseEntityMod(world: World) : EntityLivingBase(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class AgeableEntityMod(world: World) : EntityAgeable(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class CreatureEntityMod(world: World) : EntityCreature(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class FlyingEntityMod(world: World) : EntityFlying(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class MobEntityMod(world: World) : EntityMob(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class AnimalEntityMod(world: World) : EntityAnimal(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class TameableEntityMod(world: World) : EntityTameable(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class WaterMobEntityMod(world: World) : EntityWaterMob(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class SkeletonEntityMod(world: World) : AbstractSkeleton(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class GolemEntityMod(world: World) : EntityGolem(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class HorseEntityMod(world: World) : EntityHorse(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class ChestHorseEntityMod(world: World) : AbstractChestHorse(world), IModEntity {
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}


@SaveInPlace
abstract class ArrowEntityMod : EntityArrow, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)
    constructor(world: World, shooter: EntityLivingBase) : super(world, shooter)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class MinecartEntityMod : EntityMinecart, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class MinecartContainerEntityMod : EntityMinecartContainer, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class ThrowableEntityMod : EntityThrowable, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)
    constructor(world: World, thrower: EntityLivingBase) : super(world, thrower)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class ItemEntityMod : EntityItem, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(world, x, y, z)
    constructor(world: World, x: Double, y: Double, z: Double, stack: ItemStack) : super(world, x, y, z, stack)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class FireballEntityMod : EntityFireball, IModEntity  {
    constructor(world: World) : super(world)
    constructor(world: World, x: Double, y: Double, z: Double, accelX: Double, accelY: Double, accelZ: Double) : super(world, x, y, z, accelX, accelY, accelZ)
    constructor(world: World, shooter: EntityLivingBase, accelX: Double, accelY: Double, accelZ: Double) : super(world, shooter, accelX, accelY, accelZ)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}

@SaveInPlace
abstract class HangingEntityMod : EntityHanging, IModEntity {
    constructor(world: World) : super(world)
    constructor(world: World, pos: BlockPos) : super(world, pos)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val comp = AbstractSaveHandler.writeAutoNBT(this, false)
        compound.setTag("auto", comp)
        val comp2 = NBTTagCompound().apply { writeCustomNBT(this) }
        compound.setTag("custom", comp2)
        return super.writeToNBT(compound)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("auto"), false)
        readCustomNBT(compound.getCompoundTag("custom"))
        super.readFromNBT(compound)
    }

    override final fun writeEntityToNBT(compound: NBTTagCompound) {
        super.writeEntityToNBT(compound)
    }

    override final fun readEntityFromNBT(compound: NBTTagCompound) {
        super.readEntityFromNBT(compound)
    }
}
