package com.teamwizardry.librarianlib.scribe.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.*
import net.minecraft.util.registry.Registry

internal object IdentifierSerializer: NbtSerializer<Identifier>() {
    override fun deserialize(tag: NbtElement): Identifier {
        return Identifier(tag.expectType<NbtString>("tag").asString())
    }

    override fun serialize(value: Identifier): NbtElement {
        return NbtString.of(value.toString())
    }
}

//region Math stuff

internal object Vec3dSerializer: NbtSerializer<Vec3d>() {
    override fun deserialize(tag: NbtElement): Vec3d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Vec3d(
            tag.expect<AbstractNbtNumber>("X").doubleValue(),
            tag.expect<AbstractNbtNumber>("Y").doubleValue(),
            tag.expect<AbstractNbtNumber>("Z").doubleValue()
        )
    }

    override fun serialize(value: Vec3d): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtDouble.of(value.x))
        tag.put("Y", NbtDouble.of(value.y))
        tag.put("Z", NbtDouble.of(value.z))
        return tag
    }
}

internal object Vec2fSerializer: NbtSerializer<Vec2f>() {
    override fun deserialize(tag: NbtElement): Vec2f {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Vec2f(
            tag.expect<AbstractNbtNumber>("X").floatValue(),
            tag.expect<AbstractNbtNumber>("Y").floatValue()
        )
    }

    override fun serialize(value: Vec2f): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtFloat.of(value.x))
        tag.put("Y", NbtFloat.of(value.y))
        return tag
    }
}

internal object BlockPosSerializer: NbtSerializer<BlockPos>() {
    override fun deserialize(tag: NbtElement): BlockPos {
        return NbtHelper.toBlockPos(tag.expectType("tag"))
    }

    override fun serialize(value: BlockPos): NbtElement {
        return NbtHelper.fromBlockPos(value)
    }
}


internal object ChunkPosSerializer: NbtSerializer<ChunkPos>() {
    override fun deserialize(tag: NbtElement): ChunkPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return ChunkPos(
            tag.expect<AbstractNbtNumber>("X").intValue(),
            tag.expect<AbstractNbtNumber>("Z").intValue()
        )
    }

    override fun serialize(value: ChunkPos): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtInt.of(value.x))
        tag.put("Z", NbtInt.of(value.z))
        return tag
    }
}

internal object ColumnPosSerializer: NbtSerializer<ColumnPos>() {
    override fun deserialize(tag: NbtElement): ColumnPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return ColumnPos(
            tag.expect<AbstractNbtNumber>("X").intValue(),
            tag.expect<AbstractNbtNumber>("Z").intValue()
        )
    }

    override fun serialize(value: ColumnPos): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtInt.of(value.x))
        tag.put("Z", NbtInt.of(value.z))
        return tag
    }
}

internal object ChunkSectionPosSerializer: NbtSerializer<ChunkSectionPos>() {
    override fun deserialize(tag: NbtElement): ChunkSectionPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return ChunkSectionPos.from(
            ChunkPos(
                tag.expect<AbstractNbtNumber>("X").intValue(),
                tag.expect<AbstractNbtNumber>("Z").intValue()
            ),
            tag.expect<AbstractNbtNumber>("Y").intValue()
        )
    }

    override fun serialize(value: ChunkSectionPos): NbtElement {
        val tag = NbtCompound()
        tag.put("X", NbtInt.of(value.x))
        tag.put("Y", NbtInt.of(value.y))
        tag.put("Z", NbtInt.of(value.z))
        return tag
    }
}

// Dimension types seem to be "dynamic registry entries" now, which means you need to get them from a World instance.
//internal object GlobalPosSerializer: NBTSerializer<GlobalPos>() {
//    override fun deserialize(tag: NbtElement): GlobalPos {
//        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
//        val dimensionName = Identifier(tag.expect<NbtString>("Dimension").string)
//        return GlobalPos.of(
//            DimensionType.byName(dimensionName)!!, // `!!` because the dimension type registry has a default value
//            block(
//                tag.expect<AbstractNbtNumber>("X").intValue(),
//                tag.expect<AbstractNbtNumber>("Y").intValue(),
//                tag.expect<AbstractNbtNumber>("Z").intValue()
//            )
//        )
//    }
//
//    override fun serialize(value: GlobalPos): NbtElement {
//        val tag = NbtCompound()
//        tag.put("Dimension", NbtString.of(value.dimension.registryName.toString()))
//        tag.put("X", NbtInt.of(value.pos.x))
//        tag.put("Y", NbtInt.of(value.pos.y))
//        tag.put("Z", NbtInt.of(value.pos.z))
//        return tag
//    }
//}

