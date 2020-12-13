package com.teamwizardry.librarianlib.foundation.item

import net.minecraft.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelFile
import net.minecraftforge.client.model.generators.ModelProvider

public open class BaseItem(properties: Properties): Item(properties), IFoundationItem {
    override fun generateItemModel(gen: ItemModelProvider) {
        val name = this.registryName!!.path
        gen.getBuilder(name)
            .parent(ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", gen.modLoc("${ModelProvider.ITEM_FOLDER}/$name"))
    }
}