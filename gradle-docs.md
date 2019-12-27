# Modules
When creating the final jar, each module's classes and resources are merged. This means that to prevent conflicts 
modules should reside entirely in their own package, `com.teamwizardry.librarianlib.<module id>`. When merging resources
the `mods.toml`, `dependencies.toml`, and `coremods.json` files are excluded, as they require special processing to be
merged. The one exception is the `core` module, which contains the main `mods.toml` file.

## Module metadata
Module metadata, such as the main module class, is contained in the `META-INF/modules/<module id>.json` file:
```json
{
  "mainClass": "com.teamwizardry.librarianlib.<module id>.Lib<Module Name>Module"
}
```

## Temporary skeleton mod
Each module includes a skeleton mod. This mod is an empty class called `LibModuleSkeleton` with the mod ID 
`librarianlib-<module id>`. This mod exists purely to allow core mods to be loaded when the modules are bundled 
separately, so modules should assume their actual mod ID is `librarianlib`. When the merged mod jar is being created, 
the `LibModuleSkeleton` class is excluded, along with its accompanying `mods.toml` file. With the exception of the 
`core` module, which contains the final `mods.toml` file, the `mods.toml` and `pack.mcmeta` files for every module are 
generated at compile time. Inter-module dependencies are automatically added to this `mods.toml` file, and additional 
dependencies (i.e. optional dependencies on outside mods) are read from `META-INF/dependencies.toml` and appended to it.

## Test mods 
Test mods are each separate mods with the mod ID `librarianlib-<module id>-test`, primarily to avoid item/block ID 
conflicts, and have their `mods.toml` file generated at compile time, similarly to the module skeleton mods. Because 
they register blocks and items, test mods need to be loaded by forge, not the `core` module, so the modules themselves 
should have the `@Mod` annotation.

## Core mods
Core mods for modules are defined like usual, except that their transformer names should be prefixed with 
`<module id>.`, the transformers should be in `META-INF/transformers/<module id>/...` to prevent potential conflicts, 
and their `coremods.json` file _must_ have the braces (`{}`) on their own line. When the merged jar is created each 
module's `coremods.json` file has its braces stripped and are joined together (adding trailing commas to the last lines)
into the final mod.

## Maven repositories
At the moment maven repositories must be added to the `allprojects` block in the root `build.gradle`, since the 
`:runtime` project needs to be able to resolve them. This restriction may be lifted in the future however.

## External dependencies
External mod dependencies should be added to the module's `META-INF/dependencies.toml` file, which will be directly
appended to the generated `mods.toml` file. However, the `dependencies.toml` file should use `MOD_ID` instead of the 
actual mod ID, since the dependencies may be appended to either the module or the merged `mods.toml`, which means its
mod ID may be either `librarianlib-<module id>` or just `librarianlib`, depending on where it's being used.
