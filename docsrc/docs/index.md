# Welcome to the LibrarianLib documentation!

LibrarianLib is your one-stop library. It doesn't supply many specific, user facing features, but what it does do is take nearly all the pain out of modding.

It will:

* Register your blocks, items, and models for you (including automatically generating common and complex models)
* Automatically serialize and sync your tile entities
* Allow your tile entities to provide a capability simply by annotating a field
* Make config files with nothing but a class and some annotations
* Handle reading and writing your packets
* Make GUIs fun again
* Make GUI containers not as painful (and allow slots to disappear and/or move in a GUI)
* Make the gods of Java access modifiers bend to your will (native-speed reflection access. Screw private fields and hacky access transformers!)
* Make creating gorgeous particle effects easy and fun
* Provide tons of Kotlin extension functions for everything under the sun, including vector math operators!

It also has a huge number of small utility classes for:

* Getting information on client ticks and partial ticks: `ClientTickHandler`
* Tell you what modid owns a class: `OwnershipHandler`
* Creating custom registries: `RegistryMod`
* Custom event buses: `EventBus`/`Event`/`EventCancelable`
* Custom forge events for resource pack reloads and for rendering custom "things" in the world: `ResourceReloadEvent`/`CustomWorldRenderEvent`
* Safely accessing item NBT: `ItemNBTHelper`
* Building NBT and JSON objects with a builder pattern in Kotlin: `NBTMaker.kt`/`JsonMaker.kt`
* Doing 2d vector math, 2d bounding box math, and 3d transforms: `Vec2d`/`BoundingBox2D`/`Matrix4`
* Representing curves and other data as a 0-1 time step interpolation functions: `InterpFunction`
* Creating multi block render layer JSON models with the vanilla format. Layers are specified per-face. `LibLibModelBlock`
* Sending non-spamming messages to the player
* **WIP** Making shaders not terrible **Crashes on some machines**: `ShaderHelper`
* Drawing sections of textures without the need for [magic numbers](https://en.wikipedia.org/wiki/Magic_number_(programming)#Unnamed_numerical_constants) in the code, and for animating arbitrary sprites: `Sprite`/`DrawingUtil`
* **WIP** Matching structure files against the world for multiblocks and to allow rendering structure files in a GUI: `Structure`/`InWorldRender`/`StructureRenderUtil`
* Rendering BlockStates to a vertex buffer: `BlockRenderUtils`
* Registering custom sprites to the block texture map: `CustomBlockMapSprites`
* Adding custom F3+key actions: `F3Handler`
* Using an OpenGL scissor test: `ScissorUtil`
* Making item tooltips easier in general: `TooltipHelper`
* Making scanning through an ASMDataTable for annotations easier: `AnnotationHelper`
* Running code only on the client: `ClientRunnable`
* Storing a BlockPos and a dimension together: `DimWithPos`
* Pairs of EnumFacings: `EnumBiFacing`
* Generating blockstate and model JSONs: `JsonGenerationUtils`
* Basic logging: `LoggerBase`
* Profiling code performance: `Profiler`
* Raycasting blocks: `RaycastUtils`
* Using Java's Unsafe class: `UnsafeKt`

## Main Features

* [Docs WIP](feature/autoregistration.md) Automatic registration of blocks, tile entities, items, enchantments, potions, achievements, packets, capabilities, and more in the future!
* [Docs WIP](feature/baseblocks.md) Base block classes for automatic block model registration and generation! This includes base block classes that automatically generate models for over a dozen common block types, such as doors, slabs, stairs, and fences!
* [Docs WIP](feature/baseitems.md) Base item classes for automatic item model registration and generation! This includes base item classes for tools, food, armor, swords, and arrows!

* [Docs WIP](feature/basecap.md)


## Commands

* `mkdocs new [dir-name]` - Create a new project.
* `mkdocs serve` - Start the live-reloading docs server.
* `mkdocs build` - Build the documentation site.
* `mkdocs help` - Print this help message.

## Project layout

    mkdocs.yml    # The configuration file.
    docs/
        index.md  # The documentation homepage.
        ...       # Other markdown pages, images and other files.
