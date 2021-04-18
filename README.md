<h1 align="center">
  <br>
    <img src="https://raw.github.com/TeamWizardry/LibrarianLib/1.15/logo/logo_500x500.png" title="LibrarianLib" 
    width="200" height="200" alt="LibrarianLib">
  <br>
  LibrarianLib
  <br>
  <img id="mc-version-badge" src="https://img.shields.io/badge/Minecraft-1.16.4-blue" alt="Minecraft 1.16.4"/>
  <img id="forge-version-badge" src="https://img.shields.io/badge/Forge-35.1.37-blue" alt="Minecraft Forge 35.1.37"/>
  <img id="mcp-mappings-badge" src="https://img.shields.io/badge/MCP-snapshot__20201028--1.16.3-blue" alt="MCP snapshot_20201028-1.16.3"/>
  <img src="https://github.com/TeamWizardry/LibrarianLib/workflows/Publish%20Snapshots/badge.svg?branch=1.15" alt="Publish Snapshots"/>
</h1>

# Using LibrarianLib

Note: LibrarianLib uses Gradle's [variant model](https://docs.gradle.org/current/userguide/variant_model.html), which 
isn't supported by the default version of Gradle that Forge ships with. Gradle 6.8.3 has been confirmed working, and 
supports the variant model. To update the Gradle wrapper run this command:
```shell
./gradlew wrapper --gradle-version 6.8.3
```

Release builds:
```goovy
repositories {
    mavenCentral()
    maven { url = 'https://thedarkcolour.github.io/KotlinForForge/' }
}

dependencies {
    implementation 'com.teamwizardry.librarianlib:(module):(version)'
}
```
Snapshot builds:
```goovy
repositories {
    mavenCentral()
    maven { url = 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
    maven { url = 'https://thedarkcolour.github.io/KotlinForForge/' }
}

dependencies {
    implementation 'com.teamwizardry.librarianlib:(module):(branch)-SNAPSHOT'
}
```

Due to the fact that (as of April 24th 2020) ForgeGradle's `fg.deobf` is broken (it doesn't properly link sources, 
doesn't support any kind of transitive dependencies, and I would be shocked if it supported Gradle's variant model), 
LibrarianLib is distributed in its deobfuscated form. This may necessitate matching your project's MCP mappings to 
LibrarianLib's MCP mappings, which are listed at the top of this README.

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
- `lieutenant` – Client-side commands. Many thanks to [Cotton Client Commands](https://github.com/CottonMC/ClientCommands)
  for providing an excellent reference implementation. (Lieutenant is a nearly direct port of Cotton Client Commands)
- `mirage` – Create virtual resources (textures/etc.) at runtime. Use this sparingly.
- `mosaic` – Data-driven spritesheets, designed for Facade.
- `scribe` – Automatic, annotation-driven serialization.

# Contributing

This hasn't been ironed out completely, but see the CONTRIBUTING.md file
