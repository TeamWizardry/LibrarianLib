package com.teamwizardry.librarianlib.test.module

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleEnergy
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleFluid
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 6:30 PM on 6/13/17.
 */
class BlockModule : BlockModContainer("module_test", Material.ROCK) {
    override fun createTileEntity(world: World, state: IBlockState) = ModuleTE()
}

@TileRegister
class ModuleTE : TileMod() {
    @Module
    val items = ModuleInventory(27)

    @Module
    val fluid = ModuleFluid(1000)

    @Module
    val energy = ModuleEnergy(10000)
}
