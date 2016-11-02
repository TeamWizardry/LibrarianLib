package com.teamwizardry.librarianlib.test.testcore

import com.teamwizardry.librarianlib.test.bitstorage.block.BlockIntSaveTest
import com.teamwizardry.librarianlib.test.bitstorage.block.TileIntSaveTest
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by TheCodeWarrior
 */
object BlockRegister {

    val intSaveTest = BlockIntSaveTest()

    init {
        GameRegistry.registerTileEntity(TileIntSaveTest::class.java, "intSave")
    }

}
