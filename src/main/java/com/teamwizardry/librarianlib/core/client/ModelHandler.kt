package com.teamwizardry.librarianlib.core.client

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.base.IExtraVariantHolder
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.IVariantHolder
import com.teamwizardry.librarianlib.features.base.block.IBlockColorProvider
import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.base.item.ISpecialModelProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.kotlin.serialize
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.*
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IRegistryDelegate
import java.io.File
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * @author WireSegal
 * Created at 2:12 PM on 3/20/16.
 */
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
object ModelHandler {

    fun serialize(el: JsonElement)
            = if (LibLibConfig.prettyJsonSerialization) el.serialize() else el.toString() + "\n"

    // Easy access
    private val debug = LibrarianLib.DEV_ENVIRONMENT
    private var modName = ""
    val namePad: String
        get() = " " * modName.length


    private val variantCache = HashMap<String, MutableList<IVariantHolder>>()

    /**
     * This is Mod name -> (Variant name -> MRL), specifically for ItemMeshDefinitions.
     */
    val resourceLocations = mutableMapOf<String, MutableMap<String, ModelResourceLocation>>()


    /**
     * Use this method to inject your item into the list to be loaded at the end of preinit and colorized at the end of init.
     */
    @JvmStatic
    fun registerVariantHolder(holder: IVariantHolder) {
        val name = Loader.instance().activeModContainer()?.modId ?: return
        variantCache.getOrPut(name) { mutableListOf() }.add(holder)
    }

    fun getResource(modId: String, name: String) = resourceLocations[modId]?.get(name)

    @SideOnly(Side.CLIENT)
    private fun addToCachedLocations(name: String, mrl: ModelResourceLocation)
            = resourceLocations.getOrPut(modName) { hashMapOf() }.put(name, mrl)

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun preInit(e: ModelRegistryEvent) {
        for ((modid, holders) in variantCache) {
            modName = modid
            log("$modName | Registering models")
            for (holder in holders.sortedBy { (255 - it.variants.size).toChar() + if (it is IModBlockProvider) "b" else "I" + if (it is IModItemProvider) it.providedItem.key.path else "" })
                registerModels(holder)
        }
    }

