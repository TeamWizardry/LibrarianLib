package com.teamwizardry.librarianlib.client.gui.book;

import com.teamwizardry.librarianlib.api.util.misc.PathUtils;
import com.teamwizardry.librarianlib.common.network.data.DataNode;
import com.teamwizardry.librarianlib.common.network.data.DataParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class PageDataManager {

    public static String getLang() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    public static DataNode getPageData(String mod, String pagePath) {
        return getData(mod, "documentation/%LANG%/" + pagePath);
    }

    public static DataNode getData(String mod, String resourcePath) {
        IResource resource;
        DataNode root = DataNode.NULL;
        try {
        	// try selected language
            resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", getLang()) + ".json").substring(1)));
            root = DataParser.parse(resource.getInputStream());
        } catch (IOException e) {
            try {
            	// try English if that fails
                resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1)));
                root = DataParser.parse(resource.getInputStream());
            } catch (IOException e2) {
                e2.printStackTrace();
            }

        }
        return root;
    }

}
