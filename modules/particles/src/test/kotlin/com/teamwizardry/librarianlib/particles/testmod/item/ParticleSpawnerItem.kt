package com.teamwizardry.librarianlib.particles.testmod.item

import com.teamwizardry.librarianlib.core.util.SidedRunnable
import com.teamwizardry.librarianlib.particles.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.particles.testmod.init.TestItemGroup
import com.teamwizardry.librarianlib.particles.testmod.modid
import com.teamwizardry.librarianlib.particles.testmod.systems.ParticleSystems
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.UseAction
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

class ParticleSpawnerItem(val type: String): Item(
    Properties()
        .group(TestItemGroup)
        .maxStackSize(1)
) {

    init {
        this.registryName = ResourceLocation(modid, "spawn_$type")
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        if(player.world.isRemote)
            SidedRunnable.client {
                ParticleSystems.spawn(type, player)
            }
    }

    override fun onItemRightClick(worldIn: World, playerIn: PlayerEntity, handIn: Hand): ActionResult<ItemStack> {
        if(playerIn.isCrouching) {
            if(!worldIn.isRemote) {
                val eye = playerIn.getEyePosition(0f)
                val spawner = ParticleSpawnerEntity(worldIn)
                spawner.system = type
                spawner.setPosition(eye.x, eye.y - spawner.eyeHeight, eye.z)
                spawner.rotationPitch = playerIn.rotationPitch
                spawner.rotationYaw = playerIn.rotationYaw
                worldIn.addEntity(spawner)
            }
        } else {
            playerIn.activeHand = handIn
        }
        return ActionResult(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn))
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 3600 * 20 // an hour :P
    }
}
