package com.teamwizardry.librarianlib.features.neogui.component.supporting

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.component.LayerHierarchyException

interface IComponentFocus {
    /**
     * Set to true if the component is currently focused
     */
    val isFocused: Boolean

    /**
     * Requests that this component be made the focused component.
     *
     * If this component can be focused ([RequestFocusEvent.allow][GuiComponentEvents.RequestFocusEvent.allow] == true)
     * and the currently focused component blurs successfully ([requestBlur])
     * this component will become the focused component then will fire a [FocusEvent][GuiComponentEvents.FocusEvent]
     *
     * If this component is the focused component, this method does nothing and returns true.
     *
     * @return true if this component successfully acquired focus.
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun requestFocus(): Boolean

    /**
     * Forces this component to be made the focused component.
     *
     * The current focused component will be forced to blur ([forceBlur]) then
     * this component will become the focused component then will fire a [FocusEvent][GuiComponentEvents.FocusEvent]
     *
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun forceFocus()

    /**
     * Requests that this component release its focus.
     *
     * If this component can release its focus ([RequestBlurEvent.allow][GuiComponentEvents.RequestBlurEvent.allow] == true)
     * this component will relinquish its focused status then will fire a [BlurEvent][GuiComponentEvents.BlurEvent]
     *
     * If this component isn't the focused component, this method does nothing and returns true.
     *
     * @return true if this component successfully released focus.
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun requestBlur(): Boolean

    /**
     * Forces this component to release its focus.
     *
     * This component will relinquish its focused status then will fire a [BlurEvent][GuiComponentEvents.BlurEvent]
     *
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun forceBlur()

    /**
     * Requests that this component be focused or blurred depending on the value of the [focused] parameter.
     *
     * This is effectively shorthand for `if(focused) requestFocus() else requestBlur()`
     *
     * @return true if this component successfully acquired or released focus, depending on the [focused] parameter
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun requestFocusedState(focused: Boolean): Boolean

    /**
     * Forces that this component to be focused or blurred depending on the value of the [focused] parameter.
     *
     * This is effectively shorthand for `if(focused) forceFocus() else forceBlur()`
     * @throws LayerHierarchyException if this component isn't contained within a GUI (i.e. when [GuiComponent.gui] == null)
     */
    fun forceFocusedState(focused: Boolean)
}

class ComponentFocusHandler: IComponentFocus {
    lateinit var component: GuiComponent

    override val isFocused: Boolean
        get() = component.gui?.focusedComponent === component

    override fun requestFocus(): Boolean {
        val gui = component.gui ?: throw LayerHierarchyException("Component not contained within GUI")
        if(component.isFocused) return true

        val focusRequest = GuiComponentEvents.RequestFocusEvent()
        component.BUS.fire(focusRequest)

        if(focusRequest.allow) {
            if (gui.focusedComponent?.requestBlur() == false) return false
            gui.focusedComponent = component
            component.BUS.fire(GuiComponentEvents.FocusEvent())
            return true
        } else {
            return false
        }
    }

    override fun forceFocus() {
        val gui = component.gui ?: throw LayerHierarchyException("Component not contained within GUI")
        if(component.isFocused) return
        gui.focusedComponent?.forceBlur()
        gui.focusedComponent = component
        component.BUS.fire(GuiComponentEvents.FocusEvent())
    }

    override fun requestBlur(): Boolean {
        val gui = component.gui ?: throw LayerHierarchyException("Component not contained within GUI")
        if(!component.isFocused) return true
        val blurRequest = GuiComponentEvents.RequestBlurEvent()
        component.BUS.fire(blurRequest)

        if(blurRequest.allow) {
            gui.focusedComponent = null
            component.BUS.fire(GuiComponentEvents.BlurEvent())
            return true
        } else {
            return false
        }
    }

    override fun forceBlur() {
        val gui = component.gui ?: throw LayerHierarchyException("Component not contained within GUI")
        if(!component.isFocused) return
        gui.focusedComponent = null
        component.BUS.fire(GuiComponentEvents.BlurEvent())
    }

    override fun requestFocusedState(focused: Boolean): Boolean {
        return if(focused)
            requestFocus()
        else
            requestBlur()
    }

    override fun forceFocusedState(focused: Boolean) {
        if(focused)
            forceFocus()
        else
            forceBlur()
    }
}
