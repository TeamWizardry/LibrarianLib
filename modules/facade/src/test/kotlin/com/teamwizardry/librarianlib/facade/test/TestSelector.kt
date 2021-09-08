package com.teamwizardry.librarianlib.facade.test

import com.teamwizardry.librarianlib.courier.CourierPacket
import com.teamwizardry.librarianlib.courier.CourierPacketType
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import dev.thecodewarrior.prism.annotation.Refract
import dev.thecodewarrior.prism.annotation.RefractClass
import dev.thecodewarrior.prism.annotation.RefractConstructor
import net.minecraft.block.Block
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import kotlin.math.max

class TestSelector {
    val entries: MutableList<SelectorEntry> = mutableListOf()
    val index: MutableMap<String, SelectorEntry> = mutableMapOf()

    fun add(entry: SelectorEntry) {
        entries.add(entry)
        index[entry.path.toString()] = entry
    }
}

data class SelectorEntry(val path: SelectorPath, val screen: (() -> Screen)?) {
    fun create(): Screen? {
        return screen?.invoke()
    }

    override fun toString(): String {
        return path.toString()
    }
}

data class SelectorPath(val elements: List<String>) {
    val name: String = elements.lastOrNull() ?: ""
    val depth: Int = max(0, elements.size - 1)

    fun append(vararg elements: String): SelectorPath {
        return SelectorPath(this.elements + listOf(*elements))
    }

    override fun toString(): String {
        return elements.joinToString("/")
    }
}

class TestSelectorBuilder private constructor(private val selector: TestSelector, private val path: SelectorPath) {
    constructor() : this(TestSelector(), SelectorPath(emptyList()))

    fun group(name: String, config: TestSelectorBuilder.() -> Unit) {
        val groupPath = path.append("$name/")
        selector.add(SelectorEntry(groupPath, null))
        val builder = TestSelectorBuilder(selector, groupPath)
        builder.config()
    }

    fun screen(name: String, factory: (Text) -> Screen) {
        selector.add(SelectorEntry(path.append(name)) {
            factory(LiteralText(name))
        })
    }

    fun build(): TestSelector {
        return selector
    }
}

@RefractClass
data class SyncSelectionPacket @RefractConstructor constructor(
    @Refract("id") val id: String,
) : CourierPacket {

    companion object {
        val type: CourierPacketType<SyncSelectionPacket> = CourierPacketType(
            Identifier("liblib-facade-test:sync_selection"),
            SyncSelectionPacket::class.java
        )
    }
}

