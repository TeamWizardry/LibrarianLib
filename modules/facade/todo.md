# TODO
- Keyboard shortcut helpers
- Horizontal scrolling (Minecraft doesn't send that data to the GUI, it'll need a mixin or asm)
- Component focus and accessibility
- Sprite pixel snapping
- Components:
  - Gradient
  - Recipe?
  - Structure
  - Text field (using bitfont)
  - More?
- Pastry
- Guide book
- Bitfont (new typesetter)
  - Strikethrough
  - Bold (+1px character spacing and either rendering twice shifted, or a second, auto-generated, font)
  - Italic (just shear characters)
  - Load fonts up front to avoid stalling when first using them
- Add at least a shred of support for Minecraft's anemic input system (mostly the "swallowed" boolean return)
- Containers
  - Slot component
    
# Containers
- slot visibility

## Compatibility
- Just Enough Items
  - ~~ghost slots~~
  - ~~exclusion areas~~
  - clickable recipe areas?
  - facade recipe category renderer?
  - ~~getIngredientUnderMouse~~
  - recipe transfer handler?
  - ~~fix focus (`a` to bookmark doesn't work) (maybe vanilla focus issue?)~~
  -
- MouseTweaks: https://github.com/YaLTeR/MouseTweaks/blob/master/src/api/java/yalter/mousetweaks/api/IMTModGuiContainer3Ex.java
  - MT_getSlots, MT_getSlotUnderMouse, MT_isCraftingOutput, MT_isIgnored
- InventorySorter: https://github.com/cpw/inventorysorter/blob/master/src/main/java/cpw/mods/inventorysorter/InventorySorter.java
  - IMC: `"slotblacklist"` -> slot class name
  - IMC: `"containerblacklist"` -> Identifier
- Quark search bar?
-
-
