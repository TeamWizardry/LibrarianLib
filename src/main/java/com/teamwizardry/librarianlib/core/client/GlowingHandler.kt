package com.teamwizardry.librarianlib.core.client

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.base.block.IGlowingBlock
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.config.ConfigProperty
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.features.utilities.client.GlUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.resources.IResource
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionUtils
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
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
    @ConfigProperty("client", "Items that should glow.\n" +
            "Format: (block:)modid:item@meta,tintindex1,tintindex2|disableLighting, with -1 being untinted. You can have as many tintindexes as you want.\n" +
            "If meta is -1, it'll act as a wildcard. If no tint indices are supplied, it'll use any.\n\n" +
            "Resource packs can specify items to glow in a `liblib_glow.cfg` file under any /assets/modid/ folder.\n" +
            "An example of such a file's contents:\n\n" +
            "botania:resource@5\nbotania:resource@14\npsi:cad,1\nbotania:bifrostperm|false\nblock:minecraft:grass,0",
            configId = LibrarianLib.MODID)
    private var glowing = arrayOf("minecraft:glowstone|false",
            "minecraft:glowstone_dust",
            "minecraft:blaze_rod",
            "minecraft:blaze_powder",
            "minecraft:sea_lantern|false",
            "minecraft:prismarine_crystals",
            "minecraft:end_rod|false",
            "minecraft:experience_bottle",
            "quark:blaze_lantern|false")


    @ConfigProperty("client", "Whether to use the custom potion glow handler.",
            configId = LibrarianLib.MODID)
    var potionGlow = true
        private set
        @JvmStatic get

    @ConfigProperty("client", "Whether to make enchantments use the glow handler.",
            configId = LibrarianLib.MODID)
    var enchantmentGlow = true
        private set
        @JvmStatic get

    fun init() {
        if (potionGlow) {
            registerCustomGlowHandler(Items.POTIONITEM, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true })
            registerCustomGlowHandler(Items.SPLASH_POTION, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true })
            registerCustomGlowHandler(Items.LINGERING_POTION, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true })
            registerCustomGlowHandler(Items.TIPPED_ARROW, { stack, model ->
                if (PotionUtils.getEffectsFromStack(stack).isNotEmpty()) IGlowingItem.Helper.wrapperBake(model, false, 0) else null
            }, { _, _ -> true })
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

        for (i in glowing) parseLine(i)

        val resourceManager = Minecraft.getMinecraft().resourceManager
        resourceManager.resourceDomains
                .flatMap {
                    try {
                        resourceManager.getAllResources(ResourceLocation(it, "liblib_glow.cfg"))
                    } catch (e: IOException) {
                        emptyList<IResource>()
                    }
                }
                .map { it.inputStream.reader() }
                .flatMap { it.readLines() }
                .forEach(::parseLine)

        for ((name, map) in names) {
            val item = ForgeRegistries.ITEMS.getValue(ResourceLocation(name)) ?: continue
            val entries = map.entries.toList()
            val indices = entries.associate { it.key.toInt() to (it.value.first.map(String::toInt) to it.value.second) }
            registerReloadableGlowHandler(item, { stack, model ->
                val array = intArrayOf(*(indices[stack.itemDamage]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()),
                        *(indices[-1]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()))
                IGlowingItem.Helper.wrapperBake(model, array.isEmpty() || array.contains(-1), *array)
            }, { stack, _ -> indices[stack.itemDamage]?.second ?: indices[-1]?.second ?: true })
        }

        for ((name, map) in blockNames) {
            val block = ForgeRegistries.BLOCKS.getValue(ResourceLocation(name)) ?: continue
            val entries = map.entries.toList()
            val indices = entries.associate { it.key.toInt() to (it.value.first.map(String::toInt) to it.value.second) }
            registerReloadableGlowHandler(block) { _, quad, state, _ ->
                val array = intArrayOf(*(indices[state.block.getMetaFromState(state)]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()),
                        *(indices[-1]?.first?.toTypedArray()?.toIntArray() ?: intArrayOf()))
                (quad.hasTintIndex() && (array.isEmpty() || quad.tintIndex in array) || array.isEmpty() || array.contains(-1))
            }
        }
    }

    private val renderModel = ClientRunnable.produce {
        MethodHandleHelper.wrapperForMethod(RenderItem::class.java, "renderModel", "func_191961_a", IBakedModel::class.java, ItemStack::class.java)
    }

    private val removableGlows = mutableListOf<IGlowingItem>()
    private val renderSpecialHandlers = mutableMapOf<Item, IGlowingItem>()
    private val removableGlowBlocks = mutableListOf<IGlowingBlock>()
    private val blockRenderSpecialHandlers = mutableMapOf<Block, IGlowingBlock>()

    private fun registerReloadableGlowHandler(block: Block,
                                              modelTransformer: (IBlockAccess, BakedQuad, IBlockState, BlockPos) -> Boolean) {
        val glow = object : IGlowingBlock {
            override fun shouldGlow(world: IBlockAccess, quad: BakedQuad, state: IBlockState, pos: BlockPos): Boolean {
                return modelTransformer(world, quad, state, pos)
            }
        }

        blockRenderSpecialHandlers.put(block, glow)
        removableGlowBlocks.add(glow)
    }

    @JvmStatic
    fun registerCustomGlowHandler(block: Block,
                                  modelTransformer: (IBlockAccess, BakedQuad, IBlockState, BlockPos) -> Boolean) {
        blockRenderSpecialHandlers.put(block, object : IGlowingBlock {
            override fun shouldGlow(world: IBlockAccess, quad: BakedQuad, state: IBlockState, pos: BlockPos): Boolean {
                return modelTransformer(world, quad, state, pos)
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
                    renderModel?.invoke(Minecraft.getMinecraft().renderItem, arrayOf(newModel, stack))
                }
            }
        }
    }

    @JvmStatic
    fun glow(world: IBlockAccess, model: IBakedModel?, state: IBlockState, pos: BlockPos, buf: BufferBuilder) {
        val block = state.block as? IGlowingBlock ?: blockRenderSpecialHandlers[state.block]

        if (block != null) {
            if (state.renderType == EnumBlockRenderType.LIQUID)
                relightFluid(block, world, state, pos, buf)
            else if (model != null && state.renderType == EnumBlockRenderType.MODEL)
                relightModel(block, world, model, state, pos, buf, true)
        }
    }

    private fun relightModel(glow: IGlowingBlock, world: IBlockAccess, model: IBakedModel, state: IBlockState, pos: BlockPos, buffer: BufferBuilder, checkSides: Boolean, rand: Long = MathHelper.getPositionRandom(pos)): Boolean {
        var total = 0

        val brightness = glow.packedGlowCoords(world, state, pos)

        var list = model.getQuads(state, null, rand)

        if (!list.isEmpty()) {
            relightQuads(glow, world, state, pos, brightness, buffer, list, total)
            total += list.size
        }

        for (enumfacing in EnumFacing.values().reversed()) {
            list = model.getQuads(state, enumfacing, rand)

            if (!list.isEmpty() && (!checkSides || state.shouldSideBeRendered(world, pos, enumfacing))) {
                relightQuads(glow, world, state, pos, brightness, buffer, list, total)
                total += list.size
            }
        }

        return total > 0
    }

    private fun relightQuads(glow: IGlowingBlock, world: IBlockAccess, state: IBlockState, pos: BlockPos, brightness: Int, buffer: BufferBuilder, list: List<BakedQuad>, total: Int) {
        val j = list.size
        val format = buffer.vertexFormat
        for (i in (j - 1) downTo 0) {
            val bakedquad = list[i]
            if (glow.shouldGlow(world, bakedquad, state, pos)) {
                val shift = format.getUvOffsetById(1) / 4
                val truePos = (buffer.vertexCount - (total + j - i) * 4) * format.integerSize + shift
                val jShift = format.integerSize
                val buf = buffer.byteBuffer.asIntBuffer()
                buf.put(truePos, brightness)
                buf.put(truePos + jShift, brightness)
                buf.put(truePos + jShift * 2, brightness)
                buf.put(truePos + jShift * 3, brightness)
            }
        }
    }

    private fun relightFluid(glow: IGlowingBlock, world: IBlockAccess, state: IBlockState, pos: BlockPos, buffer: BufferBuilder) {
        val liquid = state.block as BlockLiquid
        var total = 0

        if (state.shouldSideBeRendered(world, pos, EnumFacing.UP)) {
            total++
            if (liquid.shouldRenderSides(world, pos.up()))
                total++
        }

        if (state.shouldSideBeRendered(world, pos, EnumFacing.DOWN))
            total++

        for (face in EnumFacing.HORIZONTALS) if (state.shouldSideBeRendered(world, pos, face)) {
            if (state.material != Material.LAVA) {
                val block = world.getBlockState(pos.offset(face)).block
                if (block != Blocks.GLASS && block != Blocks.STAINED_GLASS)
                    total++
            }
            total++
        }


        val format = buffer.vertexFormat
        val buf = buffer.byteBuffer.asIntBuffer()
        val brightness = glow.packedGlowCoords(world, state, pos)
        for (i in 1..total) {
            val shift = format.getUvOffsetById(1) / 4
            val truePos = (buffer.vertexCount - i * 4) * format.integerSize + shift
            val jShift = format.integerSize
            buf.put(truePos, brightness)
            buf.put(truePos + jShift, brightness)
            buf.put(truePos + jShift * 2, brightness)
            buf.put(truePos + jShift * 3, brightness)
        }
    }
}

