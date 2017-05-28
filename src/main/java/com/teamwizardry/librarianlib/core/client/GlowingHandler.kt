package com.teamwizardry.librarianlib.core.client

import com.teamwizardry.librarianlib.features.base.block.IGlowingBlock
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.config.ConfigPropertyBoolean
import com.teamwizardry.librarianlib.features.config.ConfigPropertyStringArray
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.ExtendedStateWrapper
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.features.utilities.client.GlUtils
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockModelRenderer
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.resources.IResource
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionUtils
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.util.*

/**
 * @author WireSegal
 * Created at 1:55 PM on 4/29/17.
 */
@SideOnly(Side.CLIENT)
object GlowingHandler {

    val parser = "(\\w+:\\w+)\\s*(?:@(-1|\\d+))?\\s*((?:,(?:-1|\\d+))+,?)?\\s*(?:\\|\\s*(false|true))?".toRegex()
    val blockParser = "block:\\s*(\\w+:\\w+)\\s*(?:@(-1|\\d+))?\\s*((?:,(?:-1|\\d+))+,?)?\\s*(?:\\|\\s*(false|true))?".toRegex()

    @JvmStatic
    @ConfigPropertyStringArray("librarianlib", "client", "glowing", "Items that should glow.\n" +
            "Format: (block:)modid:item@meta,tintindex1,tintindex2|disableLighting, with -1 being untinted. You can have as many tintindexes as you want.\n" +
            "If meta is -1, it'll act as a wildcard. If no tint indices are supplied, it'll use any.\n\n" +
            "Resource packs can specify items to glow in a `glow.cfg` file under any /assets/modid/ folder.\n" +
            "An example of such a file's contents:\n\n" +
            "botania:resource@5\nbotania:resource@14\npsi:cad,1\nbotania:bifrostperm|false",
            arrayOf("minecraft:glowstone|false",
                    "minecraft:glowstone_dust",
                    "minecraft:blaze_rod",
                    "minecraft:blaze_powder",
                    "minecraft:sea_lantern|false",
                    "minecraft:prismarine_crystals",
                    "minecraft:end_rod|false",
                    "quark:blaze_lantern|false"))
    private var glowingItems = arrayOf<String>()


    @JvmStatic
    @ConfigPropertyBoolean("librarianlib", "client", "potion_glow", "Whether to use the custom potion glow handler.", true)
    private var potionGlow = false

    @JvmStatic
    @ConfigPropertyBoolean("librarianlib", "client", "enchantment_glow", "Whether to make enchantments use the glow handler.", true)
    var enchantmentGlow = false
        private set

