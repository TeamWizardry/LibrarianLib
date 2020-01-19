# TODO

## Behavior changes
1. GUI input methods now seem to need to know if the event was consumed. For now it will always be consumed, but this 
should be investigated. Full support for `IGuiEventListener` would be ideal. 
1. Minecraft's GUIs look like they might have a lot more screen reader integration now. If at all possible we should
be compatible with that.
1. Minecraft's GUI nesting system is much more flexible now. We should make LibLib GUIs compatible with the MC api so 
mods that insert UI elements will work well with LibLib's interfaces. This would entail:
   - Correctly rendering nested gui elements
   - Reporting when the mouse is hovering over a liblib component, as well as occluding when the mouse is over an 
     external UI element
   - Sending appropriate events to the external elements
1. Look into the popular trickle-down bubble-up event system, and see if it would fit in liblib (changing mouse events 
yet again)
   
## Updates
1. LWJGL 3.+ doesn't have the old input systems any more, it uses GLFW directly. Lots and lots of code will need to be 
updated for this.

## Other
1. Add rotation/scale methods to Matrix4d and Matrix3d
1. ~~Add sprite system~~ Done.
1. Add event bus
1. Add animation/easing system
1. Add shaders for render-to-texture
1. Add stencil utility
1. Add cursor utility
