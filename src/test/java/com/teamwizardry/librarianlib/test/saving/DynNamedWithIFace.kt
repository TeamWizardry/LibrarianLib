package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.teamwizardry.librarianlib.features.saving.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.util.*

private val logger = LogManager.getLogger("DSI")

object DynNamedWithIFace : ItemMod("dyn_named") {
    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack> {
        val stack = playerIn.getHeldItem(handIn)

        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking) testStuff(getDamage(stack))
            else setDamage(stack, (getDamage(stack) + 1) % 4)
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack)
    }

    private fun testStuff(mode: Int) {
        try {
            when (mode) {
                0 -> {
                    val rng = Random()
                    val vals: List<IA> = when (rng.nextInt(3)) {
                        0 -> EA.values().toList()
                        1 -> EB.values().toList()
                        else -> EC.values().toList()
                    }
                    val element = vals[rng.nextInt(vals.size)]
                    logger.warn("Generated $element (${element.javaClass.name})")
                    val nbt = AbstractSaveHandler.writeAutoNBT(Yolo(element), false)
                    logger.warn("Saved to : $nbt")
                    val read = Yolo(EA.A)
                    AbstractSaveHandler.readAutoNBT(read, nbt, false)
                    logger.warn("Read : $read")
                }
                1 -> {
                    val nbt = AbstractSaveHandler.writeAutoNBT(Yolo2(CB()), false)
                    logger.warn("Saved to : $nbt")
                    val nbt2 = AbstractSaveHandler.writeAutoNBT(Yolo2(CC()), false)
                    logger.warn("Saved 2 to : $nbt2")
                    val read = Yolo2(CB())
                    AbstractSaveHandler.readAutoNBT(read, nbt, false)
                    logger.warn("Read : $read")
                    val read2 = Yolo2(CB())
                    val r = AbstractSaveHandler.readAutoNBT(read2, nbt2, false)
                    logger.warn("Read 2 : $read2 - $r")
                }
                2 -> {
                    val nbt = AbstractSaveHandler.writeAutoNBT(Yolo3(CE()), false)
                    logger.warn("Saved to : $nbt")
                    val nbt2 = AbstractSaveHandler.writeAutoNBT(Yolo3(CF()), false)
                    logger.warn("Saved 2 to : $nbt2")
                    val read = Yolo3(CE())
                    AbstractSaveHandler.readAutoNBT(read, nbt, false)
                    logger.warn("Read : $read")
                    val read2 = Yolo3(CE())
                    val r = AbstractSaveHandler.readAutoNBT(read2, nbt2, false)
                    logger.warn("Read 2 : $read2 - $r")
                }
                3 -> {
                    val nbt0 = AbstractSaveHandler.writeAutoNBT(Yolo5(CI()), false)
                    logger.warn("Saved to : $nbt0") // FIXME: saving this before having saved with interface type causes stackoverflow. Workaround would be to call the savers below first.
                    val nbt = AbstractSaveHandler.writeAutoNBT(Yolo4(CG()), false)
                    logger.warn("Saved to : $nbt")
                    val nbt2 = AbstractSaveHandler.writeAutoNBT(Yolo4(CH()), false)
                    logger.warn("Saved 2 to : $nbt2")
                    val nbt3 = AbstractSaveHandler.writeAutoNBT(Yolo4(CI()), false)
                    logger.warn("Saved 2 to : $nbt3")
                    val read = Yolo4(CG())
                    AbstractSaveHandler.readAutoNBT(read, nbt, false)
                    logger.warn("Read : $read")
                    val read2 = Yolo4(CH())
                    val r = AbstractSaveHandler.readAutoNBT(read2, nbt2, false)
                    logger.warn("Read 2 : $read2 - $r")
                    val read3 = Yolo4(CI())
                    AbstractSaveHandler.readAutoNBT(read3, nbt3, false)
                    logger.warn("Read 3 : $read3")
                }
            }
        } catch (e: Exception) {
            logger.warn("Couldn't save nbt", e)
        }
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return getDamage(stack).toString()
    }
}

@Savable
data class Yolo(@Save var a: IA)

@Savable
data class Yolo2(@Save var a: CA)

@Savable
data class Yolo3(@Save var a: CD)

@Savable
data class Yolo4(@Save var a: ID)

@Savable
data class Yolo5(@Save var a: CI)

@Savable
@NamedDynamic("dsi:ia")
interface IA

@Savable
@NamedDynamic("dsi:ib")
interface IB

@Savable
@NamedDynamic("dsi:ic")
interface IC : IA

@NamedDynamic("dsi:ea")
enum class EA : IA {
    A, B, C
}

@NamedDynamic("dsi:eb")
enum class EB : IA, IB {
    D, E, F
}

@NamedDynamic("dsi:ec")
enum class EC : IA, IC {
    G, H, I
}

@NamedDynamic("dsi:ed")
enum class ED : IB {
    J, K, L
}

@Savable
@NamedDynamic("dsi:ca")
abstract class CA : IA

@Savable
@NamedDynamic("dsi:cb")
class CB : IB, CA()

@Savable
@NamedDynamic("dsi:cc")
class CC : CA()

@Savable
@NamedDynamic("dsi:cd")
open class CD

@Savable
@NamedDynamic("dsi:ce")
class CE : IB, CD()

@Savable
@NamedDynamic("dsi:cf")
class CF : CD()

@Savable
@NamedDynamic("dsi:id")
interface ID

@SaveInPlace
@NamedDynamic("dsi:cg")
class CG : ID

@SaveInPlace
@NamedDynamic("dsi:ch")
class CH : ID

@SaveInPlace
@NamedDynamic("dsi:ci")
class CI : ID
