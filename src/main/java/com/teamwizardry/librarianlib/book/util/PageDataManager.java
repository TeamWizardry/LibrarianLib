package com.teamwizardry.librarianlib.book.util;

import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.librarianlib.util.PathUtils;
import com.teamwizardry.librarianlib.data.DataNode;
import com.teamwizardry.librarianlib.data.DataParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.FileNotFoundException;
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
        	IOException ex = e;
	        if(!getLang().equals("en_US")) {
	            try {
	            	// try English if that fails
	                resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1)));
	                root = DataParser.parse(resource.getInputStream());
	            } catch (IOException e2) {
	                ex = e2;
	            }
	        }
	        if(ex instanceof FileNotFoundException) {
            	LibrarianLog.I.warn("File not found: %s:%s", mod, PathUtils.resolve(resourcePath.replace("%LANG%", getLang()) + ".json").substring(1));
            	LibrarianLog.I.warn("File not found: %s:%s", mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1));
            } else {
            	ex.printStackTrace();
            }
        }
        return root;
    }

}
