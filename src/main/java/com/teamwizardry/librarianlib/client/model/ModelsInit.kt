package com.teamwizardry.librarianlib.client.model

import com.google.gson.GsonBuilder
import com.teamwizardry.librarianlib.client.model.multilayer.LibLibModelBlockLoader
import com.teamwizardry.librarianlib.client.model.multilayer.data.LibLibBlockPartDeserializer
import com.teamwizardry.librarianlib.client.model.multilayer.data.LibLibBlockPartFaceDeserializer
import net.minecraft.client.renderer.block.model.*
import net.minecraftforge.client.model.ModelLoaderRegistry

/**
 * Created by TheCodeWarrior
 */
object ModelsInit {
    init {
        ModelLoaderRegistry.registerLoader(LibLibModelBlockLoader)
    }

    @Suppress("DEPRECATION")
    val SERIALIZER = GsonBuilder()
            .registerTypeAdapter(BlockPartFace::class.java, LibLibBlockPartFaceDeserializer())
            .registerTypeAdapter(BlockPart::class.java, LibLibBlockPartDeserializer())
            .registerTypeAdapter(ModelBlock::class.java, ModelBlock.Deserializer())
//            .registerTypeAdapter(BlockPart::class.java, createInstance("net.minecraft.client.renderer.block.model.BlockPart${'$'}Deserializer"))
            .registerTypeAdapter(BlockFaceUV::class.java, createInstance("net.minecraft.client.renderer.block.model.BlockFaceUV${'$'}Deserializer"))
            //todo fix deprecation
            .registerTypeAdapter(ItemTransformVec3f::class.java, createInstance("net.minecraft.client.renderer.block.model.ItemTransformVec3f${'$'}Deserializer"))
            .registerTypeAdapter(ItemCameraTransforms::class.java, createInstance("net.minecraft.client.renderer.block.model.ItemCameraTransforms${'$'}Deserializer"))
            .registerTypeAdapter(ItemOverride::class.java, createInstance("net.minecraft.client.renderer.block.model.ItemOverride${'$'}Deserializer"))
            .create()

    fun createInstance(name: String): Any {
        val c = Class.forName(name)
        val constructor = c.getDeclaredConstructor()
        constructor.isAccessible = true
        return constructor.newInstance()
    }

}
