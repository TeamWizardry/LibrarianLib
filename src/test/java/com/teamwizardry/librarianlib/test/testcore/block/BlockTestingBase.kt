package com.teamwizardry.librarianlib.test.testcore.block

import com.teamwizardry.librarianlib.test.testcore.TestMod
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry
import java.util.function.Function

/**
 * Created by TheCodeWarrior
 */
open class BlockTestingBase(material: Material, name: String) : Block(material) {
    init {
        unlocalizedName = name
        setRegistryName(name)

        val itemBlock = ItemBlock(this)
        itemBlock.setRegistryName(name)

        setCreativeTab(TestMod.tab)
        initPreRegister()
        GameRegistry.register(this)
        GameRegistry.register(itemBlock)
    }

    @SuppressWarnings("unchecked")
    constructor(materialIn: Material, name: String, item: Function<Block, ItemBlock>?) : this(materialIn, name) {
        if (item == null) {
            GameRegistry.register(ItemBlock(this).setRegistryName(registryName))
        } else {
            GameRegistry.register(item.apply(this).setRegistryName(registryName))
        }
    }

    fun initPreRegister() {
    }

}
