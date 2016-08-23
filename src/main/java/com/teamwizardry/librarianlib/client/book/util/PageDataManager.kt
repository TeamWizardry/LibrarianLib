package com.teamwizardry.librarianlib.client.book.util

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.bloat.PathUtils
import com.teamwizardry.librarianlib.client.book.data.DataNode
import com.teamwizardry.librarianlib.client.book.data.DataParser
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResource
import net.minecraft.util.ResourceLocation
import java.io.FileNotFoundException
import java.io.IOException

object PageDataManager {

    val lang: String
        get() = Minecraft.getMinecraft().languageManager.currentLanguage.languageCode

    fun getPageData(mod: String, pagePath: String): DataNode {
        return getData(mod, "documentation/%LANG%/" + pagePath)
    }

    fun getData(mod: String, resourcePath: String): DataNode {
        val resource: IResource
        var root = DataNode.NULL
        try {
            // try selected language
            resource = Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", lang) + ".json").substring(1)))
            root = DataParser.parse(resource.inputStream)
        } catch (e: IOException) {
            var ex = e
            if (lang != "en_US") {
                try {
                    // try English if that fails
                    resource = Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation(mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1)))
                    root = DataParser.parse(resource.inputStream)
                } catch (e2: IOException) {
                    ex = e2
                }

            }
            if (ex is FileNotFoundException) {
                LibrarianLog.warn("File not found: %s:%s", mod, PathUtils.resolve(resourcePath.replace("%LANG%", lang) + ".json").substring(1))
                LibrarianLog.warn("File not found: %s:%s", mod, PathUtils.resolve(resourcePath.replace("%LANG%", "en_US") + ".json").substring(1))
            } else {
                ex.printStackTrace()
            }
        }

        return root
    }

}
