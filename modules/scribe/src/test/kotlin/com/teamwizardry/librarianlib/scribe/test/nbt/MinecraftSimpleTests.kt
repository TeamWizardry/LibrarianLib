package com.teamwizardry.librarianlib.scribe.test.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.core.util.kotlin.NbtBuilder
import com.teamwizardry.librarianlib.scribe.nbt.*
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

internal class MinecraftSimpleTests: NbtPrismTest() {
    @Test
    fun `read+write for Identifier should be symmetrical`()
        = simple<Identifier, IdentifierSerializer>(Identifier("mod:name"), NbtBuilder.string("mod:name"))

    @Test
    fun `read for Identifier with no namespace should read minecraft namespace`()
        = simpleRead<Identifier, IdentifierSerializer>(Identifier("minecraft:name"), NbtBuilder.string("name"))

    @Test
    fun `read+write for Vec3d should be symmetrical`() {
        simple<Vec3d, Vec3dSerializer>(Vec3d(1.0, 2.0, 3.0), NbtBuilder.compound {
            "X" *= double(1.0)
            "Y" *= double(2.0)
            "Z" *= double(3.0)
        })
    }

    @Test
    fun `read+write for Vec2f should be symmetrical`() {
        simple<Vec2f, Vec2fSerializer>(Vec2f(1f, 2f), NbtBuilder.compound {
            "X" *= float(1f)
            "Y" *= float(2f)
        }, { a, b -> a.x == b.x && a.y == b.y })
    }

    @Test
    fun `read+write for BlockPos should be symmetrical`() {
        simple<BlockPos, BlockPosSerializer>(BlockPos(1, 2, 3), NbtBuilder.compound {
            "X" *= int(1)
            "Y" *= int(2)
            "Z" *= int(3)
        })
    }

    @Test
    fun `read+write for ChunkPos should be symmetrical`() {
        simple<ChunkPos, ChunkPosSerializer>(ChunkPos(1, 2), NbtBuilder.compound {
            "X" *= int(1)
            "Z" *= int(2)
        })
    }

    @Test
    fun `read+write for ColumnPos should be symmetrical`() {
        simple<ColumnPos, ColumnPosSerializer>(ColumnPos(1, 2), NbtBuilder.compound {
            "X" *= int(1)
            "Z" *= int(2)
        })
    }

    @Test
    fun `read+write for SectionPos should be symmetrical`() {
        simple<ChunkSectionPos, ChunkSectionPosSerializer>(ChunkSectionPos.from(ChunkPos(1, 3), 2), NbtBuilder.compound {
            "X" *= int(1)
            "Y" *= int(2)
            "Z" *= int(3)
        })
    }

    @Test
    fun `read+write for EulerAngle should be symmetrical`() {
        simple<EulerAngle, EulerAngleSerializer>(EulerAngle(1f, 2f, 3f), NbtBuilder.compound {
            "Pitch" *= float(1)
            "Yaw" *= float(2)
            "Roll" *= float(3)
        })
    }

    @Test
    fun `read+write for Box should be symmetrical`() {
        simple<Box, BoxSerializer>(Box(
            -1.0, -2.0, -3.0, 1.0, 2.0, 3.0
        ), NbtBuilder.compound {
            "MinX" *= double(-1)
            "MinY" *= double(-2)
            "MinZ" *= double(-3)
            "MaxX" *= double(1)
            "MaxY" *= double(2)
            "MaxZ" *= double(3)
        })
    }

    @Test
    fun `read for Box should correct minmax swaps`() {
        simpleRead<Box, BoxSerializer>(Box(
            -1.0, -2.0, -3.0, 1.0, 2.0, 3.0
        ), NbtBuilder.compound {
            "MinX" *= double(1)
            "MinY" *= double(2)
            "MinZ" *= double(3)
            "MaxX" *= double(-1)
            "MaxY" *= double(-2)
            "MaxZ" *= double(-3)
        })
    }

    @Test
    fun `read+write for BlockBox should be symmetrical`() {
        simple<BlockBox, BlockBoxSerializer>(BlockBox(
            -1, -2, -3, 1, 2, 3
        ), NbtBuilder.compound {
            "MinX" *= int(-1)
            "MinY" *= int(-2)
            "MinZ" *= int(-3)
            "MaxX" *= int(1)
            "MaxY" *= int(2)
            "MaxZ" *= int(3)
        }, { a, b ->
            a.minX == b.minX && a.minY == b.minY && a.minZ == b.minZ &&
                a.maxX == b.maxX && a.maxY == b.maxY && a.maxZ == b.maxZ
        })
    }

    @Test
    fun `read+write for MCPair should be symmetrical`() {
        simple<MCPair<String, Int>, MCPairSerializerFactory.MCPairSerializer>(MCPair("test", 10), NbtBuilder.compound {
            "A" *= string("test")
            "B" *= int(10)
        }, { a, b -> a.left == b.left && a.right == b.right })
    }

    @Test
    fun `read+write for MCPair with a null value should exclude that key`() {
        simple<MCPair<String, Int?>, MCPairSerializerFactory.MCPairSerializer>(MCPair<String, Int?>("test", null), NbtBuilder.compound {
            "A" *= string("test")
        }, { a, b -> a.left == b.left && a.right == b.right })
    }

