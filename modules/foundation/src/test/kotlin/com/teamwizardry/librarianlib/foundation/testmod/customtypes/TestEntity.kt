package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class TestEntity(entityTypeIn: EntityType<*>, worldIn: World): Entity(entityTypeIn, worldIn) {
    override fun registerData() {
    }

    override fun readAdditional(compound: CompoundNBT) {
    }

    override fun writeAdditional(compound: CompoundNBT) {
    }

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }
}