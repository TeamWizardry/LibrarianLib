<h1 align="center">
  <br>
    <img src="https://raw.github.com/TeamWizardry/LibrarianLib/1.15/logo/logo_500x500.png" title="LibrarianLib" 
    width="200" height="200" alt="LibrarianLib">
  <br>
  LibrarianLib
  <br>
  <img id="mc-version-shield" src="https://img.shields.io/badge/Minecraft-1.15.2-blue" alt="Minecraft 1.15.2"/>
  <img id="forge-version-shield" src="https://img.shields.io/badge/Forge-31.2.36-blue" alt="Minecraft Forge 31.2.36"/>
  <img id="mcp-mappings-shield" src="https://img.shields.io/badge/MCP-snapshot__20200803--1.15.1-blue" alt="MCP snapshot_20200803-1.15.1"/>
  <img src="https://github.com/TeamWizardry/LibrarianLib/workflows/Publish%20Snapshots/badge.svg?branch=1.15" alt="Publish Snapshots"/>
</h1>

# Using LibrarianLib

Release builds:
```goovy
repositories {
    jcenter()
    maven { url = 'https://jitpack.io' }
    maven { url = 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
    implementation 'com.teamwizardry.librarianlib:librarianlib-(module):(version)'
}
```
Snapshot builds:
```goovy
repositories {
    maven { url = 'https://oss.jfrog.org/artifactory/oss-snapshot-local' }
    maven { url = 'https://jitpack.io' }
    maven { url = 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
    implementation 'com.teamwizardry.librarianlib:librarianlib-(module):(branch)-SNAPSHOT'
}
```

Due to the fact that (as of April 24th 2020) ForgeGradle's `fg.deobf` is broken (it doesn't properly link sources, and 
doesn't support any kind of transitive dependencies), LibrarianLib is distributed in its deobfuscated form. This may 
necessitate matching your project's MCP mappings to LibrarianLib's MCP mappings, which are listed at the top of this
README.

# Modules
LibrarianLib is divided into multiple modules, each with a particular focus. You only need to depend on the modules you
actually want to use, meaning you aren't faced with a monolithic (and potentially overwhelming) library. 

Each module has a test/example mod, located in `modules/<module name>/src/test/*`, which can be a useful reference.

Here's a summary of the modules and what they do. Many modules will have their own readme file located in the 
`modules/<module name>` directory. 

#### Core
- `core` – The core module which contains basic code used by the other modules. For example, it contains most of the 
math code, such as LibLib's vector and matrix classes, easing functions, and abstract animation classes. It also 
contains simple helpers. More details in the core readme.

#### Flagship features
- `foundation` – A set of utilities designed to form the "foundation" of your mod. This module aims to curb the 
ever-increasing amount of boilerplate Forge requires.
- `facade` – A feature-rich, flexible GUI framework.
- `glitter` – High-performance particle systems.

#### Minor/niche features
- `albedo` – Making GLSL shaders simple.
- `courier` — Networking made easy(er).
- `etcetera` – Minor utilities that don't warrant their own modules.
- `mirage` – Create virtual resources (textures/etc.) at runtime. Use this sparingly.
- `mosaic` – Data-driven spritesheets, designed for Facade.
- `prism` – Automatic, annotation-driven serialization.
- `testbase` – An internal module which isn't published, testbase is specifically designed to make testing the other 
modules easier, meaning tests will be more likely to be written.

# Contributing

This hasn't been ironed out completely, but see the CONTRIBUTING.md file
