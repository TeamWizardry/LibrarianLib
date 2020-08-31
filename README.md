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

## Using LibrarianLib

Release builds:
```goovy
repositories {
    jcenter()
    maven { url = 'https://jitpack.io' }
    maven { url = 'https://minecraft.curseforge.com/api/maven/' }
}

dependencies {
    compile 'com.teamwizardry.librarianlib:librarianlib-etcetera:(version)'
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
    compile 'com.teamwizardry.librarianlib:librarianlib-etcetera:(branch)-SNAPSHOT'
}
```

Due to the fact that (as of April 24th 2020) ForgeGradle's `fg.deobf` is broken (it doesn't properly link sources, and 
doesn't support any kind of transitive dependencies), LibrarianLib is distributed in its deobfuscated form. This may 
necessitate matching your project's MCP mappings to LibrarianLib's MCP mappings, which are listed at the top of this
README.

## Contributing

Haven't gotten this procedure ironed out yet.

### Running LibrarianLib
To set up the run configuration for LibrarianLib, run the `genIntellijRuns` or `genEclipseRuns` for the `runtime` 
subproject. 

### Enabling debug logging
To enable debug logging for modules add their comma-separated names to the `librarianlib.debug.modules` system property.
For example, to enable debug logging for the bootstrapper and the foundation module, you would add 
`-Dlibrarianlib.debug.modules=bootstrap,foundation` to the VM options.
