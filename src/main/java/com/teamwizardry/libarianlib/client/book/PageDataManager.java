package com.teamwizardry.libarianlib.client.book;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import com.teamwizardry.libarianlib.common.PathUtils;
import com.teamwizardry.libarianlib.common.data.DataNode;
import com.teamwizardry.libarianlib.common.data.DataParser;

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
            resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", getLang()) + ".json").substring(1)));
            root = DataParser.parse(resource.getInputStream());
        } catch (IOException e) {
            //TODO: add logger
            try {
                resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1)));
                root = DataParser.parse(resource.getInputStream());
            } catch (IOException e2) {
                //TODO: add logger
                e2.printStackTrace();
            }

        }
        return root;
    }

}
