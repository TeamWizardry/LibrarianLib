# TODO
- zIndex
- Keyboard shortcut helpers
- Yoga layout
- Stencil clipping ([Forge PR](https://github.com/MinecraftForge/MinecraftForge/pull/6543))
- Render-to-texture (far future)
  - LibLib shader module?
- Cursors
- Component focus and accessibility
- Animation
- Tooltips (requires pastry tooltips)
- Sprite pixel snapping
- Components:
  - Flat color
  - Gradient
  - Arc
  - Gauge
  - Fluid sprite/gauge
  - Itemstack
  - Recipe?
  - Structure
  - Text field (using bitfont)
  - More?
- Pastry
- Guide book
- Bitfont (new typesetter)
- Containers
  - Slot component
  - JEI/etc. compatibility
  - Research other things I need to do to make containers

After first skimming over the Minecraft GUI changes I planned on adding a lot of compatibility for the built-in widget
system, seeing as it looked much more decent than the previous system. Turns out I was wrong, because widgets aren't
rendered unless the GUI specifically does so, and Minecraft's default GUI input system is shockingly dumb. For example, 
every widget gets mouse down events, but only the one the mouse is currently over receives mouse up events.
