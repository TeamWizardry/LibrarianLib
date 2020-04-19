## Module metadata
Module metadata, such as the main module class, is contained in the `META-INF/modules/<module id>.json` file:
```json
{
  "mainClass": "com.teamwizardry.librarianlib.<module id>.Lib<Module Name>Module"
}
```

## Temporary skeleton mod
Each module includes a skeleton mod. This mod exists largely to allow core mods to be loaded when the modules are 
separate. The modules themselves are actually loaded by the core module's bootstrapper, not by the skeleton mod, so 
modules should assume their mod ID is `librarianlib`. For every module except the `core` module, the gradle script 
automatically generates each module's skeleton class, mixin connector, `mods.toml` file, and `pack.mcmeta` file during 
the build process. 

## Dependencies
### Mods
Dependencies on other mods should be added to the `META-INF/dependencies.toml` file in each module. The contents of this 
file will be appended to the core bootstrapper's `mods.toml` file. The actual mod jars should be added to the `mod` 
dependency configuration. Any artifacts in this configuration will be copied into the runtime `mods` directory.

### Libraries
Non-mod libraries should be added to the `shade` dependency configuration, which also adds them to the `api` 
configuration. Anything not provided by another mod _must_ be shaded in order to function. These libraries will be 
shaded into the module build output at runtime since loading libraries from the root classloader can lead to 
[classloading issues](#shading-at-runtime). Every shaded package must also be added to the list of packages to 
relocate by passing them to the `shadePackages` function. When adding or updating a shaded library, make sure to check
what transitive dependencies it has and [exclude](https://docs.gradle.org/current/userguide/dependency_downgrade_and_exclude.html#sec:excluding-transitive-deps)
any that aren't needed. For example, a library that depends on the Kotlin stdlib will try to shade the entire stdlib,
which is already being provided by Kottle. 

#### <a name="shading-at-runtime"></a>Why shade at runtime?
For example, if a mod such as Kottle is placed on the classpath, it'll get lifted into the mod classloader. Then, when 
mods use Kotlin types they'll reference the Kottle classes in the mod classloader, but when non-mod libraries in the 
root classloader use Kotlin types they'll reference the Kottle classes that are still in the root classloader. Moving 
Kottle out of the root classloader avoids the duplicates, but now the libraries that are still stuck down there can't
access Kotlin types. Thus, we shade all the dependencies into the runtime environment.

## Building

more to come…

# OLD:

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

## Library dependencies
Any external non-mod dependencies that are required in the runtime classpath should be added to the `shade` 
configuration, since anything that isn't a mod dependency (e.g. Kottle) we have to package ourselves. Adding to this
configuration also shades for runtime and for maven publishing, which resolves the fact that libraries in the classpath 
can't access loaded mods, and if the mods are in the classpath the libraries will reference the wrong copy of the mod.
(They reference the wrong copy since forge lifts a copy of any classpath mods into its own classloader, which is where 
our mod lives. Our mod then references Kottle from the forge classloader, while the library in the classpath references 
Kottle from the main classloader.) `shade` is implicitly also `api` to reflect the fact that in the end the code is 
going to be freely available as part of the maven jar.

Because `shade` includes transitive dependencies, it is vitally important after adding or updating a `shade` dependency 
to check for and exclude any superfluous transitive dependencies (e.g. the Kotlin stdlib). The packages to be 
[relocated](https://imperceptiblethoughts.com/shadow/configuration/relocation/) should be configured by passing them to 
`shadePackages`. These packages will then automatically be moved under the `ll` package (e.g. `org.msgpack` -> 
`ll.org.msgpack`).

The [shadow](https://github.com/johnrengelman/shadow) plugin's relocator is applied to both the maven and final jars. 
Note however that _only_ the relocator is applied, do any dependencies added to the `shadow` configuration or 
`shadowJar` task will not be added to the maven or final jars. The system operates this way because the `shade` 
dependencies are already present in the build output due to the runtime shading process. 

## Mod dependencies
Mod dependencies retrieved from Maven should be added to the `mod` configuration, which at build time will be copied 
into Minecraft's mods directory. Any plain mod jars should be placed in `runtime/mods`, which will also be copied into 
Minecraft's mods directory. Any mod files added directly to Minecraft's mods directory will be deleted during the next 
build.

External mod dependencies should be added to the module's `META-INF/dependencies.toml` file, which will be directly
appended to the generated `mods.toml` file. However, the `dependencies.toml` file should use `MOD_ID` instead of the 
actual mod ID, since the dependencies may be appended to either the module or the merged `mods.toml`, which means its
mod ID may be either `librarianlib-<module id>` or just `librarianlib`, depending on where it's being used.

## Module `gradle.properties`
Each module's `gradle.properties` has some standard properties (these must be defined in the `gradle.properties`, not
at runtime):
- `human_name`: The name of the module, as opposed to the module's ID. e.g. the `virtualresources` module's name would 
be `Virtual Resources` 
- `module_description`: A short description of the module
- `maven_developers`: An optional list of developers to be added to the maven POM file. The list consists of a series of
comma-separated developers, with three fields separated by colons: `id:name:email`. Any empty fields or trailing fields
that are omitted will not be added, e.g. `id:name`, `:name:email`

