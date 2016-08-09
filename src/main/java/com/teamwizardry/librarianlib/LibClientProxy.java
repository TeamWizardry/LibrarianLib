package com.teamwizardry.librarianlib;

import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.fx.shader.LibShaders;
import com.teamwizardry.librarianlib.fx.shader.ShaderHelper;
import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.sprite.SpritesMetadataSection;
import com.teamwizardry.librarianlib.sprite.SpritesMetadataSectionSerializer;
import com.teamwizardry.librarianlib.sprite.Texture;
import com.teamwizardry.librarianlib.util.ScissorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Prefixed with Lib so code suggestion in dependent projects doesn't suggest it
 */
public class LibClientProxy extends LibCommonProxy implements IResourceManagerReloadListener {

	@Override
	public void preInit() {
		super.preInit();
		LibrarianLib.guide = new Book(LibrarianLib.MODID);
		
		MinecraftForge.EVENT_BUS.register(new GuiTickHandler());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(ScissorUtil.INSTANCE);
		LibShaders.INSTANCE.getClass();// load the class
		ShaderHelper.initShaders();
		try {
			MetadataSerializer s = (MetadataSerializer) ReflectionHelper.findField(Minecraft.class, "metadataSerializer_", "field_110452_an").get(Minecraft.getMinecraft());
			s.registerMetadataSectionType(new SpritesMetadataSectionSerializer(), SpritesMetadataSection.class);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		List<WeakReference<Texture>> newList = new ArrayList<>();

		for (WeakReference<Texture> tex : Texture.textures) {
			if (tex.get() != null) {
				tex.get().loadSpriteData();
				newList.add(tex);
			}
		}

		Texture.textures = newList;
	}
}