    @SideOnly(Side.CLIENT)
    fun init() {
        val itemColors = Minecraft.getMinecraft().itemColors
        val blockColors = Minecraft.getMinecraft().blockColors
        for ((modid, holders) in variantCache) {
            modName = modid
            var flag = false
            for (holder in holders) {
                if (holder is IItemColorProvider && holder is IModItemProvider) {
                    val color = holder.itemColorFunction
                    if (color != null) {
                        if (!flag) {
                            log("$modName | Registering colors")
                            flag = true
                        }
                        log("$namePad | Registering item color for ${holder.providedItem.key.path}")
                        itemColors.registerItemColorHandler(IItemColor(color), holder.providedItem)
                    }
                }

                if (holder is IModBlockProvider && holder is IBlockColorProvider) {
                    val color = holder.blockColorFunction
                    if (color != null) {
                        if (!flag) {
                            log("$modName | Registering colors")
                            flag = true
                        }
                        log("$namePad | Registering block color for ${holder.providedBlock.key.path}")
                        blockColors.registerBlockColorHandler(IBlockColor(color), holder.providedBlock)
                    }
                }

            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerModels(holder: IVariantHolder) {
        if (holder is IExtraVariantHolder)
            registerModels(holder, holder.extraVariants, true)

        if (holder is IModBlockProvider) {
            val mapper = holder.stateMapper
            if (mapper != null)
                ModelLoader.setCustomStateMapper(holder.providedBlock, mapper)

            if (shouldGenerateAnyJson()) generateBlockJson(holder, mapper)
        }

        if (holder is IModItemProvider) {
            val meshDef = holder.meshDefinition

            if (meshDef != null) {
                ModelLoader.setCustomMeshDefinition(holder.providedItem, ItemMeshDefinition(meshDef))
                return
            }
        }

        registerModels(holder, holder.variants, false)

    }

    @SideOnly(Side.CLIENT)
    fun registerModels(holder: IVariantHolder, variants: Array<out String>, extra: Boolean) {
        if (holder is IModItemProvider) {
            val item = holder.providedItem
            for ((index, variantName) in variants.withIndex()) {
                val variant = VariantHelper.toSnakeCase(variantName)

                if (index == 0) {
                    var print = "$namePad | Registering "

                    if (variant != item.key.path || variants.size != 1 || extra)
                        print += "${if (extra) "extra " else ""}variant${if (variants.size == 1) "" else "s"} of "

                    print += if (item is IModBlockProvider) "block" else "item"
                    print += " ${item.key.path}"
                    log(print)
                }

                if (holder is ISpecialModelProvider && holder.getSpecialModel(index) != null) {
                    log("$namePad |  Variant #${index + 1}: $variant - SPECIAL")
                    continue
                }

                if ((variant != item.key.path || variants.size != 1))
                    log("$namePad |  Variant #${index + 1}: $variant")

                if (shouldGenItemJson(holder)) generateItemJson(holder, variant)

                val model = ModelResourceLocation(if (variant.contains(':')) variant else "$modName:$variant", "inventory")
                if (!extra) {
                    ModelLoader.setCustomModelResourceLocation(item, index, model)
                    addToCachedLocations(getKey(item, index), model)
                } else {
                    ModelBakery.registerItemVariants(item, model)
                    addToCachedLocations(variantName, model)
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @JvmStatic
    fun onModelBake(e: ModelBakeEvent) {
        val customModelGetter = MethodHandleHelper.wrapperForStaticGetter(ModelLoader::class.java, "customModels")
        @Suppress("UNCHECKED_CAST")
        val customModels = customModelGetter.invoke() as MutableMap<Pair<IRegistryDelegate<Item>, Int>, ModelResourceLocation>

        for ((modid, holders) in variantCache) {
            modName = modid
            var hasRegisteredAny = false
            for (holder in holders) if (holder is ISpecialModelProvider) {
                val item = holder.providedItem
                var flag = false
                for ((index, variant) in holder.variants.withIndex()) {
                    val model = holder.getSpecialModel(index)
                    if (model != null) {
                        if (!hasRegisteredAny) {
                            log("$modName | Registering special models")
                            hasRegisteredAny = true
                        }
                        if (!flag) {
                            var print = "$namePad | Applying special model rules for "
                            print += if (item is IModBlockProvider) "block " else "item "
                            print += item.key.path
                            log(print)
                            flag = true
                        }
                        val mrl = ModelResourceLocation(ResourceLocation(modName, variant).toString(), "inventory")
                        log("$namePad | Special model for variant $index - $variant applied")
                        e.modelRegistry.putObject(mrl, model)
                        customModels[item.delegate to index] = mrl
                        addToCachedLocations(variant, mrl)
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private fun shouldGenItemJson(provider: IVariantHolder): Boolean {
        if (!shouldGenerateAnyJson()) return false

        if (provider !is IModBlockProvider && provider !is IModItemProvider) return false

        val entry = (provider as? IModBlockProvider)?.providedBlock ?: (provider as IModItemProvider).providedItem

        val statePath = getPathForBaseBlockstate(entry)

        val file = File(statePath)
        if (!file.exists()) return true

        val json: JsonElement
        try {
            json = JsonParser().parse(file.reader())
        } catch (t: Throwable) {
            return true
        }

        var isForge = false
        if (json.isJsonObject && json.asJsonObject.has("forge_marker")) {
            val marker = json.asJsonObject["forge_marker"]
            if (marker.isJsonPrimitive && marker.asJsonPrimitive.isNumber)
                isForge = marker.asInt != 0
        }
        if (isForge)
            log("$namePad | Assuming forge override for ${entry.getRegistryName()!!.path} item model")
        return !isForge
    }

    @SideOnly(Side.CLIENT)
    fun generateItemJson(holder: IModItemProvider, variant: String) {
        if (holder is IModelGenerator && holder.generateMissingItem(holder, variant)) return

        generateItemJson(holder) {
            getPathForItemModel(this, variant) to
                    generateBaseItemModel(this, variant)
        }
    }

    @SideOnly(Side.CLIENT)
    inline fun generateItemJson(holder: IModItemProvider, modelFiles: FileDsl<Item>.() -> Unit) {
        val files = FileDsl(holder.providedItem).apply(modelFiles).map
        for ((path, model) in files) {
            val file = File(path)
            file.parentFile.mkdirs()
            if (file.createNewFile()) {
                file.writeText(serialize(model))
                log("$namePad | Creating ${file.name} for item model of ${getNameForItemProvider(holder)}")
                generatedFiles.add(path)
            }
        }
    }

    @SideOnly(Side.CLIENT)
    fun shouldGenerateAnyJson() = debug && LibLibConfig.generateJson && modName in OwnershipHandler.DEV_OWNED

    @SideOnly(Side.CLIENT)
    fun generateBlockJson(holder: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?) {
        if (holder is IModelGenerator && holder.generateMissingBlockstate(holder, mapper)) return

        generateBlockJson(holder, {
            generateBaseBlockStates(this, mapper)
        }, {
            getPathForBlockModel(this) to
                    generateBaseBlockModel(this)
        })
    }

    @SideOnly(Side.CLIENT)
    inline fun generateBlockJson(holder: IModBlockProvider,
                                 blockstateFiles: FileDsl<Block>.() -> Unit,
                                 modelFiles: FileDsl<Block>.() -> Unit) {
        val files = FileDsl(holder.providedBlock).apply(blockstateFiles).map
        var flag = false
        for ((path, model) in files) {
            if (model !is JsonObject) return
            val stateFile = File(path)
            stateFile.parentFile.mkdirs()

            val stateJson = try {
                JsonParser().parse(stateFile.reader()).asJsonObject
            } catch (ignored: Throwable) {
                flag = true
                JsonObject()
            }

            if (stateJson.size() == 0 || !(stateJson.has("multipart") || stateJson.has("forge_marker") || model.has("multipart") || model.has("forge_marker"))) {
                val variants = if (stateJson.has("variants")) stateJson.get("variants").asJsonObject else null
                val varsInFile = variants?.entrySet()?.map { it.key } ?: listOf()

                val newVariants = if (model.has("variants")) model.get("variants").asJsonObject else null
                val newVarsInFile = newVariants?.entrySet()?.map { it.key } ?: listOf()

                if (newVariants == null || variants == null || newVarsInFile.any { it !in varsInFile }) {
                    if (newVariants != null && variants != null)
                        varsInFile
                            .filter { it in newVarsInFile }
                            .forEach { model.setObject("variants.$it", variants.get(it)) }

                    stateFile.writeText(serialize(model))
                    ModelHandler.log("$namePad | ${if (flag) "Creating" else "Updating"} ${stateFile.name} for blockstate of block ${holder.providedBlock.key.path}")
                    generatedFiles.add(path)
                    flag = true
                }
            }
        }

        if (flag) {
            val models = FileDsl(holder.providedBlock).apply(modelFiles).map
            for ((path, model) in models) {
                val modelFile = File(path)
                modelFile.parentFile.mkdirs()
                if (modelFile.createNewFile()) {
                    modelFile.writeText(serialize(model))
                    ModelHandler.log("$namePad | Creating ${modelFile.name} for block model of block ${holder.providedBlock.key.path}")
                    generatedFiles.add(path)
                }
            }
        }
    }

    fun getNameForItemProvider(provider: IModItemProvider): String {
        val item = provider.providedItem
        return (if (item is ItemBlock) "block " else "item ") + item.key.path
    }

    @JvmStatic
    fun getKey(item: Item, meta: Int): String {
        return "i_" + item.registryName + "@" + meta
    }

    fun log(text: String) {
        if (debug) LibrarianLog.info(text)
    }
}