internal object EulerAngleSerializer: NbtSerializer<EulerAngle>() {
    override fun deserialize(tag: NbtElement): EulerAngle {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return EulerAngle(
            tag.expect<AbstractNbtNumber>("Pitch").floatValue(),
            tag.expect<AbstractNbtNumber>("Yaw").floatValue(),
            tag.expect<AbstractNbtNumber>("Roll").floatValue()
        )
    }

    override fun serialize(value: EulerAngle): NbtElement {
        val tag = NbtCompound()
        tag.put("Pitch", NbtFloat.of(value.pitch))
        tag.put("Yaw", NbtFloat.of(value.yaw))
        tag.put("Roll", NbtFloat.of(value.roll))
        return tag
    }
}

internal object BoxSerializer: NbtSerializer<Box>() {
    override fun deserialize(tag: NbtElement): Box {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return Box(
            tag.expect<AbstractNbtNumber>("MinX").doubleValue(),
            tag.expect<AbstractNbtNumber>("MinY").doubleValue(),
            tag.expect<AbstractNbtNumber>("MinZ").doubleValue(),
            tag.expect<AbstractNbtNumber>("MaxX").doubleValue(),
            tag.expect<AbstractNbtNumber>("MaxY").doubleValue(),
            tag.expect<AbstractNbtNumber>("MaxZ").doubleValue()
        )
    }

    override fun serialize(value: Box): NbtElement {
        val tag = NbtCompound()
        tag.put("MinX", NbtDouble.of(value.minX))
        tag.put("MinY", NbtDouble.of(value.minY))
        tag.put("MinZ", NbtDouble.of(value.minZ))
        tag.put("MaxX", NbtDouble.of(value.maxX))
        tag.put("MaxY", NbtDouble.of(value.maxY))
        tag.put("MaxZ", NbtDouble.of(value.maxZ))
        return tag
    }
}

internal object BlockBoxSerializer: NbtSerializer<BlockBox>() {
    override fun deserialize(tag: NbtElement): BlockBox {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        return BlockBox(
            tag.expect<AbstractNbtNumber>("MinX").intValue(),
            tag.expect<AbstractNbtNumber>("MinY").intValue(),
            tag.expect<AbstractNbtNumber>("MinZ").intValue(),
            tag.expect<AbstractNbtNumber>("MaxX").intValue(),
            tag.expect<AbstractNbtNumber>("MaxY").intValue(),
            tag.expect<AbstractNbtNumber>("MaxZ").intValue()
        )
    }

    override fun serialize(value: BlockBox): NbtElement {
        val tag = NbtCompound()
        tag.put("MinX", NbtInt.of(value.minX))
        tag.put("MinY", NbtInt.of(value.minY))
        tag.put("MinZ", NbtInt.of(value.minZ))
        tag.put("MaxX", NbtInt.of(value.maxX))
        tag.put("MaxY", NbtInt.of(value.maxY))
        tag.put("MaxZ", NbtInt.of(value.maxZ))
        return tag
    }
}

//endregion

public typealias MCPair<A, B> = net.minecraft.util.Pair<A, B>

internal class MCPairSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<MCPair<*, *>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return MCPairSerializer(prism, mirror as ClassMirror)
    }

    class MCPairSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<MCPair<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: NbtElement): MCPair<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
            return MCPair(
                if (tag.contains("A")) firstSerializer.read(tag.expect("A")) else null,
                if (tag.contains("B")) secondSerializer.read(tag.expect("B")) else null
            )
        }

        override fun serialize(value: MCPair<Any?, Any?>): NbtElement {
            val tag = NbtCompound()
            value.left?.also { tag.put("A", firstSerializer.write(it)) }
            value.right?.also { tag.put("B", secondSerializer.write(it)) }
            return tag
        }
    }
}

/*
internal class DefaultedListSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<DefaultedList<*>>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return DefaultedListSerializer(prism, mirror as ClassMirror)
    }

    class DefaultedListSerializer(prism: NbtPrism, type: ClassMirror): NbtSerializer<DefaultedList<Any>>(type) {
        private val listSerializer by prism[
                Mirror.reflectClass(ArrayList::class.java)
                    .withTypeArguments(
                        type.findSuperclass(DefaultedList::class.java)!!.typeParameters[0]
                    )
        ]

        override fun deserialize(tag: NbtElement): DefaultedList<Any> {
            if(existing == null)
                throw DeserializationException("Expected an existing DefaultedList instance")
            val result = listSerializer.read(tag, null) as List<*>
            mixinCast<DefaultedListScribeHooks>(existing).delegate = result
            return existing
        }

        override fun serialize(value: DefaultedList<Any>): NbtElement {
            return listSerializer.write(mixinCast<DefaultedListScribeHooks>(value).delegate)
        }
    }
}
*/