    inline fun<reified T: NbtElement> nbtPassthroughTest(original: T, shouldCopy: Boolean) {
        val serializer = prism[Mirror.reflect<T>()].value
        assertEquals(TagPassthroughSerializerFactory.TagPassthroughSerializer::class.java, serializer.javaClass)

        val serialized = serializer.write(original)
        assertEquals(original, serialized)
        if(shouldCopy)
            assertNotSame(original, serialized)
        else
            assertSame(original, serialized)

        val deserialized = serializer.read(serialized, null)
        assertEquals(original, deserialized)
        if(shouldCopy)
            assertNotSame(serialized, deserialized)
        else
            assertSame(serialized, deserialized)
    }

    @Test
    fun `read+write for NbtCompound should return a copy of the tag`()
        = nbtPassthroughTest(NbtBuilder.compound { "Foo" *= string("bar") }, true)
    @Test
    fun `read+write for NbtList should return a copy of the tag`()
        = nbtPassthroughTest(NbtBuilder.list { +string("foo") }, true)
    @Test
    fun `read+write for NbtLongArray should return a copy of the tag`()
        = nbtPassthroughTest(NbtBuilder.longArray(1, 2, 3), true)
    @Test
    fun `read+write for NbtIntArray should return a copy of the tag`()
        = nbtPassthroughTest(NbtBuilder.intArray(1, 2, 3), true)
    @Test
    fun `read+write for NbtByteArray should return a copy of the tag`()
        = nbtPassthroughTest(NbtBuilder.byteArray(1, 2, 3), true)

    @Test
    fun `read+write for NbtString should return the tag`()
        = nbtPassthroughTest(NbtString.of("foo"), false)
    @Test
    fun `read+write for NbtDouble should return the tag`()
        = nbtPassthroughTest(NbtDouble.of(1.0), false)
    @Test
    fun `read+write for NbtFloat should return the tag`()
        = nbtPassthroughTest(NbtFloat.of(1f), false)
    @Test
    fun `read+write for NbtLong should return the tag`()
        = nbtPassthroughTest(NbtLong.of(1), false)
    @Test
    fun `read+write for NbtInt should return the tag`()
        = nbtPassthroughTest(NbtInt.of(1), false)
    @Test
    fun `read+write for NbtShort should return the tag`()
        = nbtPassthroughTest(NbtShort.of(1), false)
    @Test
    fun `read+write for NbtByte should return the tag`()
        = nbtPassthroughTest(NbtByte.of(1), false)

    @Test
    fun `read for Tag passthrough with the wrong tag type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<NbtByte>()].value.read(NbtString.of("oops!"), null)
        }
    }

    @Test
    fun `read+write for Text should be symmetrical`() {
        simple<Text, TextSerializerFactory.TextSerializer>(
            LiteralText("value"), NbtString.of("""
                {"text":"value"}
            """.trimIndent())
        )
    }

    @Test
    fun `read+write for GameProfile should be symmetrical`() {
        val uuid = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")
        val most = uuid.mostSignificantBits
        val least = uuid.leastSignificantBits

        simple<GameProfile, GameProfileSerializer>(
            GameProfile(uuid, "Notch"),
            NbtBuilder.compound {
                "Id" *= intArray(
                    (most shr 32).toInt(), most.toInt(),
                    (least shr 32).toInt(), least.toInt()
                )
                "Name" *= string("Notch")
            }
        )
    }

    @Test
    fun `read+write for BlockState should be symmetrical`() {
        simple<BlockState, BlockStateSerializer>(
            Blocks.BONE_BLOCK.stateManager.states[0],
            NbtBuilder.compound {
                "Name" *= string("minecraft:bone_block")
                "Properties" *= compound {
                    "axis" *= string("x")
                }
            }
        )
    }

    @Test
    fun `read+write for ItemStack should be symmetrical`() {
        val stack = ItemStack(Items.DIAMOND, 16)
        simple<ItemStack, ItemStackSerializer>(
            stack,
            NbtBuilder.compound {
                "id" *= string("minecraft:diamond")
                "Count" *= byte(16)
            },
            { a, b -> ItemStack.areEqual(a, b) && ItemStack.areEqual(a, b) }
        )
    }

    @Test
    fun `read+write for ItemStack with Tag should be symmetrical`() {
        val stack = ItemStack(Items.DIAMOND, 16)
        stack.tag = NbtBuilder.compound {
            "Custom" *= string("value")
        }
        simple<ItemStack, ItemStackSerializer>(
            stack,
            NbtBuilder.compound {
                "id" *= string("minecraft:diamond")
                "Count" *= byte(16)
                "tag" *= compound {
                    "Custom" *= string("value")
                }
            },
            { a, b -> ItemStack.areEqual(a, b) && ItemStack.areEqual(a, b) }
        )
    }

    /*
    @Test
    fun `read+write for StatusEffectInstance should be symmetrical`() {
        val stack = StatusEffectInstance(StatusEffects.JUMP_BOOST, 20, 2)
        simple<StatusEffectInstance, StatusEffectInstanceSerializer>(
            stack,
            NbtBuilder.compound {
                "Id" *= byte(8)
                "Amplifier" *= byte(2)
                "Duration" *= int(20)
                "Ambient" *= byte(0)
                "ShowParticles" *= byte(1)
                "ShowIcon" *= byte(1)
            }
        )
    }
    */

    @Test
    fun `read+write for EnchantmentLevelEntry should be symmetrical`() {
        val data = EnchantmentLevelEntry(Enchantments.SHARPNESS, 3)
        simple<EnchantmentLevelEntry, EnchantmentLevelEntrySerializer>(
            data,
            NbtBuilder.compound {
                "Enchantment" *= string("minecraft:sharpness")
                "Level" *= int(3)
            },
            { a, b -> a.enchantment == b.enchantment && a.level == b.level }
        )
    }

}