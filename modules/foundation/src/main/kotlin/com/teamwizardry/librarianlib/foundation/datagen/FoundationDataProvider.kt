package com.teamwizardry.librarianlib.foundation.datagen

import net.minecraft.data.DataGenerator
import java.util.TreeMap
import kotlin.Throws
import java.io.IOException
import net.minecraft.data.DirectoryCache
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper
import net.minecraft.item.ItemStack
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import java.lang.IllegalStateException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.block.Block
import net.minecraft.data.IDataProvider
import net.minecraft.item.Item
import net.minecraft.potion.Effect
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Supplier

public abstract class FoundationDataProvider(protected val gen: DataGenerator) : IDataProvider {
    /**
     * Save the given [data] to the relative path [path] within the datagen output directory
     */
    @Throws(IOException::class)
    protected fun save(cache: DirectoryCache, path: String, data: String) {
        val fullPath = gen.outputFolder.resolve(path)
        @Suppress("UnstableApiUsage")
        val hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString()
        if (cache.getPreviousHash(fullPath) != hash || !Files.exists(fullPath)) {
            Files.createDirectories(fullPath.parent)
            Files.newBufferedWriter(fullPath).use { bufferedwriter -> bufferedwriter.write(data) }
        }
        cache.recordHash(fullPath, hash)
    }
}