internal class TagPassthroughSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<NbtElement>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return TagPassthroughSerializer(mirror as ClassMirror)
    }

    class TagPassthroughSerializer(type: ClassMirror): NbtSerializer<NbtElement>(type) {
        @Suppress("UNCHECKED_CAST")
        private val nbtClass = type.erasure as Class<NbtElement>

        override fun deserialize(tag: NbtElement): NbtElement {
            return expectType(tag, nbtClass, "tag").copy()
        }

        override fun serialize(value: NbtElement): NbtElement {
            return value.copy()
        }
    }
}

internal class TextSerializerFactory(prism: NbtPrism): NBTSerializerFactory(prism, Mirror.reflect<Text>()) {
    override fun create(mirror: TypeMirror): NbtSerializer<*> {
        return TextSerializer(mirror as ClassMirror)
    }

    class TextSerializer(type: ClassMirror): NbtSerializer<Text>(type) {
        private val componentClass = type.erasure

        override fun deserialize(tag: NbtElement): Text {
            val component = Text.Serializer.fromJson(tag.expectType<NbtString>("tag").asString())
                ?: inconceivable("ITextComponent.Serializer.fromJson doesn't seem to ever return null")
            if(!componentClass.isAssignableFrom(component.javaClass))
                throw DeserializationException("Wrong ITextComponent type. Expected ${componentClass.simpleName}, " +
                    "found ${component.javaClass.simpleName}.")
            return component
        }

        override fun serialize(value: Text): NbtElement {
            return NbtString.of(Text.Serializer.toJson(value))
        }
    }
}

internal object GameProfileSerializer: NbtSerializer<GameProfile>() {
    override fun deserialize(tag: NbtElement): GameProfile {
        return NbtHelper.toGameProfile(tag.expectType("tag"))
            ?: throw DeserializationException("Reading GameProfile") // it only returns null if an error occurs
    }

    override fun serialize(value: GameProfile): NbtElement {
        val tag = NbtCompound()
        NbtHelper.writeGameProfile(tag, value)
        return tag
    }
}

internal object BlockStateSerializer: NbtSerializer<BlockState>() {
    override fun deserialize(tag: NbtElement): BlockState {
        return NbtHelper.toBlockState(tag.expectType("tag"))
    }

    override fun serialize(value: BlockState): NbtElement {
        return NbtHelper.fromBlockState(value)
    }
}

internal object ItemStackSerializer: NbtSerializer<ItemStack>() {
    override fun deserialize(tag: NbtElement): ItemStack {
        return ItemStack.fromNbt(tag.expectType("tag"))
    }

    override fun serialize(value: ItemStack): NbtElement {
        return value.writeNbt(NbtCompound())
    }
}

/*
internal object StatusEffectInstanceSerializer: NbtSerializer<StatusEffectInstance>() {
    override fun deserialize(tag: NbtElement): StatusEffectInstance {
        return StatusEffectInstance.fromNbt(tag.expectType("tag")) // May return null. How should we handle this?
    }

    override fun serialize(value: StatusEffectInstance): NbtElement {
        return value.writeNbt(NbtCompound())
    }
}
 */

internal object EnchantmentLevelEntrySerializer: NbtSerializer<EnchantmentLevelEntry>() {
    override fun deserialize(tag: NbtElement): EnchantmentLevelEntry {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<NbtCompound>("tag")
        val id = Identifier(tag.expect<NbtString>("Enchantment").asString())
        val enchantment = Registry.ENCHANTMENT.get(id)
            ?: throw DeserializationException("Unknown enchantment type $id")
        return EnchantmentLevelEntry(
            enchantment,
            tag.expect<AbstractNbtNumber>("Level").intValue()
        )
    }

    override fun serialize(value: EnchantmentLevelEntry): NbtElement {
        val tag = NbtCompound()
        tag.put("Enchantment", NbtString.of(Registry.ENCHANTMENT.getId(value.enchantment).toString()))
        tag.put("Level", NbtInt.of(value.level))
        return tag
    }
}
