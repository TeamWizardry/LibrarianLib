package com.teamwizardry.librarianlib.test.fx

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.math.interpolate.float.InterpLinearFloat
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemBasicParticles : ItemMod("basic_particle") {

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {

        if (worldIn.isRemote) {
            val builder = ParticleBuilder(50)
//            builder.enableMotionCalculation()
//            builder.enableRandom()
//            builder.setMotion(playerIn.lookVec * 0.5)
            builder.setJitter(5, vec(0.1, 0.1, 0.1))

            val loc = ResourceLocation("blocks/wool_colored_orange")
            if (playerIn.isSneaking) {
                builder.setRender(loc)
            } else {
                builder.setRenderNormalLayer(loc)
            }
            builder.setRotation(InterpLinearFloat(0f, 3*Math.PI.toFloat()))

            ParticleSpawner.spawn(builder, worldIn, InterpLine(playerIn.positionVector, playerIn.getPositionEyes(1f)), 5)
        }

        return ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand))
    }


}
