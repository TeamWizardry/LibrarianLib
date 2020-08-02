## Module metadata
Module metadata, such as the main module class, is contained in the `META-INF/librarianlib/modules/<module id>.json` file:
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

## Test mods 
Test mods are each separate mods with the mod ID `librarianlib-<module id>-test`, primarily to avoid item/block ID 
conflicts, and have their `mods.toml` file generated at compile time, similarly to the module skeleton mods. Because 
they register blocks and items, test mods need to be loaded by Forge, not the core module, so the modules themselves 
should have the `@Mod` annotation. The test mods load only after all the modules have loaded.

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

Relocation rules are applied globally, so it's important to verify that the relocated packages aren't used from 
Minecraft elsewhere. For example, if Minecraft includes library `X` and module `A` shades its own copy of `X`, then 
when an unrelated module `B` refers to _Minecraft's_ copy of library `X`, the references wind up pointing to `ll.X`, 
which is missing at runtime when module `A` isn't present.

#### <a name="shading-at-runtime"></a>Why shade at runtime?
The rationale behind shading at runtime was an issue that occurred when using the fat Kottle jar (as opposed to the slim
jar, which doesn't include the kotlin runtime):

For example, if a mod such as Kottle is placed on the classpath, it'll get lifted into the mod classloader. Then, when 
mods use Kotlin types they'll reference the Kottle classes in the mod classloader, but when non-mod libraries in the 
root classloader use Kotlin types they'll reference the Kottle classes that are still in the root classloader. Moving 
Kottle out of the root classloader avoids the duplicates, but now the libraries that are still stuck down there can't
access Kotlin types. Thus, we shade all the dependencies into the runtime environment.

After switching to the slim Kottle jar that should no longer be an issue, however the runtime shading will hopefully 
avoid similar issues in the future, and I've spent over a week working on this gradle setup, so I really don't feel like 
ripping it back out again.

## Maven repositories
Additional Maven repositories must be added to the `allprojects` block in the root `build.gradle`, since all the 
projects need to be able to resolve them.

## META-INF
There are a number of files located in the `META-INF` directory, and where possible, to avoid conflicts, these are 
located in the `META-INF/ll/<module id>` directory. 

## Core mods
Core mods for modules are defined as usual, except that the transformer names should be prefixed with `<module id>.`, 
and the transformer javascript files should be placed in `META-INF/ll/<module id>/asm/` to prevent potential 
conflicts. Due to the jar merging process, the `coremods.json` file _must_ have the braces (`{}`) on their own lines.

## Mixins
Each module's mixin JSON files should be placed in the `META-INF/ll/<module id>/mixin/` directory. The contents of this
directory will automatically be scanned and added to the generated mixin connector.

## Building

![Module build process flow chart](https://raw.github.com/TeamWizardry/LibrarianLib/1.15/gradle/module_build.png)

## Publishing
https://github.com/bintray/gradle-bintray-plugin

### New Release
- Bump the version in `gradle.properties`
- Create a new commit with *only* that change, with the message `Publish v<version>`
- Create a new tag named `v<version>` on that commit
- Push to GitHub

### New Module
New packages must first have a release build published to Bintray, which only allows release builds. Once the package is
on Bintray, it must be approved for snapshots on oss.jfrog.org. 

### Snapshots
Snapshots are automatically created when pushing to any branch that starts with `1.15`.

## TODO
https://github.com/Kotlin/binary-compatibility-validator
Nail down publishing to jfrog and then applying for oss.jfrog.org
Fix race condition in build script (downloadMcpConfig occasionally fails, saying `Cannot get property 'outputs' on null object`)

