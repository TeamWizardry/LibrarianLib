package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Texture

object PastryTexture {
    val texture = Texture("librarianlib:textures/gui/pastry/components.png".toRl())

    val background = texture.getSprite("background", 24, 24)
    val textField = texture.getSprite("text_field", 24, 11)
    val button = texture.getSprite("button", 24, 11)
    val buttonPressed = texture.getSprite("button_pressed", 24, 11)
    val optionSpinner = texture.getSprite("option_spinner", 40, 11)
    val scrollbarTrackVertical = texture.getSprite("scrollbar_track_vertical", 7, 16)
    val scrollbarTrackHorizontal = texture.getSprite("scrollbar_track_horizontal", 16, 7)
    val scrollbarHandleVertical = texture.getSprite("scrollbar_handle_vertical", 5, 5)
    val scrollbarHandleHorizontal = texture.getSprite("scrollbar_handle_horizontal", 5, 5)
    val radialSlider = texture.getSprite("radial_slider", 15, 15)
    val radialSliderPointer = texture.getSprite("radial_slider_pointer", 1, 8)
    val checkboxOff = texture.getSprite("checkbox_off", 7, 7)
    val checkboxOn = texture.getSprite("checkbox_on", 7, 7)
    val switchOff = texture.getSprite("switch_off", 11, 7)
    val switchOn = texture.getSprite("switch_on", 11, 7)
    val radioOff = texture.getSprite("radio_off", 7, 7)
    val radioOn = texture.getSprite("radio_on", 7, 7)
    val stepperFrame = texture.getSprite("stepper_frame", 17, 9)
    val stepperPlus = texture.getSprite("stepper_plus", 7, 7)
    val stepperPlusPressed = texture.getSprite("stepper_plus_pressed", 7, 7)
    val stepperMinus = texture.getSprite("stepper_minus", 7, 7)
    val stepperMinusPressed = texture.getSprite("stepper_minus_pressed", 7, 7)
    val sliderHandle = texture.getSprite("slider_handle", 7, 7)
    val sliderHandleDown = texture.getSprite("slider_handle_down", 7, 7)
    val sliderHandleUp = texture.getSprite("slider_handle_up", 7, 7)
    val sliderHandleLeft = texture.getSprite("slider_handle_left", 7, 7)
    val sliderHandleRight = texture.getSprite("slider_handle_right", 7, 7)
    val sliderTickLeft = texture.getSprite("slider_tick_left", 7, 1)
    val sliderTickRight = texture.getSprite("slider_tick_right", 7, 1)
    val sliderTickTop = texture.getSprite("slider_tick_top", 1, 7)
    val sliderTickBottom = texture.getSprite("slider_tick_bottom", 1, 7)
    val sliderTrackHorizontal = texture.getSprite("slider_track_horizontal", 22, 1)
    val sliderTrackVertical = texture.getSprite("slider_track_vertical", 1, 22)
}