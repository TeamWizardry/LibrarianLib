package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.kotlin.toComponent
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.input.Keyboard
import java.util.function.Supplier

/**
 * Created by TheCodeWarrior
 */
@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = LibrarianLib.MODID)
object F3Handler {
    private val handlers = mutableMapOf<Int, Runnable>()
    private val updateText = mutableMapOf<Int, Supplier<String?>>()
    private val descriptions = mutableMapOf<Int, String>()
    private val reserved = setOf(
            Keyboard.KEY_A, Keyboard.KEY_B, Keyboard.KEY_D, Keyboard.KEY_F, Keyboard.KEY_G,
            Keyboard.KEY_H, Keyboard.KEY_N, Keyboard.KEY_P, /* Q has handler */ Keyboard.KEY_T
    )
    private val keysDown = mutableMapOf<Int, Boolean>()

    fun addHandler(key: Int, desc: String, update: Supplier<String?>, run: Runnable): Boolean {
        if (key in handlers || key in reserved)
            return false
        handlers[key] = run
        keysDown[key] = false
        descriptions[key] = desc
        updateText[key] = update
        return true
    }

    init {
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

    @JvmStatic
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

                keysDown[it.key] = currentKey
            }
        }
    }
}
