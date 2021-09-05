<h1 align="center">
  <br>
    <img src="https://raw.github.com/TeamWizardry/LibrarianLib/5c982d5/logo/logo_500x500.png" title="LibrarianLib" 
    width="200" height="200" alt="LibrarianLib">
  <br>
  LibrarianLib
  <br>
  <a href="https://www.curseforge.com/minecraft/mc-mods/librarianlib"><img src="https://img.shields.io/badge/Download-CurseForge-f16436" alt="CurseForge"/></a>
  <a href="https://modrinth.com/mod/librarianlib"><img src="https://img.shields.io/badge/Download-Modrinth-5da426" alt="Modrinth"/></a>
  <br>
  <img id="mc-version-badge" src="https://img.shields.io/badge/Minecraft-1.17.1-blue" alt="Minecraft 1.17.1"/>
  <img id="mod-version-badge" src="https://img.shields.io/badge/LibrarianLib-5.0.0--alpha.7-blue" alt="LibrarianLib 5.0.0-alpha.7"/>
</h1>

# Using LibrarianLib

Release builds:
```goovy
repositories {
    mavenCentral()
}
dependencies {
    modImplementation "com.teamwizardry.librarianlib:<module>:<version>"
}
```
Snapshot builds:
```goovy
repositories {
    maven { url = "https://s01.oss.sonatype.org/content/repositories/snapshots" }
}
dependencies {
    modImplementation "com.teamwizardry.librarianlib:<module>:<branch>-SNAPSHOT"
}
```


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
- `facade` – A feature-rich, flexible GUI framework.
- `glitter` – High-performance particle systems.

#### Minor features
- `albedo` – Making GLSL shaders simple.
- `courier` — Networking made easy(er).
- `etcetera` – Minor utilities that don't warrant their own modules.
- `mosaic` – Data-driven spritesheets, designed for Facade.
- `scribe` – Automatic, annotation-driven serialization.

# Contributing

This hasn't been ironed out completely, but see the CONTRIBUTING.md file
