# Behavior changes
1. GUI input methods now seem to need to know if the event was consumed. For now it will always be consumed, but this 
should be investigated. Full support for `IGuiEventListener` would be ideal. 
2. Minecraft's GUIs look like they might have a lot more screen reader integration now. If at all possible we should
be compatible with that.
3. Minecraft's GUI nesting system is much more flexible now. We should make LibLib GUIs compatible with the MC api so 
mods that insert UI elements will work well with LibLib's interfaces. This would entail:
   - Correctly rendering nested gui elements
   - Reporting when the mouse is hovering over a liblib component, as well as occluding when the mouse is over an 
     external UI element
   - Sending appropriate events to the external elements
   
# Updates
1. The old `updateScreen` method has been renamed to `tick`, revealing its true nature! I was under the impression it 
ran before each frame, but in fact it only runs once per tick. This means the update event needs to be fixed, both here 
and in 1.12
2. LWJGL 3.+ doesn't have the Keyboard class any more, it uses GLFW directly. Lots and lots of code will need to be 
updated for this.

# Other
1. Add rotation/scale methods to Matrix4d and Matrix3d
2. Add sprite system
3. Add event bus

