package com.teamwizardry.librarianlib.client.model.multilayer

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.model.ModelsInit
import net.minecraft.client.renderer.block.model.ModelBlock
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.animation.ModelBlockAnimation
import java.io.InputStreamReader

/**
 * Created by TheCodeWarrior
 */
object LibLibModelBlockLoader : ICustomModelLoader {

    private lateinit var manager: IResourceManager

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        val v = modelLocation.resourcePath.endsWith(".liblib", true)
        if (v)
            LibrarianLog.debug("foobar $modelLocation")
        return v
    }

    override fun loadModel(modelLocation: ResourceLocation): IModel {

        var modelPath = modelLocation.resourcePath
        if (modelLocation.resourcePath.startsWith("models/")) {
            modelPath = modelPath.substring("models/".length)
        }
        val armatureLocation = ResourceLocation(modelLocation.resourceDomain, "armatures/$modelPath.json")
        val animation = ModelBlockAnimation.loadVanillaAnimation(manager, armatureLocation)

        val iresource = manager.getResource(ResourceLocation(modelLocation.resourceDomain, modelLocation.resourcePath + ".json"))
        val reader = InputStreamReader(iresource.inputStream, com.google.common.base.Charsets.UTF_8)
        val model = JsonUtils.gsonDeserialize<ModelBlock>(ModelsInit.SERIALIZER, reader, ModelBlock::class.java, false)
        val iModel = LibLibModelWrapper(modelLocation, model, false, animation)
        return iModel
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        this.manager = resourceManager
    }
}
