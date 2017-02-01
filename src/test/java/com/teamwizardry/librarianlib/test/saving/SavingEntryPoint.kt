package com.teamwizardry.librarianlib.test.saving

import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object SavingEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        SavingBlockRegister
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object SavingBlockRegister {
    val primitives = BlockPrimitivesSaving()
    val primitiveArrays = BlockPrimitiveArraysSaving()
    val objects = BlockObjectsSaving()
    val objectArrays = BlockObjectArraysSaving()
    val primitiveDeepArrays = BlockPrimitiveDeepArraysSaving()
    val primitiveGenerics = BlockPrimitiveGenericsSaving()
    val primitiveSavable = BlockSavablesSaving()
    val extended = BlockExtendedSaving()
}
