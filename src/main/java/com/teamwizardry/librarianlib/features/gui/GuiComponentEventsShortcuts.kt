package com.teamwizardry.librarianlib.features.gui

/**
 * TODO: Document file GuiComponentEventsShortcuts
 *
 * Created by TheCodeWarrior
 */

class GuiComponentEventsShortcuts internal constructor() {
    @JvmField val ComponentTickEvent = GuiComponent.ComponentTickEvent::class.java

    @JvmField val PreDrawEvent = GuiComponent.PreDrawEvent::class.java
    @JvmField val PostDrawEvent = GuiComponent.PostDrawEvent::class.java
    @JvmField val PreChildrenDrawEvent = GuiComponent.PreChildrenDrawEvent::class.java

    @JvmField val MouseDownEvent = GuiComponent.MouseDownEvent::class.java
    @JvmField val MouseUpEvent = GuiComponent.MouseUpEvent::class.java
    @JvmField val MouseDragEvent = GuiComponent.MouseDragEvent::class.java
    @JvmField val MouseClickEvent = GuiComponent.MouseClickEvent::class.java

    @JvmField val KeyDownEvent = GuiComponent.KeyDownEvent::class.java
    @JvmField val KeyUpEvent = GuiComponent.KeyUpEvent::class.java

    @JvmField val MouseInEvent = GuiComponent.MouseInEvent::class.java
    @JvmField val MouseOutEvent = GuiComponent.MouseOutEvent::class.java
    @JvmField val MouseWheelEvent = GuiComponent.MouseWheelEvent::class.java

    @JvmField val FocusEvent = GuiComponent.FocusEvent::class.java
    @JvmField val BlurEvent = GuiComponent.BlurEvent::class.java
    @JvmField val EnableEvent = GuiComponent.EnableEvent::class.java
    @JvmField val DisableEvent = GuiComponent.DisableEvent::class.java

    @JvmField val AddChildEvent = GuiComponent.AddChildEvent::class.java
    @JvmField val RemoveChildEvent = GuiComponent.RemoveChildEvent::class.java
    @JvmField val AddToParentEvent = GuiComponent.AddToParentEvent::class.java
    @JvmField val RemoveFromParentEvent = GuiComponent.RemoveFromParentEvent::class.java

    @JvmField val SetDataEvent = GuiComponent.SetDataEvent::class.java
    @JvmField val RemoveDataEvent = GuiComponent.RemoveDataEvent::class.java
    @JvmField val GetDataEvent = GuiComponent.GetDataEvent::class.java
    @JvmField val GetDataKeysEvent = GuiComponent.GetDataKeysEvent::class.java
    @JvmField val GetDataClassesEvent = GuiComponent.GetDataClassesEvent::class.java

    @JvmField val HasTagEvent = GuiComponent.HasTagEvent::class.java
    @JvmField val AddTagEvent = GuiComponent.AddTagEvent::class.java
    @JvmField val RemoveTagEvent = GuiComponent.RemoveTagEvent::class.java

    @JvmField val LogicalSizeEvent = GuiComponent.LogicalSizeEvent::class.java
    @JvmField val MouseOverEvent = GuiComponent.MouseOverEvent::class.java

    @JvmField val MessageArriveEvent = GuiComponent.MessageArriveEvent::class.java
}
