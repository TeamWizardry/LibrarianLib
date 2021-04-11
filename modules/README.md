# Creating modules

- `build.gradle.kts` - the main module file

# Module structure

### Packages
- `com.teamwizardry.librarianlib.<module id>` - module root
- `com.teamwizardry.librarianlib.<module id>.mixin` - Mixin files
- `com.teamwizardry.librarianlib.<module id>.bridge` - Code bridging Mixins, ASM, and library code. e.g. interfaces
implemented using Mixins or hooks for Mixins/ASM.

### Classes
- `com.teamwizardry.librarianlib.<module id>.LibrarianLib<ModuleName>Module` - Main module class

### Resources
- `META-INF/ll/<module id>/module.json` - Module metadata
- `META-INF/ll/<module id>/mixins/*.mixins.json` - [Mixin configuration files](https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment#mixin-configuration-files)
- `META-INF/ll/<module id>/asm/*.asm.js` - Forge coremods. The `coremods.json` file will be automatically generated. To 
avoid conflicts, transformer names should use this format: `ll.<module id>.<transformer name>`
    
