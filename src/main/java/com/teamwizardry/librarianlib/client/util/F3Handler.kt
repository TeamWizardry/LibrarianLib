package com.teamwizardry.librarianlib.client.util

import com.teamwizardry.librarianlib.common.util.toComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard
import java.util.function.Supplier

/**
 * Created by TheCodeWarrior
 */
object F3Handler {
    private val handlers = mutableMapOf<Int, Runnable>()
    private val updateText = mutableMapOf<Int, Supplier<String?>>()
    private val descriptions = mutableMapOf<Int, String>()
    private val reserved = setOf(
            Keyboard.KEY_A, Keyboard.KEY_B, Keyboard.KEY_D, Keyboard.KEY_F, Keyboard.KEY_G,
            Keyboard.KEY_H, Keyboard.KEY_N, Keyboard.KEY_P, /* Q has handler */ Keyboard.KEY_T
    )
    private val keysDown = mutableMapOf<Int, Boolean>()
    private var f3Down = false

    fun addHandler(key: Int, desc: String, update: Supplier<String?>, run: Runnable): Boolean {
        if (key in handlers || key in reserved)
            return false
        handlers.put(key, run)
        keysDown.put(key, false)
        descriptions.put(key, desc)
        updateText.put(key, update)
        return true
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
        addHandler(Keyboard.KEY_Q, "", Supplier { null }, Runnable {
            val guinewchat = Minecraft.getMinecraft().ingameGUI.chatGUI
            handlers.forEach {
                if (it.key == Keyboard.KEY_Q)
                    return@forEach
                val str = "F3 + ${Keyboard.getKeyName(it.key)} = ${descriptions[it.key]}"
                guinewchat.printChatMessage(str.toComponent())
            }
        })
    }

    @SubscribeEvent
    fun key(e: InputEvent.KeyInputEvent) {
        if (Minecraft.getMinecraft().currentScreen != null) return

        val currentF3 = Keyboard.isKeyDown(Keyboard.KEY_F3)
        if (currentF3) {
            keysDown.forEach {
                val currentKey = Keyboard.isKeyDown(it.key)

                if (currentKey && !it.value) {
                    handlers.get(it.key)?.run()
                    val update = updateText[it.key]?.get()
                    if (update != null) {
                        Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessage(
                                "".toComponent().appendSibling("[Debug]: ".toComponent().setStyle(Style().setColor(TextFormatting.YELLOW).setBold(true)))
                                        .appendText(update))
                    }

                    KeyBinding.setKeyBindState(it.key, true)
                    KeyBinding.setKeyBindState(Keyboard.KEY_ESCAPE, true)
                    KeyBinding.onTick(it.key)
                    KeyBinding.onTick(Keyboard.KEY_ESCAPE)
                }

                keysDown.put(it.key, currentKey)
            }
        }
    }
}
