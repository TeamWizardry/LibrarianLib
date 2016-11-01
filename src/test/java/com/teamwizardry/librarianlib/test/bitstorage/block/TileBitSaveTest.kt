package com.teamwizardry.librarianlib.test.bitstorage.block

import com.teamwizardry.librarianlib.common.util.FakeList
import com.teamwizardry.librarianlib.common.util.FakeMap
import com.teamwizardry.librarianlib.common.util.bitsaving.*
import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class TileBitSaveTest : TileEntity(), IBitStorageContainer {
    override val S: BitStorage = BitwiseStorageManager.createStorage(this, STORAGE_LOC)

    val arrayIndex: Int by S.getProp("intVal_arrayIndex")
    val arrayValues: FakeList<Int> by S.getProp("intArr_arrayValues")
    val sideValues: FakeMap<EnumFacing, Int> by S.getProp("intMap_sideValues")

    val hitVal: Float by S.getProp("floatVal_hitVal")
    val hitPos: FakeList<Float> by S.getProp("floatArr_hitPos")
    val sideClickerYPos: FakeMap<EnumFacing, Float> by S.getProp("floatMap_sideClickerYpos")

    val spawnParticles: Boolean by S.getProp("boolVal_spawnParticles")
    val randomValues: FakeList<Boolean> by S.getProp("boolArr_randomValues")
    val sideIsOn: FakeMap<EnumFacing, Boolean> by S.getProp("boolMap_sideIsOn")

    val particleType: EnumParticleTypes by S.getProp("selVal_particleType")
    val sidesHit: FakeList<EnumFacing> by S.getProp("selArr_sidesHit")
    val particlesWhenHit: FakeMap<EnumFacing, EnumParticleTypes> by S.getProp("selMap_particlesWhenHit")

    companion object {
        val STORAGE_LOC = ResourceLocation(TestMod.MODID, "bitsave0")
        val particleTypes = arrayOf(
                EnumParticleTypes.DRIP_LAVA, EnumParticleTypes.DRIP_WATER,
                EnumParticleTypes.CRIT, EnumParticleTypes.CRIT_MAGIC,
                EnumParticleTypes.END_ROD, EnumParticleTypes.FIREWORKS_SPARK,
                EnumParticleTypes.FLAME, EnumParticleTypes.HEART
        )

        init {
            BitwiseStorageManager.createAllocator(STORAGE_LOC).

                    createProp("intVal_arrayIndex", IntProp(2)).
                    createProp("intArr_arrayValues", IntArrayProp(3, 4, true)).
                    createProp("intMap_sideValues", IntMapProp(3, EnumFacing.values())).

                    createProp("floatVal_hitVal", FloatProp(8, 1)).
                    createProp("floatArr_hitPos", FloatArrayProp(1, 4, 3)).
                    createProp("floatMap_sideClickerYpos", FloatMapProp(8, 8, EnumFacing.values(), true)).

                    createProp("boolVal_spawnParticles", BoolProp()).
                    createProp("boolArr_randomValues", BoolArrayProp(3)).
                    createProp("boolMap_sideIsOn", BoolMapProp(EnumFacing.values())).

                    createProp("selVal_particleType", SelectionProp(particleTypes)).
                    createProp("selArr_sidesHit", SelectionArrayProp(EnumFacing.values(), 4)).
                    createProp("selMap_particlesWhenHit", SelectionMapProp(EnumFacing.values(), particleTypes))
        }
    }
}
