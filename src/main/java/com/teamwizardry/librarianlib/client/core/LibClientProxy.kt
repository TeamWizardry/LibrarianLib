package com.teamwizardry.librarianlib.client.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.book.Book
import com.teamwizardry.librarianlib.client.fx.shader.LibShaders
import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSection
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSectionSerializer
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.client.util.ScissorUtil
import com.teamwizardry.librarianlib.common.core.ExampleBookCommand
import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.ref.WeakReference
import java.util.*

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
class LibClientProxy : LibCommonProxy(), IResourceManagerReloadListener {

    override var bookInstance: Book? = null
        private set

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)

        bookInstance = Book(LibrarianLib.MODID)

        if (LibrarianLib.DEV_ENVIRONMENT)
            ClientCommandHandler.instance.registerCommand(ExampleBookCommand())

        ScissorUtil
        LibShaders
        ShaderHelper.initShaders()

        ModelHandler.preInit()

        val s = ReflectionHelper.findField(Minecraft::class.java, "metadataSerializer_", "field_110452_an").get(Minecraft.getMinecraft()) as MetadataSerializer //todo methodhandle
        s.registerMetadataSectionType(SpritesMetadataSectionSerializer(), SpritesMetadataSection::class.java)

        if (Minecraft.getMinecraft().resourceManager is IReloadableResourceManager)
            (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(this)
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        ModelHandler.init()
    }

    override fun translate(s: String, vararg format: Any?): String {
        return I18n.format(s, *format)
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        val newList = ArrayList<WeakReference<Texture>>()

        for (tex in Texture.textures) {
            tex.get()?.loadSpriteData()
            if (tex.get() != null) newList.add(tex)
        }

        Texture.textures = newList
    }
}
