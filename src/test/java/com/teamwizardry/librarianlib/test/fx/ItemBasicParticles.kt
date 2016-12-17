package com.teamwizardry.librarianlib.test.fx

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemBasicParticles : ItemMod("basic_particle") {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {

        if (worldIn.isRemote) {
            val builder = ParticleBuilder(50)
            builder.enableMotionCalculation()
            builder.enableRandom()
            builder.setMotion(playerIn.lookVec * 0.5)
            builder.setJitter(5, Vec3d(0.1, 0.1, 0.1))

            val loc = ResourceLocation(TestMod.MODID, "particles/glow")
            if (playerIn.isSneaking) {
                builder.setRender(loc)
            } else {
                builder.setRenderNormalLayer(loc)
            }

            ParticleSpawner.spawn(builder, worldIn, InterpLine(playerIn.positionVector, playerIn.getPositionEyes(1f)), 5)
        }

        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }


}
