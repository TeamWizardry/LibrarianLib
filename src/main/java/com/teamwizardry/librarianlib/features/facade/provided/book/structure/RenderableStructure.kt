package com.teamwizardry.librarianlib.features.facade.provided.book.structure

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.createCacheArrayAndReset
import com.teamwizardry.librarianlib.features.structure.Structure
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.gen.structure.template.Template
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.collections.set

class RenderableStructure(val name: ResourceLocation, private val access: IBlockAccess?) : Structure(name) {
    var perfectCenter: Vec3d
    @SideOnly(Side.CLIENT)
    private lateinit var blocks: Map<BlockRenderLayer, List<Template.BlockInfo>>
    @SideOnly(Side.CLIENT)
    private lateinit var vboCaches: Map<BlockRenderLayer, IntArray>
    private var builtVbos = false

    init {
        var minX = 0
        var minY = 0
        var minZ = 0
        var maxX = 0
        var maxY = 0
        var maxZ = 0

        if (templateBlocks != null)
            for (info in templateBlocks!!) {
                val pos = info.pos

                minX = Math.min(minX, pos.x)
                minY = Math.min(minY, pos.y)
                minZ = Math.min(minZ, pos.z)

                maxX = Math.max(maxX, pos.x)
                maxY = Math.max(maxY, pos.y)
                maxZ = Math.max(maxZ, pos.z)
            }

        val max = BlockPos(maxX, maxY, maxZ)
        val min = BlockPos(minX, minY, minZ)

        val size = max.subtract(min)

        perfectCenter = vec(
                size.x / 2.0,
                size.y / 2.0,
                size.z / 2.0)
        origin = BlockPos(perfectCenter)
    }

    @SideOnly(Side.CLIENT)
    fun draw() {
        if (!builtVbos) {
            val newBlocks = mutableMapOf<BlockRenderLayer, MutableList<Template.BlockInfo>>()
            val newVbos = mutableMapOf<BlockRenderLayer, IntArray>()
            blocks = newBlocks
            vboCaches = newVbos

            templateBlocks?.let { template ->
                template
                        .filter { it.blockState.material !== Material.AIR }
                        .forEach { newBlocks.getOrPut(it.blockState.block.renderLayer) { mutableListOf() }.add(it) }

                for ((layer, infoSet) in blocks) {
                    val tes = Tessellator.getInstance()
                    val buffer = tes.buffer
                    val dispatcher = Minecraft.getMinecraft().blockRendererDispatcher

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)

                    for (info in infoSet) {
                        val blockAccess = access ?: blockAccess

                        buffer.setTranslation(info.pos.x.toDouble(), info.pos.y.toDouble(), info.pos.z.toDouble())

                        dispatcher.blockModelRenderer.renderModel(blockAccess, dispatcher.getModelForState(info.blockState), info.blockState, BlockPos.ORIGIN, buffer, true)

                        buffer.setTranslation(0.0, 0.0, 0.0)
                    }

                    newVbos[layer] = buffer.createCacheArrayAndReset()
                }

                builtVbos = true
            }
        }

        for ((_, vbo) in vboCaches) {
            val tes = Tessellator.getInstance()
            val buffer = tes.buffer
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
            buffer.addVertexData(vbo)
            tes.draw()
        }
    }
}
