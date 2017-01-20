package com.teamwizardry.librarianlib.client.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.event.ResourceReloadEvent
import com.teamwizardry.librarianlib.client.fx.shader.LibShaders
import com.teamwizardry.librarianlib.client.fx.shader.ShaderHelper
import com.teamwizardry.librarianlib.client.model.ModelsInit
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSection
import com.teamwizardry.librarianlib.client.sprite.SpritesMetadataSectionSerializer
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.client.util.F3Handler
import com.teamwizardry.librarianlib.client.util.ScissorUtil
import com.teamwizardry.librarianlib.common.core.LibCommonProxy
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResourceManager
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.client.resources.data.MetadataSerializer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.io.InputStream

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
@SideOnly(Side.CLIENT)
class LibClientProxy : LibCommonProxy(), IResourceManagerReloadListener {

    override val isClient = true
    override val isDedicatedServer = false

    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)

        F3Handler
        ModelsInit
        UnlistedPropertyDebugViewer
        ScissorUtil
        LibShaders
        ShaderHelper.init()

        val s = MethodHandleHelper.wrapperForGetter(Minecraft::class.java, "metadataSerializer_", "field_110452_an")(Minecraft.getMinecraft()) as MetadataSerializer
        s.registerMetadataSectionType(SpritesMetadataSectionSerializer(), SpritesMetadataSection::class.java)
        SpritesMetadataSection.registered = true

        Texture.register()

        (Minecraft.getMinecraft().resourceManager as IReloadableResourceManager).registerReloadListener(this)
        onResourceManagerReload(Minecraft.getMinecraft().resourceManager)

        if(LibrarianLib.DEV_ENVIRONMENT)
            TextureMapExporter
    }

    override fun latePre(e: FMLPreInitializationEvent) {
        super.latePre(e)
        ModelHandler.preInit()
    }

    override fun lateInit(e: FMLInitializationEvent) {
        super.lateInit(e)
        ModelHandler.init()
    }

    override fun translate(s: String, vararg format: Any?): String {
        return I18n.format(s, *format)
    }

    override fun canTranslate(s: String): Boolean {
        return I18n.hasKey(s)
    }

    override fun getResource(modId: String, path: String): InputStream? {
        val resourceManager = Minecraft.getMinecraft().resourceManager
        try {
            return resourceManager.getResource(ResourceLocation(modId, path)).inputStream
        } catch (e: IOException) {
            return null
        }
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        MinecraftForge.EVENT_BUS.post(ResourceReloadEvent(resourceManager))
    }
}
