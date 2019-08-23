# LibrarianLib

[![Build Status](https://travis-ci.org/TeamWizardry/LibrarianLib.svg?branch=1.12)](https://travis-ci.org/TeamWizardry/LibrarianLib)

## [Documentation WIP](https://docs.teamwizardry.com/index.php?title=Main_Page)

LibrarianLib is the library mod to end all library mods. It doesn't attempt to do much else than take the pain out of modding. It can handle a great deal of boilerplate code that you really shouldn't have to touch. Its largest components are an automatic serialization system, automatic model creation and registration, a powerful GUI library that takes out the magic numbers and complex control stuctures by making everything modular, and a powerful and flexable particle system.

LibrarianLib depends on [Shadowfacts' Forgelin](https://minecraft.curseforge.com/projects/shadowfacts-forgelin). Make sure you have it if you want to use it.


## Using LibrarianLib as a dev
Adding LibrarianLib to your dev workspace is easy when using gradle (or maven, or any other similar system relying on maven).

All you need to do is to add `http://maven.bluexin.be/repository/snapshots/` as remote maven repository.
For example, in gradle, you can add this :
```groovy
repositories {
  maven {
    url = "http://maven.bluexin.be/repository/snapshots/"
  }
  // Any other repo you may need
}
```

Then use the following artifact reference in your dependencies :

 * group-id: `com.teamwizardry.librarianlib`
 * artifact-id: `librarianlib-$mcversion`
 * version: `$liblibversion-SNAPSHOT`
 * classifier: `deobf` (this will ensure the sources link properly)

For example, this is what a gradle dependency on liblib 4.0 on minecraft 1.12 would look like :
```groovy
dependencies {
  compile "com.teamwizardry.librarianlib:librarianlib-1.12:4.0-SNAPSHOT:deobf"
}
```

## Features
(note: this list was thrown together based on the package list, so it is not exhaustive)

- reflection-based animation
- annotation-based automatic block/item annotation
- base capability implementation
- base blocks/items/entities/fluids/multiparts 
- base tile entities with automatic serialization
- automatic model generation for everything under the sun (plants, fences, doors, you name it)
- per-chunk data storage
- per-world data storage
- annotation-based config handler
- a base container that simplifies shift-clicking to just be `transferRule().from(inventory1).to(inventory2).filter { some item filter }`
- a simplified event bus for liblib's gui framework
- a giant gui framework (from what I can tell, it's one of, if not the, most advanced ones in MC)
- a data-driven guide book implementation using said gui framework
- a massively reworked version of the giant gui framework
- a hud handler using the gui framework, with gui components that are automatically positioned to match every minecraft hud element
- a data-driven guide book implementation using said gui framework
- a set of simple GUI components designed to closely emulate desktop UI, meaning the user doesn't have to guess at how controls might behave (e.g. button, toggle button, spinner button, etc)
- a shit ton of small helpers
- a shit ton of small kotlin-specific helpers
- a bunch of math classes
- a methodhandle helper for native-speed reflective method/field access
- base network implementation with automatic packet serialization
- a powerful but inefficent particle system
- a new, more powerful, and hyper-efficient particle system (I've gotten 20k particles with world collision running on my potato)
- an advanced reflection-based automatic serialization library
- shader helpers
- mcmeta-defined sprites, with 9-slice scaling, tiling, and more, eliminating the need to sprinkle magic constants everywhere in your GUIs
- a structure rendering utility
- a persistent TESR class, meaning you no longer have to track all your TESR parameters in your tile entity
- bitfont, my custom typesetting/text editing engine, along with a massively expanded version of minecraft's font
- a shit ton of utilities
- a hyper-efficient ray-world collision handler, used for the particle system
- a stencil mask utility
- a custom cursor helper
