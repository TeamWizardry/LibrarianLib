# Creating modules

- `build.gradle.kts` - the main module file

# Module structure

### Packages
- `com.teamwizardry.librarianlib.<module id>` - module root
- `com.teamwizardry.librarianlib.<module id>.mixin` - Mixin files
- `com.teamwizardry.librarianlib.<module id>.bridge` - Code bridging Mixins, ASM, and library code. e.g. interfaces
implemented using Mixins or hooks for Mixins/ASM.

### Classes
- `com.teamwizardry.librarianlib.<module id>.LibLib<ModuleName>` - Main module class
- `com.teamwizardry.librarianlib.<module id>.LibLib<ModuleName>.Common`/`Client`/`Server` - The common, client, and 
  server entry points for the module. 

### Resources
- `ll/<module id>/<module id>.mixins.json` – [Mixin configuration files](https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files)
- `ll/<module id>/<module id>-refmap.json` – The location of the generated mixin refmap.
- `ll/<module id>/icon.png` – An optional 128x128px mod icon. The default uses a global liblib icon, so to use a custom
  icon, update the `icon` property in the `configureFabricModJson` block in the `build.gradle.kts` file)

### Test
- `com.teamwizardry.librarianlib.<module id>.test.LibLib<ModuleName>Test` - Main module test class
- `com.teamwizardry.librarianlib.<module id>.test.LibLib<ModuleName>Test.Common`/`Client`/`Server` - The common, client 
  and server points for the test mod.