    fun init() {
        if (potionGlow) {
            registerCustomGlowHandler(Items.POTIONITEM, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true})
            registerCustomGlowHandler(Items.SPLASH_POTION, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true})
            registerCustomGlowHandler(Items.LINGERING_POTION, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true})
            registerCustomGlowHandler(Items.TIPPED_ARROW, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true})
        }
        ClientRunnable.registerReloadHandler { onResourceReload() }
    }

    fun onResourceReload() {
        val handlers = HashMap(renderSpecialHandlers)
        for (it in removableGlows)
            for ((k, v) in handlers)
                if (v == it)
                    renderSpecialHandlers.remove(k)

        removableGlows.clear()

        val names = mutableMapOf<String, MutableMap<String, Pair<List<String>, Boolean?>>>()
        val blockNames = mutableMapOf<String, MutableMap<String, Pair<List<String>, Boolean?>>>()

        fun parseLine(line: String) {
            val match = parser.matchEntire(line.trim())
            if (match != null) {
                val name = match.groupValues[1]
                var meta = match.groupValues[2]
                if (meta.isBlank()) meta = "-1"
                val tintIndices = match.groupValues[3].split(",").filterNot(String::isBlank)
                names.getOrPut(name) { mutableMapOf() }.put(meta, tintIndices to (if (match.groupValues[4].isEmpty()) null else match.groupValues[4] != "false"))
            } else {
                val blockMatch = blockParser.matchEntire(line.trim())
                if (blockMatch != null) {
                    val name = blockMatch.groupValues[1]
                    var meta = blockMatch.groupValues[2]
                    if (meta.isBlank()) meta = "-1"
                    val tintIndices = blockMatch.groupValues[3].split(",").filterNot(String::isBlank)
                    blockNames.getOrPut(name) { mutableMapOf() }.put(meta, tintIndices to (if (blockMatch.groupValues[4].isEmpty()) null else blockMatch.groupValues[4] != "false"))
                }
            }
        }

        for (i in glowingItems) parseLine(i)

        val resourceManager = Minecraft.getMinecraft().resourceManager
        resourceManager.resourceDomains
                .flatMap { try {
                    resourceManager.getAllResources(ResourceLocation(it, "liblib_glow.cfg"))
                } catch(e: IOException) {
                    emptyList<IResource>()
                } }
                .map { it.inputStream.reader() }
                .flatMap { it.readLines() }
                .forEach(::parseLine)

        for ((name, map) in names) {
            val item = ForgeRegistries.ITEMS.getValue(ResourceLocation(name)) ?: continue
            val entries = map.entries.toList()
            val indices = entries.associate { it.key.toInt() to (it.value.first.map(String::toInt) to it.value.second) }
            registerReloadableGlowHandler(item, {
                stack, model ->
                val array = intArrayOf(*(indices[stack.itemDamage]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()),
                        *(indices[-1]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()))
                IGlowingItem.Helper.wrapperBake(model, array.isEmpty() || array.contains(-1), *array)
            }, { stack, _ -> indices[stack.itemDamage]?.second ?: indices[-1]?.second ?: true })
        }

        for ((name, map) in blockNames) {
            val block = ForgeRegistries.BLOCKS.getValue(ResourceLocation(name)) ?: continue
            val entries = map.entries.toList()
            val indices = entries.associate { it.key.toInt() to (it.value.first.map(String::toInt) to it.value.second) }
            registerReloadableGlowHandler(block) {
                _, model, state, _ ->
                val array = intArrayOf(*(indices[state.block.getMetaFromState(state)]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()),
                        *(indices[-1]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()))
                IGlowingItem.Helper.wrapperBake(model, array.isEmpty() || array.contains(-1), *array)
            }
        }
    }

    private val renderModel = MethodHandleHelper.wrapperForMethod(RenderItem::class.java, arrayOf("renderModel", "func_175036_a", "a"), IBakedModel::class.java, ItemStack::class.java)

    private val removableGlows = mutableListOf<IGlowingItem>()
    private val renderSpecialHandlers = mutableMapOf<Item, IGlowingItem>()
    private val removableGlowBlocks = mutableListOf<IGlowingBlock>()
    private val blockRenderSpecialHandlers = mutableMapOf<Block, IGlowingBlock>()

    private fun registerReloadableGlowHandler(block: Block,
                                              modelTransformer: (IBlockAccess, IBakedModel, IBlockState, BlockPos) -> IBakedModel?) {
        val glow = object : IGlowingBlock {
            override fun transformToGlow(world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos): IBakedModel? {
                return modelTransformer(world, model, state, pos)
            }
        }
        blockRenderSpecialHandlers.put(block, glow)
        removableGlowBlocks.add(glow)
    }

    @JvmStatic
    fun registerCustomGlowHandler(block: Block,
                                  modelTransformer: (IBlockAccess, IBakedModel, IBlockState, BlockPos) -> IBakedModel?) {
        blockRenderSpecialHandlers.put(block, object : IGlowingBlock {
            override fun transformToGlow(world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos): IBakedModel? {
                return modelTransformer(world, model, state, pos)
            }
        })
    }

    private fun registerReloadableGlowHandler(item: Item,
                                  modelTransformer: (ItemStack, IBakedModel) -> IBakedModel?,
                                  shouldDisableLighting: ((ItemStack, IBakedModel) -> Boolean) = { _, _ -> false }) {
        val glow = object : IGlowingItem {
            override fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel? {
                return modelTransformer(itemStack, model)
            }

            override fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel): Boolean {
                return shouldDisableLighting(itemStack, model)
            }
        }
        renderSpecialHandlers.put(item, glow)
        removableGlows.add(glow)
    }

    @JvmStatic
    @JvmOverloads
    fun registerCustomGlowHandler(item: Item,
                        modelTransformer: (ItemStack, IBakedModel) -> IBakedModel?,
                        shouldDisableLighting: ((ItemStack, IBakedModel) -> Boolean) = { _, _ -> false }) {
        renderSpecialHandlers.put(item, object : IGlowingItem {
            override fun transformToGlow(itemStack: ItemStack, model: IBakedModel): IBakedModel? {
                return modelTransformer(itemStack, model)
            }

            override fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel): Boolean {
                return shouldDisableLighting(itemStack, model)
            }
        })
    }

    @JvmStatic
    fun glow(stack: ItemStack, model: IBakedModel) {
        val item = stack.item as? IGlowingItem ?: renderSpecialHandlers[stack.item]

        if (item != null) {
            val newModel = item.transformToGlow(stack, model)
            if (newModel != null) GlUtils.withLighting(!item.shouldDisableLightingForGlow(stack, model)) {
                val packed = item.packedGlowCoords(stack, model)
                GlUtils.useLightmap(packed) {
                    renderModel(Minecraft.getMinecraft().renderItem, arrayOf(newModel, stack))
                }
            }
        }
    }

    @JvmStatic
    fun glow(blockModelRenderer: BlockModelRenderer, world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos, buf: VertexBuffer) {
        val block = state.block as? IGlowingBlock ?: blockRenderSpecialHandlers[state.block]

        if (block != null) {
            val newModel = block.transformToGlow(world, model, state, pos)
            if (newModel != null) {
                val prev = ForgeModContainer.forgeLightPipelineEnabled
                ForgeModContainer.forgeLightPipelineEnabled = false
                blockModelRenderer.renderModel(world, newModel, object : ExtendedStateWrapper(state, world, pos) {
                    override fun getPackedLightmapCoords(source: IBlockAccess, pos: BlockPos): Int {
                        return block.packedGlowCoords(source, source.getBlockState(pos), pos)
                    }
                }, pos, buf, true)
                ForgeModContainer.forgeLightPipelineEnabled = prev
            }
        }
    }
}
