package com.teamwizardry.librarianlib.data

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.sprite.Sprite
import com.teamwizardry.librarianlib.sprite.Texture
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

@SideOnly(Side.CLIENT)
object DataNodeParsers {

    val keyGenerator = DefaultStateMapper()

    fun parseBlockState(data: DataNode): IBlockState {
        if (data.isMap && data["id"].isString) {
            val id = data["id"].asStringOr("minecraft:air")
            val block = Block.REGISTRY.getObject(ResourceLocation(id))

            val stateContainer = block.blockState

            if (data["props"].isString) {
                val map = data["props"].asStringOr("default")

                for (state in stateContainer.validStates) if (state.stringForm() == map)
                    return state

                if (map != "default" && map.isNotBlank())
                    LibrarianLog.warn("Block state data props are invalid! ($map)")

                return block.defaultState
            } else if (stateContainer.properties.isNotEmpty())
                LibrarianLog.warn("Block state data doesn't contain props ($id)!")

            return block.defaultState
        }
        return Blocks.AIR.defaultState
    }

    private fun IBlockState.stringForm(): String {
        return keyGenerator.getPropertyString(properties)
    }

    fun parseItem(node: DataNode): Item? {
        if (node.isString)
            return Item.getByNameOrId(node.asString())
        return null
    }

    fun parseStack(node: DataNode): ItemStack? {
        if (node.isString)
            return ItemStack(parseItem(node)!!)
        if (node.isMap) {
            val meta = node.get("meta").asIntOr(0)
            val size = node.get("amount").asIntOr(1)

            return ItemStack(parseItem(node.get("item"))!!, size, meta)
        }
        return null
    }

    private val textures = HashMap<String, Texture>()

    private val spritePattern = Pattern.compile("([^\\s@]+)" + "(?:@([^\\s\\[]+)" + "\\s*" + "(\\[\\s*" + "(\\d+)\\s*,\\s*(\\d+)" + "\\s*\\])?)?")

    fun parseSprite(node: DataNode): Sprite? {
        var rl: String? = null
        var sprite: String? = null
        var w = 16
        var h = 16

        if (node.isString) {
            val m = spritePattern.matcher(node.asString())

            if (!m.find()) {
                rl = node.asString()
            } else {
                rl = m.group(1)
                sprite = m.group(2)
                if (m.group(3) != null) {
                    w = Integer.parseInt(m.group(4))
                    h = Integer.parseInt(m.group(5))
                }
            }
        }
        if (node.isMap) {
            rl = node.get("loc").asString()
            sprite = node.get("sprite").asString()
            w = node.get("width").asIntOr(w)
            h = node.get("height").asIntOr(h)
        }

        if (rl == null)
            return null
        if (sprite == null)
            return Sprite(ResourceLocation(rl))

        var texture: Texture? = textures[rl]
        if (texture == null) {
            texture = Texture(ResourceLocation(rl))
            textures.put(rl, texture)
        }

        return texture.getSprite(sprite, w, h)
    }
}
