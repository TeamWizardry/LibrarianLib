package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.sprite.Texture

object PastryTexture {
    val texture = Texture("librarianlib:textures/gui/pastry/components.png".toRl(), 256, 256)

    val background = texture.getSprite("background")
    val textField = texture.getSprite("text_field")
    val button = texture.getSprite("button")
    val buttonPressed = texture.getSprite("button_pressed")
    val optionSpinner = texture.getSprite("option_spinner")
    val scrollbarTrackVertical = texture.getSprite("scrollbar_track_vertical")
    val scrollbarTrackHorizontal = texture.getSprite("scrollbar_track_horizontal")
    val scrollbarHandleVertical = texture.getSprite("scrollbar_handle_vertical")
    val scrollbarHandleHorizontal = texture.getSprite("scrollbar_handle_horizontal")
    val radialSlider = texture.getSprite("radial_slider")
    val radialSliderPointer = texture.getSprite("radial_slider_pointer")
    val checkboxOff = texture.getSprite("checkbox_off")
    val checkboxOn = texture.getSprite("checkbox_on")
    val switchOff = texture.getSprite("switch_off")
    val switchOn = texture.getSprite("switch_on")
    val radioOff = texture.getSprite("radio_off")
    val radioOn = texture.getSprite("radio_on")
    val stepperFrame = texture.getSprite("stepper_frame")
    val stepperPlus = texture.getSprite("stepper_plus")
    val stepperPlusPressed = texture.getSprite("stepper_plus_pressed")
    val stepperMinus = texture.getSprite("stepper_minus")
    val stepperMinusPressed = texture.getSprite("stepper_minus_pressed")
    val sliderHandle = texture.getSprite("slider_handle")
    val sliderHandleDown = texture.getSprite("slider_handle_down")
    val sliderHandleUp = texture.getSprite("slider_handle_up")
    val sliderHandleLeft = texture.getSprite("slider_handle_left")
    val sliderHandleRight = texture.getSprite("slider_handle_right")
    val sliderTickLeft = texture.getSprite("slider_tick_left")
    val sliderTickRight = texture.getSprite("slider_tick_right")
    val sliderTickTop = texture.getSprite("slider_tick_top")
    val sliderTickBottom = texture.getSprite("slider_tick_bottom")
    val sliderTrackHorizontal = texture.getSprite("slider_track_horizontal")
    val sliderTrackVertical = texture.getSprite("slider_track_vertical")
}