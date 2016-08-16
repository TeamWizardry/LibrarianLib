package com.teamwizardry.librarianlib

import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.fx.shader.LibShaders
import com.teamwizardry.librarianlib.fx.shader.ShaderHelper
import com.teamwizardry.librarianlib.gui.TickCounter
import com.teamwizardry.librarianlib.sprite.SpritesMetadataSection
import com.teamwizardry.librarianlib.sprite.SpritesMetadataSectionSerializer
import com.teamwizardry.librarianlib.sprite.Texture
import com.teamwizardry.librarianlib.util.ScissorUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.relauncher.ReflectionHelper

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
class LibClientProxy : LibCommonProxy(), IResourceManagerReloadListener {

    override fun preInit() {
        super.preInit()
        LibrarianLib.guide = Book(LibrarianLib.MODID)

        if (Const.isDev)
            ClientCommandHandler.instance.registerCommand(ExampleBookCommand())

        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(ScissorUtil.INSTANCE)
        LibShaders.INSTANCE.javaClass// load the class
        ShaderHelper.initShaders()
        try {
            val s = ReflectionHelper.findField(Minecraft::class.java, "metadataSerializer_", "field_110452_an").get(Minecraft.getMinecraft()) as MetadataSerializer
            s.registerMetadataSectionType(SpritesMetadataSectionSerializer(), SpritesMetadataSection::class.java)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        if (Minecraft.getMinecraft().resourceManager is IReloadableResourceManager) {
            (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(this)
        }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        val newList = ArrayList<WeakReference<Texture>>()

        for (tex in Texture.textures) {
            if (tex.get() != null) {
                tex.get()!!.loadSpriteData()
                newList.add(tex)
            }
        }

        Texture.textures = newList
    }
}
